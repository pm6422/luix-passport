package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.TeamUser;
import cn.luixtech.passport.server.repository.TeamUserRepository;
import cn.luixtech.passport.server.service.TeamUserService;
import cn.luixtech.passport.server.dto.ManagedUser;
import cn.luixtech.passport.server.dto.TeamMemberInvitation;
import cn.luixtech.passport.server.service.MailService;
import cn.luixtech.passport.server.service.UserRoleService;
import cn.luixtech.passport.server.domain.Role;
import cn.luixtech.passport.server.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamUserServiceImpl implements TeamUserService {

    private final TeamUserRepository teamUserRepository;
    private final UserService userService;
    private final MailService mailService;
    private final UserRoleService userRoleService;

    public TeamUserServiceImpl(TeamUserRepository teamUserRepository,
            @Lazy UserService userService,
            MailService mailService,
            UserRoleService userRoleService) {
        this.teamUserRepository = teamUserRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.userRoleService = userRoleService;
    }

    @Override
    public Set<String> findTeamIdsByUsername(String username) {
        return teamUserRepository.findByUserId(username).stream()
                .map(TeamUser::getTeamId)
                .collect(Collectors.toSet());
    }

    @Override
    public void save(String teamId, String username) {
        save(teamId, Set.of(username));
    }

    @Override
    public void save(String teamId, Set<String> usernames) {
        List<TeamUser> teamUsers = teamUserRepository.findByTeamId(teamId);
        if (CollectionUtils.isEmpty(teamUsers)) {
            // insert
            usernames.forEach(username -> {
                TeamUser teamUser = new TeamUser(teamId, username);
                teamUserRepository.save(teamUser);
            });
        } else {
            // delete
            teamUserRepository.deleteByTeamId(teamId);
            // insert
            usernames.forEach(username -> {
                TeamUser teamUser = new TeamUser(teamId, username);
                teamUserRepository.save(teamUser);
            });
        }
    }

    @Override
    public void invite(String teamId, List<TeamMemberInvitation> invitations) {
        for (TeamMemberInvitation invitation : invitations) {
            String email = invitation.getEmail();
            String role = invitation.getRole();
            ManagedUser user = userService.findByEmail(email);
            if (user != null) {
                if (teamUserRepository.findByTeamIdAndUserId(teamId, user.getUsername()).isEmpty()) {
                    TeamUser teamUser = new TeamUser(teamId, user.getUsername());
                    teamUserRepository.save(teamUser);
                    java.util.Set<String> currentRoles = userRoleService.findRoleIds(user.getUsername());
                    java.util.Set<String> updatedRoles = new java.util.HashSet<>(currentRoles);
                    updatedRoles.add(Role.TEAM_MEMBER.equals(role) ? Role.TEAM_MEMBER : role);
                    userRoleService.update(user.getUsername(), updatedRoles);
                }
            }
            // Send email
            mailService.sendEmail(new String[] { email }, "Invitation", "You have been invited to join the team.");
        }
    }
}
