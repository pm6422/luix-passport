package org.infinity.passport.repository;

import org.infinity.passport.domain.HanlpPersonName;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the HanlpPersonName entity.
 */
@Repository
public interface HanlpPersonNameRepository extends MongoRepository<HanlpPersonName, String> {

}
