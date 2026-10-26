package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, String> {
    long countByEnabledIsTrue();

    long countByCreatedAtAfter(java.time.Instant createdAt);
}
