package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.DataDict;
import cn.luixtech.passport.server.persistence.Tables;
import cn.luixtech.passport.server.repository.DataDictRepository;
import cn.luixtech.passport.server.service.DataDictService;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.luixtech.passport.server.persistence.tables.DataDict.DATA_DICT;


@Service
@AllArgsConstructor
public class DataDictServiceImpl implements DataDictService {
    private final DSLContext         dslContext;
    private final DataDictRepository dataDictRepository;

    @Override
    public Page<DataDict> find(Pageable pageable, String num, String categoryCode, Boolean enabled) {
        // Ignore a query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        DataDict criteria = new DataDict();
        criteria.setNum(num);
        criteria.setCategoryCode(categoryCode);
        criteria.setEnabled(enabled);
        Example<DataDict> queryExample = Example.of(criteria, matcher);
        return dataDictRepository.findAll(queryExample, pageable);
    }

    @Override
    public void batchUpdateCategoryCode(List<String> ids, String targetCategoryCode) {
        dslContext.update(Tables.DATA_DICT)
                .set(DATA_DICT.CATEGORY_CODE, targetCategoryCode)
                .where(DATA_DICT.ID.in(ids))
                .execute();
    }
}
