package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    long countByEnabledIsTrue();
}
