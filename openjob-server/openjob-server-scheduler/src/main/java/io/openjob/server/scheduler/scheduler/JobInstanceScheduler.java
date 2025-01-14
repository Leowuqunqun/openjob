package io.openjob.server.scheduler.scheduler;

import akka.actor.ActorSelection;
import io.openjob.common.constant.InstanceStatusEnum;
import io.openjob.common.request.ServerStopJobInstanceRequest;
import io.openjob.common.response.WorkerResponse;
import io.openjob.common.util.FutureUtil;
import io.openjob.server.common.util.ServerUtil;
import io.openjob.server.repository.constant.WorkerStatusEnum;
import io.openjob.server.repository.dao.JobInstanceDAO;
import io.openjob.server.repository.dao.WorkerDAO;
import io.openjob.server.repository.entity.JobInstance;
import io.openjob.server.repository.entity.Worker;
import io.openjob.server.scheduler.dto.JobInstanceStopRequestDTO;
import io.openjob.server.scheduler.dto.JobInstanceStopResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Slf4j
@Component
public class JobInstanceScheduler {
    private final JobInstanceDAO jobInstanceDAO;
    private final WorkerDAO workerDAO;

    @Autowired
    public JobInstanceScheduler(JobInstanceDAO jobInstanceDAO, WorkerDAO workerDAO) {
        this.jobInstanceDAO = jobInstanceDAO;
        this.workerDAO = workerDAO;
    }

    /**
     * Stop
     *
     * @param stopRequest stopRequest
     * @return JobInstanceStopResponseDTO
     */
    public JobInstanceStopResponseDTO stop(JobInstanceStopRequestDTO stopRequest) {
        JobInstanceStopResponseDTO jobInstanceStopResponseDTO = new JobInstanceStopResponseDTO();
        JobInstance jobInstance = this.jobInstanceDAO.getById(stopRequest.getJobInstanceId());
        if (Objects.isNull(jobInstance)) {
            throw new RuntimeException("Job instance is not exist! id=" + stopRequest.getJobInstanceId());
        }

        // Not running or empty address.
        if (!InstanceStatusEnum.isRunning(jobInstance.getStatus()) || StringUtils.isEmpty(jobInstance.getWorkerAddress())) {
            jobInstanceStopResponseDTO.setType(1);
            return jobInstanceStopResponseDTO;
        }

        //  Worker offline
        Worker byAddress = this.workerDAO.getByAddress(jobInstance.getWorkerAddress());
        if (WorkerStatusEnum.OFFLINE.getStatus().equals(byAddress.getStatus())) {
            jobInstanceStopResponseDTO.setType(0);
            return jobInstanceStopResponseDTO;
        }

        try {
            ServerStopJobInstanceRequest serverStopJobInstanceRequest = new ServerStopJobInstanceRequest();
            serverStopJobInstanceRequest.setJobId(jobInstance.getJobId());
            serverStopJobInstanceRequest.setJobInstanceId(jobInstance.getId());
            ActorSelection masterActor = ServerUtil.getWorkerTaskMasterActor(jobInstance.getWorkerAddress());
            FutureUtil.mustAsk(masterActor, serverStopJobInstanceRequest, WorkerResponse.class, 3000L);
        } catch (Throwable throwable) {
            log.error("Job instance exception!", throwable);
            throw new RuntimeException("Job instance exception! message=" + throwable.getMessage());
        }

        jobInstanceStopResponseDTO.setType(0);
        return jobInstanceStopResponseDTO;
    }
}
