package cn.luixtech.passport.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {
  private String userId;
  private String email;
  private String firstName;
  private String lastName;
  private byte[] avatar;
  private String role;
  private Set<String> roles;
  private Instant joinedAt;
}
