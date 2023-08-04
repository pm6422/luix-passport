package com.luixtech.passport.domain;

import com.luixtech.passport.domain.base.BaseUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class User extends BaseUser implements Serializable {
    private static final long serialVersionUID = 5164123668745353298L;
}
