package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, String> {

    List<TeamUser> findByTeamId(String teamId);

    void deleteByTeamId(String teamId);

    java.util.Optional<TeamUser> findByTeamIdAndUserId(String teamId, String userId);

    List<TeamUser> findByUserId(String userId);
}
