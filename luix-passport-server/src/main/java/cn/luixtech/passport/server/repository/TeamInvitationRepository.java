package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, String> {
  List<TeamInvitation> findByTeamIdAndStatus(String teamId, String status);

  Optional<TeamInvitation> findByTeamIdAndEmailAndStatus(String teamId, String email, String status);

  List<TeamInvitation> findByEmailAndStatusAndExpiresAtAfter(String email, String status, Instant now);

  List<TeamInvitation> findByTeamId(String teamId);

  List<TeamInvitation> findByTeamIdAndStatusNot(String teamId, String status);
}
