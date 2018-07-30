package org.infinity.passport.service;

import org.infinity.passport.domain.DictItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DictItemService {

    DictItem insert(String dictCode, String dictItemCode, String dictItemName, String remark, Boolean enabled);

    void update(String id, String dictCode, String dictItemCode, String dictItemName, String remark, Boolean enabled);

    Page<DictItem> findByDictCodeAndDictItemNameCombinations(Pageable pageable, String dictCode, String dictItemName);
}