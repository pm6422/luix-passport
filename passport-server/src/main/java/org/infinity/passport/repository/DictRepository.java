package org.infinity.passport.repository;

import org.infinity.passport.domain.Dict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the Dict entity.
 */
@Repository
public interface DictRepository extends MongoRepository<Dict, String> {

    Page<Dict> findByDictName(Pageable pageable, String dictName);

    Optional<Dict> findOneByDictCode(String dictCode);

    List<Dict> findByEnabled(Boolean enabled);
}
