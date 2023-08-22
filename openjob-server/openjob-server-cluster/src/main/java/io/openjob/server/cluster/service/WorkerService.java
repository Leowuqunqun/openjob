package io.openjob.server.cluster.service;

import com.google.common.collect.Lists;
import io.openjob.common.OpenjobSpringContext;
import io.openjob.common.constant.CommonConstant;
import io.openjob.common.request.WorkerStartRequest;
import io.openjob.common.request.WorkerStopRequest;
import io.openjob.common.response.ServerWorkerStartResponse;
import io.openjob.common.util.DateUtil;
import io.openjob.server.cluster.autoconfigure.ClusterProperties;
import io.openjob.server.cluster.dto.WorkerFailDTO;
import io.openjob.server.cluster.dto.WorkerJoinDTO;
import io.openjob.server.cluster.data.RefreshData;
import io.openjob.server.cluster.util.ClusterUtil;
import io.openjob.server.common.ClusterContext;
import io.openjob.server.common.util.SlotsUtil;
import io.openjob.server.repository.constant.WorkerStatusEnum;
import io.openjob.server.repository.dao.AppDAO;
import io.openjob.server.repository.dao.WorkerDAO;
import io.openjob.server.repository.entity.App;
import io.openjob.server.repository.entity.Worker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Service
@Log4j2
public class WorkerService {
    private final WorkerDAO workerDAO;
    private final AppDAO appDAO;
    private final ClusterProperties clusterProperties;
    private final RefreshData refreshManager;

    @Autowired
    public WorkerService(WorkerDAO workerDAO, AppDAO appDAO, ClusterProperties clusterProperties, RefreshData refreshManager) {
        this.workerDAO = workerDAO;
        this.appDAO = appDAO;
        this.clusterProperties = clusterProperties;
        this.refreshManager = refreshManager;
    }

    /**
     * Worker start
     *
     * @param startRequest start request.
     */
    public ServerWorkerStartResponse workerStart(WorkerStartRequest startRequest) {
        // Check app name.
        App app = this.checkAppName(startRequest.getAppName());

        // Do worker start.
        OpenjobSpringContext.getBean(this.getClass()).doWorkerStart(startRequest, app);

        // Akka message for worker start.
        WorkerJoinDTO workerJoinDTO = new WorkerJoinDTO();
        workerJoinDTO.setClusterVersion(ClusterContext.getSystem().getClusterVersion());
        ClusterUtil.sendMessage(workerJoinDTO, ClusterContext.getCurrentNode(), this.clusterProperties.getSpreadSize(), new HashSet<>());

        // Online workers  and exclude start worker.
        Set<String> onlineWorkers = ClusterUtil.getOnlineWorkers(app.getId());

        // Response
        ServerWorkerStartResponse response = new ServerWorkerStartResponse();
        response.setAppId(app.getId());
        response.setAppName(app.getName());
        response.setWorkerAddressList(onlineWorkers);
        return response;
    }

    /**
     * Do worker start.
     *
     * @param workerStartRequest workerStartRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public void doWorkerStart(WorkerStartRequest workerStartRequest, App app) {
        // Refresh system.
        // Lock system cluster version.
        this.refreshManager.refreshSystem(true);

        // Update worker status.
        this.updateWorkerForStart(workerStartRequest, app);

        // Refresh cluster context.
        refreshClusterContext();
    }

    /**
     * Worker stop
     *
     * @param stopReq stop request.
     */
    public void workerStop(WorkerStopRequest stopReq) {
        // Check app name.
        this.checkAppName(stopReq.getAppName());

        // Do worker stop.
        OpenjobSpringContext.getBean(this.getClass()).doWorkerStop(stopReq);

        // Akka message for worker start.
        WorkerFailDTO workerFailDTO = new WorkerFailDTO();
        workerFailDTO.setClusterVersion(ClusterContext.getSystem().getClusterVersion());
        ClusterUtil.sendMessage(workerFailDTO, ClusterContext.getCurrentNode(), this.clusterProperties.getSpreadSize(), new HashSet<>());
    }

    /**
     * Do worker stop.
     *
     * @param stopReq stopReq
     */
    @Transactional(rollbackFor = Exception.class)
    public void doWorkerStop(WorkerStopRequest stopReq) {
        // Refresh system.
        // Lock system cluster version.
        this.refreshManager.refreshSystem(true);

        Worker worker = workerDAO.getByAddress(stopReq.getAddress());
        if (Objects.isNull(worker)) {
            log.error("worker({}) do not exist!", stopReq.getAddress());
            return;
        }

        // Update worker
        worker.setStatus(WorkerStatusEnum.OFFLINE.getStatus());
        worker.setUpdateTime(DateUtil.timestamp());
        workerDAO.save(worker);

        // Refresh cluster context.
        this.refreshClusterContext();
    }


    /**
     * Worker check
     */
    public void workerCheck() {
        Set<Long> currentSlots = ClusterContext.getCurrentSlots();

        // Query slot ids.
        List<Long> querySlotIds = Lists.newArrayList();
        currentSlots.forEach(id -> {
            if (id <= ClusterContext.getSystem().getWorkerSupervisorSlot()) {
                querySlotIds.add(id);
            }
        });

        // Query workers
        Map<Integer, List<Worker>> workerMap = this.workerDAO.listAllWorkersBySlotIds(querySlotIds)
                .stream()
                .collect(Collectors.groupingBy(Worker::getStatus));

        // New join worker
        long onlinePos = DateUtil.timestamp() - this.clusterProperties.getWorker().getOnlinePeriod();
        List<Worker> offlineWorkers = Optional.ofNullable(workerMap.get(WorkerStatusEnum.OFFLINE.getStatus())).orElseGet(ArrayList::new);
        offlineWorkers.forEach(w -> {
            // Join worker
            // Ignore just off the line
            if (w.getUpdateTime() < onlinePos && w.getLastHeartbeatTime() > onlinePos) {
                WorkerStartRequest workerStartRequest = new WorkerStartRequest();
                workerStartRequest.setAddress(w.getAddress());
                workerStartRequest.setAppName(w.getAppName());
                workerStartRequest.setWorkerKey(w.getWorkerKey());
                workerStartRequest.setProtocolType(w.getProtocolType());

                log.info("Scheduling worker start begin! address={}", w.getAddress());
                this.workerStart(workerStartRequest);
            }
        });

        // New fail worker.
        long offlinePos = DateUtil.timestamp() - this.clusterProperties.getWorker().getOfflinePeriod();
        List<Worker> onlineWorkers = Optional.ofNullable(workerMap.get(WorkerStatusEnum.ONLINE.getStatus())).orElseGet(ArrayList::new);
        onlineWorkers.forEach(w -> {
            // Fail worker
            if (w.getLastHeartbeatTime() < offlinePos) {
                WorkerStopRequest workerStopRequest = new WorkerStopRequest();
                workerStopRequest.setWorkerKey(w.getWorkerKey());
                workerStopRequest.setAddress(w.getAddress());
                workerStopRequest.setAppName(w.getAppName());

                log.info("Scheduling worker stop begin! address={}", w.getAddress());
                this.workerStop(workerStopRequest);
            }
        });
    }

    private void refreshClusterContext() {
        List<Worker> workers = workerDAO.listOnlineWorkers();
        ClusterUtil.refreshAppWorkers(workers);

        log.info("Refresh app workers={}", workers.stream().map(Worker::getAddress).collect(Collectors.toList()));
    }

    /**
     * Check app name.
     *
     * @param appName app name.
     * @return App
     */
    private App checkAppName(String appName) {
        App app = appDAO.getAppByName(appName);
        if (Objects.isNull(app)) {
            throw new RuntimeException(String.format("Register application(%s) do not exist!", appName));
        }
        return app;
    }

    private void updateWorkerForStart(WorkerStartRequest startReq, App app) {
        // Update worker.
        long now = DateUtil.timestamp();
        Worker worker = workerDAO.getByAddress(startReq.getAddress());
        if (Objects.nonNull(worker)) {
            worker.setWorkerKey(worker.getWorkerKey());
            worker.setStatus(WorkerStatusEnum.ONLINE.getStatus());

            //Must update last heartbeat time.
            worker.setLastHeartbeatTime(now);
            worker.setUpdateTime(now);

            // Update latest app name
            worker.setAppName(startReq.getAppName());
            worker.setNamespaceId(app.getNamespaceId());
            worker.setAppId(app.getId());
            worker.setProtocolType(startReq.getProtocolType());
            workerDAO.save(worker);
            return;
        }

        // save
        Worker saveWorker = new Worker();
        saveWorker.setUpdateTime(now);
        saveWorker.setCreateTime(now);
        saveWorker.setWorkerKey(startReq.getWorkerKey());
        saveWorker.setAddress(startReq.getAddress());
        saveWorker.setSlotsId(SlotsUtil.getWorkerSupervisorSlotsId(UUID.randomUUID().toString()));
        saveWorker.setStatus(WorkerStatusEnum.ONLINE.getStatus());
        saveWorker.setAppId(app.getId());
        saveWorker.setAppName(startReq.getAppName());
        saveWorker.setLastHeartbeatTime(now);
        saveWorker.setNamespaceId(app.getNamespaceId());
        saveWorker.setProtocolType(startReq.getProtocolType());
        saveWorker.setVersion(startReq.getVersion());
        saveWorker.setMetric("");
        saveWorker.setVersion("");
        saveWorker.setWorkerKey("");
        saveWorker.setDeleteTime(0L);
        saveWorker.setDeleted(CommonConstant.NO);
        workerDAO.save(saveWorker);
    }
}
