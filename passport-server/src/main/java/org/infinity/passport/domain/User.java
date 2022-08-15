package org.infinity.passport.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.BaseUser;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the User entity.
 */
@Document
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class User extends BaseUser implements Serializable {
    private static final long serialVersionUID = 5164123668745353298L;
}
