package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.DataDict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DataDictService {
    Page<DataDict> find(Pageable pageable, String num, String categoryCode, Boolean enabled);

    void batchUpdateCategoryCode(List<String> ids, String targetCategoryCode);
}
