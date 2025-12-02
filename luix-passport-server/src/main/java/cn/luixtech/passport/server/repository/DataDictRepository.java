package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.DataDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataDictRepository extends JpaRepository<DataDict, String> {

    DataDict findFirstByOrderByIdAsc();

    List<DataDict> findByCategoryCode(String timezone);

    List<DataDict> findByCategoryCodeAndEnabledIsTrue(String timezone);

    boolean existsByCategoryCodeAndDictCode(String categoryCode, String dictCode);
}
