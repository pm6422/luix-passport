package org.infinity.passport.repository;

import org.infinity.passport.domain.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the HttpSession entity.
 */
@Repository
public interface HttpSessionRepository extends MongoRepository<HttpSession, String> {

    Page<HttpSession> findByPrincipal(Pageable pageable, String principal);
}
