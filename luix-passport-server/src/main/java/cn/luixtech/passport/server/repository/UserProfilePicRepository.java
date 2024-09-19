package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.UserProfilePic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link UserProfilePic} entity.
 */
@Repository
public interface UserProfilePicRepository extends JpaRepository<UserProfilePic, String> {

}
