package org.infinity.passport.repository;

import java.util.List;

import org.infinity.passport.domain.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
public interface AuthorityRepository extends MongoRepository<Authority, String> {

    List<Authority> findByEnabled(Boolean enabled);

}
