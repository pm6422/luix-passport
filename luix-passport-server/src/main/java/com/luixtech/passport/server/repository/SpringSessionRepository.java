package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.SpringSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringSessionRepository extends JpaRepository<SpringSession, String> {

    List<SpringSession> findAllByOrderByLastAccessTimeDesc();

    long countByCreationTimeAfter(Long since);
}
