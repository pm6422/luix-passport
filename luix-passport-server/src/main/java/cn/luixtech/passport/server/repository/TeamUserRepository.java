package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link TeamUser} entity.
 */
@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, String> {

    List<TeamUser> findByTeamId(String teamId);

    void deleteByTeamId(String teamId);
}
