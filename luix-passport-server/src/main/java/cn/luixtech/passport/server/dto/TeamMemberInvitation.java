package cn.luixtech.passport.server.dto;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TeamMemberInvitation {

  @NotEmpty
  private String email;
  @NotEmpty
  private Set<String> roles;
}
