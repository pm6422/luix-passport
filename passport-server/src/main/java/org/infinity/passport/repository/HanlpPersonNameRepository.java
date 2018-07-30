package org.infinity.passport.repository;

import org.infinity.passport.domain.HanlpPersonName;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the HanlpPersonName entity.
 */
public interface HanlpPersonNameRepository extends MongoRepository<HanlpPersonName, String> {

}
