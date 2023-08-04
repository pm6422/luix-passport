package com.luixtech.passport.repository;

import com.luixtech.passport.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link App} entity.
 */
@Repository
public interface AppRepository extends JpaRepository<App, String> {

}
