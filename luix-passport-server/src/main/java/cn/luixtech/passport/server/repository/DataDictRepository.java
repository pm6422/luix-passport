package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.DataDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link DataDict} entity.
 */
@Repository
public interface DataDictRepository extends JpaRepository<DataDict, String> {

    DataDict findFirstByOrderByIdAsc();

    List<DataDict> findByCategoryCode(String timezone);

    List<DataDict> findByCategoryCodeAndEnabledIsTrue(String timezone);
}
