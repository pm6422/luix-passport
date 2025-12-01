package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.SpringSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringSessionRepository extends JpaRepository<SpringSession, String> {

    List<SpringSession> findAllByOrderByLastAccessTimeDesc();
}
