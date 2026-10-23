package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar, String> {

}
