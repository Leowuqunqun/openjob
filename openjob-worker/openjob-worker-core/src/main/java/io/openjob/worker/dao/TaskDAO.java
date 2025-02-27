package io.openjob.worker.dao;

import io.openjob.common.constant.TaskStatusEnum;
import io.openjob.common.util.DateUtil;
import io.openjob.worker.entity.Task;
import io.openjob.worker.exception.BatchUpdateStatusException;
import io.openjob.worker.persistence.H2TaskMemoryPersistence;
import io.openjob.worker.persistence.TaskPersistence;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbc.JdbcBatchUpdateException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Slf4j
public class TaskDAO {
    public static final TaskDAO INSTANCE = new TaskDAO();
    private final TaskPersistence taskPersistence;

    private TaskDAO() {
        this.taskPersistence = new H2TaskMemoryPersistence();
    }

    /**
     * Add
     *
     * @param task task
     * @return Boolean
     */
    public Boolean add(Task task) {
        try {
            Long now = DateUtil.timestamp();
            task.setUpdateTime(now);
            task.setCreateTime(now);
            int rows = taskPersistence.batchSave(Collections.singletonList(task));
            return rows > 0;
        } catch (SQLException e) {
            log.error("Task add failed!", e);
            return false;
        }
    }

    /**
     * Batch add.
     *
     * @param taskList taskList
     * @return Integer
     */
    public Integer batchAdd(List<Task> taskList) {
        try {
            Long now = DateUtil.timestamp();
            taskList.forEach(t -> {
                t.setUpdateTime(now);
                t.setCreateTime(now);
            });

            return taskPersistence.batchSave(taskList);
        } catch (SQLException e) {
            log.error("Task add failed!", e);
            return 0;
        }
    }

    /**
     * Get by task id.
     *
     * @param taskId taskId
     * @return Task
     */
    public Task getByTaskId(String taskId) {
        try {
            return taskPersistence.findByTaskId(taskId);
        } catch (SQLException e) {
            log.error("Task getByTaskId failed!", e);
            return null;
        }
    }

    /**
     * Delete by task ids.
     *
     * @param taskIds task ids.
     * @return effect rows.
     */
    public Integer batchDeleteByTaskIds(List<String> taskIds) {
        try {
            return taskPersistence.batchDeleteByTaskIds(taskIds);
        } catch (SQLException e) {
            log.error("Task batchDeleteByTaskIds failed!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Batch update status by task id.
     *
     * @param tasks         tasks
     * @param currentStatus currentStatus
     * @return effect rows.
     */
    public Integer batchUpdateStatusByTaskId(List<Task> tasks, Integer currentStatus) {
        try {
            return taskPersistence.batchUpdateStatusByTaskId(tasks, currentStatus);
        } catch (JdbcBatchUpdateException exception) {
            throw new BatchUpdateStatusException(exception);
        } catch (Throwable e) {
            log.error("Task batchUpdateStatusByTaskId failed!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Batch update status and worker address.
     *
     * @param taskIds       task ids
     * @param status        status
     * @param workerAddress worker address
     * @return Integer
     */
    public Integer batchUpdateStatusAndWorkerAddressByTaskId(List<String> taskIds, Integer status, String workerAddress) {
        try {
            return taskPersistence.batchUpdateStatusAndWorkerAddressByTaskId(taskIds, status, workerAddress);
        } catch (SQLException e) {
            log.error("Task batchUpdateStatusByTaskId failed!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Batch update failover by worker address list.
     *
     * @param workerAddressList worker address list
     * @return Integer
     */
    public Integer batchUpdateFailoverByWorkerAddress(List<String> workerAddressList) {
        try {
            return taskPersistence.batchUpdateFailoverByWorkerAddress(workerAddressList);
        } catch (SQLException e) {
            log.error("Task batchUpdateFailoverByWorkerAddress failed!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Pull failover list.
     *
     * @param instanceId instance id
     * @param size       size
     * @return List
     */
    public List<Task> pullFailoverListBySize(Long instanceId, Long size) {
        try {
            return taskPersistence.pullFailoverListBySize(instanceId, size);
        } catch (SQLException e) {
            log.error("Task pullFailoverListBySize failed!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get list.
     *
     * @param instanceId instance id.
     * @param circleId   circle id.
     * @param size       size
     * @return List
     */
    public List<Task> getList(Long instanceId, Long circleId, Long size) {
        try {
            return taskPersistence.findListBySize(instanceId, circleId, size);
        } catch (SQLException e) {
            log.error("Task getList failed!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Count task.
     *
     * @param instanceId instance id.
     * @param circleId   circle id.
     * @param statusList status list.
     * @return count numbers.
     */
    public Integer countTask(Long instanceId, Long circleId, List<Integer> statusList) {
        try {
            return taskPersistence.countTask(instanceId, circleId, statusList);
        } catch (SQLException e) {
            log.error("Task countTask failed!", e);
            return 0;
        }
    }
}
