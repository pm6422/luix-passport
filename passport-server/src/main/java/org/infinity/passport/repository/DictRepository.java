package org.infinity.passport.repository;

import java.util.List;
import java.util.Optional;

import org.infinity.passport.domain.Dict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Dict entity.
 */
public interface DictRepository extends MongoRepository<Dict, String> {

    Page<Dict> findByDictName(Pageable pageable, String dictName);

    Optional<Dict> findOneByDictCode(String dictCode);

    List<Dict> findByEnabled(Boolean enabled);
}
