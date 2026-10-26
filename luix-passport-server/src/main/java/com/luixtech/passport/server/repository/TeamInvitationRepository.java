package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, String> {
  List<TeamInvitation> findByTeamIdAndStatus(String teamId, String status);

  Optional<TeamInvitation> findByTeamIdAndInviteeEmailAndStatus(String teamId, String inviteeEmail, String status);

  List<TeamInvitation> findByInviteeEmailAndStatusAndExpiresAtAfter(String inviteeEmail, String status, Instant now);

  List<TeamInvitation> findByTeamId(String teamId);

  List<TeamInvitation> findByTeamIdAndStatusNot(String teamId, String status);

  List<TeamInvitation> findByExpiresAtBeforeAndStatusNotIn(Instant now, List<String> statuses);
}
