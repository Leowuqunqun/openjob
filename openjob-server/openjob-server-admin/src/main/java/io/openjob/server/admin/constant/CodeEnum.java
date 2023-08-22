package io.openjob.server.admin.constant;

import io.openjob.server.common.exception.CodeExceptionAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: 100+
 * Namespace: 200+
 * Application: 300+
 * Job: 400+
 * Delay: 500+
 * Alert: 1000+
 *
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum CodeEnum implements CodeExceptionAssert {
    // Code list
    USER_EXIST(100, "User is exist!"),
    USER_NOT_FOUND(101, "User not found"),
    USER_DELETED(102, "User deleted"),
    USER_PWD_INVALID(103, "User password invalid"),
    USER_ROLE_EMPTY(104, "User role empty"),
    USER_VERIFY_CODE_INVALID(105, "Verification code invalid"),

    // Namespace
    NAMESPACE_DELETE_INVALID(200, "Namespace can not be delete!"),

    // Application
    APP_NAME_EXIST(300, "App name must be globally unique!"),
    APP_DELETE_INVALID(301, "Application can not be deleted!"),

    // Job
    TIME_EXPRESSION_INVALID(400, "Time expression is invalid"),
    JOB_DELETE_INVALID(401, "Job can not be deleted!"),
    SHELL_PROCESSOR_INFO_INVALID(402, "Shell content can not be empty!"),
    SHELL_PROCESSOR_TYPE_INVALID(403, "Shell type type can not be empty!"),
    KETTLE_PROCESSOR_INFO_INVALID(404, "Kettle command can not be empty!"),
    KETTLE_PROCESSOR_TYPE_INVALID(405, "Kettle command type can not be empty!"),
    SHARDING_PARAMS_INVALID(406, "Sharding params can not be empty!"),

    // Delay
    DELAY_TOPIC_EXIST(500, "Topic is exist!"),
    DELAY_DELETE_INVALID(501, "Delay can not be deleted!"),

    // Alert
    ALERT_SECRET_EMPTY(1001, "Secret can not be empty!"),
    ;


    /**
     * Value
     */
    private final Integer value;

    /**
     * Message
     */
    private final String message;
}
