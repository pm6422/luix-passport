package org.infinity.passport.service.impl;

import org.infinity.passport.domain.Dict;
import org.infinity.passport.repository.DictRepository;
import org.infinity.passport.service.DictService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl implements DictService {

    private final DictRepository dictRepository;

    public DictServiceImpl(DictRepository dictRepository) {
        this.dictRepository = dictRepository;
    }

    @Override
    public Map<String, String> findDictCodeDictNameMap() {
        return dictRepository.findAll().stream().collect(Collectors.toMap(Dict::getDictCode, Dict::getDictName));
    }
}