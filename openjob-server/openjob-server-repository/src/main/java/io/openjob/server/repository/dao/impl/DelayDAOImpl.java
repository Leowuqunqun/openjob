package io.openjob.server.repository.dao.impl;

import io.openjob.common.constant.CommonConstant;
import io.openjob.common.util.DateUtil;
import io.openjob.server.common.dto.PageDTO;
import io.openjob.server.repository.dao.DelayDAO;
import io.openjob.server.repository.dto.DelayPageDTO;
import io.openjob.server.repository.entity.Delay;
import io.openjob.server.repository.repository.DelayRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.MediaSize;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Component
public class DelayDAOImpl implements DelayDAO {
    private final DelayRepository delayRepository;

    @Autowired
    public DelayDAOImpl(DelayRepository delayRepository) {
        this.delayRepository = delayRepository;
    }

    @Override
    public Long save(Delay delay) {
        Long timestamp = DateUtil.timestamp();
        delay.setDeleted(CommonConstant.NO);
        delay.setDeleteTime(0L);
        delay.setCreateTime(timestamp);
        delay.setUpdateTime(timestamp);
        return this.delayRepository.save(delay).getId();
    }

    @Override
    public void deleteById(Long id) {
        this.delayRepository.deleteById(id);
    }

    @Override
    public Long update(Delay delay) {
        this.delayRepository.findById(delay.getId())
                .ifPresent(d -> {
                    d.setNamespaceId(delay.getNamespaceId());
                    d.setAppId(delay.getAppId());
                    d.setName(delay.getName());
                    d.setTopic(delay.getTopic());
                    d.setDescription(delay.getDescription());
                    d.setProcessorInfo(delay.getProcessorInfo());
                    d.setFailRetryTimes(delay.getFailRetryTimes());
                    d.setFailRetryInterval(delay.getFailRetryInterval());
                    d.setExecuteTimeout(delay.getExecuteTimeout());
                    d.setConcurrency(delay.getConcurrency());
                    d.setBlockingSize(delay.getBlockingSize());
                    d.setFailTopicEnable(delay.getFailTopicEnable());
                    d.setFailTopicConcurrency(delay.getFailTopicConcurrency());
                    d.setUpdateTime(DateUtil.timestamp());
                    this.delayRepository.save(d);
                });
        return delay.getId();
    }

    @Override
    public void updateCidById(Long id, Long cid) {
        this.delayRepository.findById(id)
                .ifPresent(d -> {
                    if (Objects.nonNull(cid)) {
                        d.setCid(cid);
                    }
                    this.delayRepository.save(d);
                });
    }

    @Override
    public void updateStatusOrDeleted(Long id, Integer status, Integer deleted) {
        this.delayRepository.findById(id)
                .ifPresent(d -> {
                    if (Objects.nonNull(deleted)) {
                        d.setDeleted(deleted);
                        d.setDeleteTime(DateUtil.timestamp());
                    }
                    this.delayRepository.save(d);
                });
    }

    @Override
    public List<Delay> findByIds(List<Long> ids) {
        return this.delayRepository.findByIdIn(ids);
    }

    @Override
    public Delay findByTopic(String topic) {
        return this.delayRepository.findByTopic(topic);
    }

    @Override
    public Optional<Delay> findById(Long id) {
        return this.delayRepository.findById(id);
    }

    @Override
    public List<Delay> findByTopics(List<String> topics) {
        return this.delayRepository.findByTopicIn(topics);
    }

    @Override
    public List<Delay> findByAppId(Long appId) {
        return this.delayRepository.findByAppIdAndDeleted(appId, CommonConstant.NO);
    }

    @Override
    public Long countByNamespace(Long namespaceId) {
        return this.delayRepository.countByNamespaceIdAndDeleted(namespaceId, CommonConstant.NO);
    }

    @Override
    public Long countByNamespaceAndCreateTime(Long namespaceId, Long startTime, Long endTime) {
        return this.delayRepository.countByNamespaceIdAndCreateTimeGreaterThanEqualAndCreateTimeLessThanEqualAndDeleted(namespaceId, startTime, endTime, CommonConstant.NO);
    }

    @Override
    public Delay getFirstByNamespaceAndAppid(Long namespaceId, Long appId) {
        return this.delayRepository.findFirstByNamespaceIdAndAppIdAndDeleted(namespaceId, appId, CommonConstant.NO);
    }

    @Override
    public PageDTO<Delay> pageList(DelayPageDTO delayPageDTO) {
        // Matcher
        ExampleMatcher matching = ExampleMatcher.matching();
        Delay delay = new Delay();
        delay.setPid(0L);
        delay.setDeleted(CommonConstant.NO);

        // Namespace
        if (Objects.nonNull(delayPageDTO.getNamespaceId())) {
            delay.setNamespaceId(delayPageDTO.getNamespaceId());
        }

        // Application
        if (Objects.nonNull(delayPageDTO.getAppId())) {
            delay.setAppId(delayPageDTO.getAppId());
        }

        // Name
        if (StringUtils.isNotEmpty(delayPageDTO.getName())) {
            delay.setName(delayPageDTO.getName());
            matching = matching.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        }

        // Topic
        if (StringUtils.isNotEmpty(delayPageDTO.getTopic())) {
            delay.setTopic(delayPageDTO.getTopic());
            matching = matching.withMatcher("topic", ExampleMatcher.GenericPropertyMatchers.contains());
        }

        // Condition
        Example<Delay> example = Example.of(delay, matching);
        PageRequest pageRequest = PageRequest.of(delayPageDTO.getPage() - 1, delayPageDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));

        // Pagination
        PageDTO<Delay> pageDTO = new PageDTO<>();

        // Query
        Page<Delay> pageList = this.delayRepository.findAll(example, pageRequest);
        if (!pageList.isEmpty()) {
            pageDTO.setPage(delayPageDTO.getPage());
            pageDTO.setSize(delayPageDTO.getSize());
            pageDTO.setTotal(pageList.getTotalElements());
            pageDTO.setList(pageList.toList());
        }
        return pageDTO;
    }
}
