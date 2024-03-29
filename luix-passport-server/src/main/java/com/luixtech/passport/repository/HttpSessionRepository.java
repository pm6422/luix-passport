package com.luixtech.passport.repository;

import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.domain.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
public interface HttpSessionRepository extends JpaRepository<HttpSession, String> {

    Page<HttpSession> findByPrincipal(Pageable pageable, String principal);
}
