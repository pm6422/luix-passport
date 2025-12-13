package cn.luixtech.passport.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {
  private String userId;
  private String email;
  private String firstName;
  private String lastName;
  private byte[] profilePic;
  private String role;
  private Instant joinedAt;
}
