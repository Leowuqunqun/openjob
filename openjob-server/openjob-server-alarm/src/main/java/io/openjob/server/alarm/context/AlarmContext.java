package io.openjob.server.alarm.context;

import com.google.common.collect.Maps;
import io.openjob.server.repository.entity.AlertRule;
import io.openjob.server.repository.entity.App;
import io.openjob.server.repository.entity.Delay;
import io.openjob.server.repository.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.6
 */
@Slf4j
public class AlarmContext {

    /**
     * Alert rules
     */
    private static final List<AlertRule> ALARM_RULES = new ArrayList<>();

    /**
     * appId => App
     */
    private static final Map<Long, App> APP_MAP = Maps.newConcurrentMap();

    /**
     * jobId => Job
     */
    private static final Map<Long, Job> JOB_MAP = Maps.newConcurrentMap();

    /**
     * topic => Delay
     */
    private static final Map<String, Delay> DELAY_MAP = Maps.newConcurrentMap();

    public static synchronized void refreshAlarmRules() {
        ALARM_RULES.clear();
        log.info("Refresh alarm rules success!");
    }

    public static synchronized void refreshAppMap() {
        APP_MAP.clear();
        log.info("Refresh alarm app map success!");
    }

    public static synchronized void refreshJobMap() {
        JOB_MAP.clear();
        log.info("Refresh alarm job map success!");
    }

    public static synchronized void refreshDelayMap() {
        DELAY_MAP.clear();
        log.info("Refresh alarm delay map success!");
    }

    /**
     * Get alarm rules
     *
     * @param supplier supplier
     * @return List
     */
    public static synchronized List<AlertRule> getAlarmRules(Supplier<List<AlertRule>> supplier) {
        if (CollectionUtils.isEmpty(ALARM_RULES)) {
            ALARM_RULES.addAll(supplier.get());
        }
        return ALARM_RULES;
    }

    public static synchronized App getAppById(Long appid, Function<Long, App> function) {
        return APP_MAP.computeIfAbsent(appid, function);
    }

    public static synchronized Job getJobById(Long jobId, Function<Long, Job> function) {
        return JOB_MAP.computeIfAbsent(jobId, function);
    }

    public static synchronized Delay getDelayByTopic(String topic, Function<String, Delay> function) {
        return DELAY_MAP.computeIfAbsent(topic, function);
    }
}
