package org.infinity.passport.service.impl;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.infinity.passport.domain.DictItem;
import org.infinity.passport.exception.DuplicationException;
import org.infinity.passport.exception.NoDataFoundException;
import org.infinity.passport.repository.DictItemRepository;
import org.infinity.passport.repository.DictRepository;
import org.infinity.passport.service.DictItemService;
import org.infinity.passport.service.DictService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DictItemServiceImpl implements DictItemService {

    private final DictRepository     dictRepository;
    private final DictItemRepository dictItemRepository;
    private final DictService        dictService;

    public DictItemServiceImpl(DictRepository dictRepository,
                               DictItemRepository dictItemRepository,
                               DictService dictService) {
        this.dictRepository = dictRepository;
        this.dictItemRepository = dictItemRepository;
        this.dictService = dictService;
    }

    @Override
    public DictItem insert(DictItem domain) {
        // 判断dictCode是否存在
        dictRepository.findOneByDictCode(domain.getDictCode()).orElseThrow(() -> new NoDataFoundException(domain.getDictCode()));
        // 根据dictItemCode与dictCode检索记录是否存在
        List<DictItem> existingDictItems = dictItemRepository.findByDictCodeAndDictItemCode(domain.getDictCode(),
                domain.getDictItemCode());
        if (CollectionUtils.isNotEmpty(existingDictItems)) {
            throw new DuplicationException(ImmutableMap.of("dictCode", domain.getDictCode(), "dictItemCode", domain.getDictItemCode()));
        }

        Map<String, String> dictCodeDictNameMap = dictService.findDictCodeDictNameMap();
        domain.setDictName(dictCodeDictNameMap.get(domain.getDictCode()));
        dictItemRepository.save(domain);
        return domain;
    }

    @Override
    public void update(DictItem domain) {
        dictItemRepository.findById(domain.getId()).map(dictItem -> {
            Map<String, String> findDictCodeDictNameMap = dictService.findDictCodeDictNameMap();
            dictItem.setDictName(findDictCodeDictNameMap.get(domain.getDictCode()));
            dictItemRepository.save(dictItem);
            log.debug("Updated dict item: {}", domain);
            return dictItem;
        }).orElseThrow(() -> new NoDataFoundException(domain.getId()));
    }

    @Override
    public Page<DictItem> find(Pageable pageable, String dictCode, String dictItemName) {
        DictItem probe = new DictItem();
        probe.setDictCode(dictCode);
        probe.setDictItemName(dictItemName);
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        return dictItemRepository.findAll(Example.of(probe, matcher), pageable);
    }
}