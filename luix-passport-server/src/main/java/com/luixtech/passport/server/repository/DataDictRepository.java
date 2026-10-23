package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.DataDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataDictRepository extends JpaRepository<DataDict, String> {

    DataDict findFirstByOrderByIdAsc();

    List<DataDict> findByCategoryCode(String timezone);

    List<DataDict> findByCategoryCodeAndEnabledIsTrue(String timezone);

    boolean existsByCategoryCodeAndDictCode(String categoryCode, String dictCode);

    @Modifying
    @Query("UPDATE DataDict d SET d.categoryCode = :categoryCode WHERE d.id IN :ids")
    void updateCategoryCodeByIdIn(@Param("ids") List<String> ids, @Param("categoryCode") String categoryCode);
}
