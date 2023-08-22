package io.openjob.server.cluster.actor;

import io.openjob.common.actor.BaseActor;
import io.openjob.common.request.WorkerDelayStatusRequest;
import io.openjob.common.response.Result;
import io.openjob.common.response.ServerResponse;
import io.openjob.server.cluster.service.WorkerDelayService;
import io.openjob.server.scheduler.service.DelayInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Component
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkerDelayInstanceStatusActor extends BaseActor {

    private final WorkerDelayService workerDelayService;

    @Autowired
    public WorkerDelayInstanceStatusActor(WorkerDelayService workerDelayService) {
        this.workerDelayService = workerDelayService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WorkerDelayStatusRequest.class, this::handleDelayStatus)
                .build();
    }

    /**
     * Handle delay status.
     *
     * @param statusRequest statusRequest
     */
    public void handleDelayStatus(WorkerDelayStatusRequest statusRequest) {
        this.workerDelayService.handleDelayStatus(statusRequest);

        ServerResponse serverResponse = new ServerResponse(statusRequest.getDeliveryId());
        getSender().tell(Result.success(serverResponse), getSelf());
    }
}
