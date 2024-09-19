package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.TableSeqNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link TableSeqNumber} entity.
 */
@Repository
public interface TableSeqNumberRepository extends JpaRepository<TableSeqNumber, String> {

    Optional<TableSeqNumber> findOneByTableName(String tableName);
}
