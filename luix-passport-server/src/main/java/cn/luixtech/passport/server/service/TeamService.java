package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.Team;
import cn.luixtech.passport.server.domain.TeamMember;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface TeamService {
  Team create(String name, byte[] photo);

  List<Team> findUserTeams(String username);

  List<TeamMember> findTeamMembers(HttpServletRequest request, String teamId);
}
