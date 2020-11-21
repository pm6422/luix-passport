package org.infinity.passport.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.DictItem;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.DictItemRepository;
import org.infinity.passport.service.DictItemService;
import org.infinity.passport.service.DictService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DictItemServiceImpl implements DictItemService {

    private final DictItemRepository dictItemRepository;

    private final DictService dictService;

    public DictItemServiceImpl(DictItemRepository dictItemRepository, DictService dictService) {
        this.dictItemRepository = dictItemRepository;
        this.dictService = dictService;
    }

    @Override
    public DictItem insert(String dictCode, String dictItemCode, String dictItemName, String remark, Boolean enabled) {
        Map<String, String> dictCodeDictNameMap = dictService.findDictCodeDictNameMap();
        DictItem dictItem = new DictItem(dictCode, dictCodeDictNameMap.get(dictCode), dictItemCode, dictItemName,
                remark, enabled);
        dictItemRepository.save(dictItem);
        return dictItem;
    }

    @Override
    public void update(String id, String dictCode, String dictItemCode, String dictItemName, String remark,
                       Boolean enabled) {
        Map<String, String> findDictCodeDictNameMap = dictService.findDictCodeDictNameMap();
        DictItem existingDictItem = dictItemRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        existingDictItem.setDictCode(dictCode);
        existingDictItem.setDictName(findDictCodeDictNameMap.get(dictCode));
        existingDictItem.setDictItemCode(dictItemCode);
        existingDictItem.setDictItemName(dictItemName);
        existingDictItem.setRemark(remark);
        existingDictItem.setEnabled(enabled);
        dictItemRepository.save(existingDictItem);
    }

    @Override
    public Page<DictItem> findByDictCodeAndDictItemNameCombinations(Pageable pageable, String dictCode,
                                                                    String dictItemName) {
        if (StringUtils.isEmpty(dictCode) && StringUtils.isEmpty(dictItemName)) {
            return dictItemRepository.findAll(pageable);
        } else if (StringUtils.isNotEmpty(dictCode) && StringUtils.isNotEmpty(dictItemName)) {
            return dictItemRepository.findByDictCodeAndDictItemName(pageable, dictCode, dictItemName);
        } else if (StringUtils.isNotEmpty(dictCode) && StringUtils.isEmpty(dictItemName)) {
            return dictItemRepository.findByDictCode(pageable, dictCode);
        } else {
            return dictItemRepository.findByDictItemName(pageable, dictItemName);
        }
    }
}