package com.luixtech.passport.repository;

import com.luixtech.passport.domain.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link UserAuthority} entity.
 */
@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, String> {

    List<UserAuthority> findByUserId(String userId);

    void deleteByUserId(String userId);

}
