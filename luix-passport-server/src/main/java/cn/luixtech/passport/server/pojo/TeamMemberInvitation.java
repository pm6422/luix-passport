package cn.luixtech.passport.server.pojo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TeamMemberInvitation {

  @NotEmpty
  private String email;
  @NotEmpty
  private String role;
}
