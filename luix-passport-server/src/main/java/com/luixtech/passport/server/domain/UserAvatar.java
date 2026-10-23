package com.luixtech.passport.server.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserAvatar {

    @Id
    @NotEmpty(message = "id:{Validation.NotEmpty}")
    @Column(nullable = false, length = 20)
    private String id;

    @NotNull
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] avatar;
}
