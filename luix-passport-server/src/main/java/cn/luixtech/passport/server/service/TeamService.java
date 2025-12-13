package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.Team;

public interface TeamService {
  Team create(String name, byte[] photo);

  java.util.List<Team> findUserTeams(String username);
}
