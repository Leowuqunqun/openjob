package io.openjob.server.repository.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "`job`")
public class Job {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native",parameters = {@Parameter(name = "sequence_name", value = "job_id")})
    private Long id;

    @Column(name = "`namespace_id`")
    private Long namespaceId;

    @Column(name = "`app_id`")
    private Long appId;

    @Column(name = "`slots_id`")
    private Long slotsId;

    @Column(name = "`workflow_id`")
    private Long workflowId;

    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`description`")
    private String description;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`processor_type`")
    private String processorType;

    @Column(name = "`processor_info`")
    private String processorInfo;

    @Column(name = "`execute_type`")
    private String executeType;

    @Column(name = "`params`")
    private String params;

    @Column(name = "`params_type`")
    private String paramsType;

    @Column(name = "`extend_params_type`")
    private String extendParamsType;

    @Column(name = "`extend_params`")
    private String extendParams;

    @Column(name = "`fail_retry_times`")
    private Integer failRetryTimes;

    @Column(name = "`fail_retry_interval`")
    private Integer failRetryInterval;

    @Column(name = "`execute_timeout`")
    private Integer executeTimeout;

    @Column(name = "`concurrency`")
    private Integer concurrency;

    @Column(name = "`time_expression_type`")
    private String timeExpressionType;

    @Column(name = "`time_expression`")
    private String timeExpression;

    @Column(name = "`next_execute_time`")
    private Long nextExecuteTime;

    @Column(name = "`execute_strategy`")
    private Integer executeStrategy;

    @Column(name = "`deleted`")
    private Integer deleted;

    /**
     * Delete time
     */
    @Column(name = "`delete_time`")
    private Long deleteTime;

    @Column(name = "`create_time`")
    private Long createTime;

    @Column(name = "`update_time`")
    private Long updateTime;
}
