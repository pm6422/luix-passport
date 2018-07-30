package org.infinity.passport.repository;

import java.util.List;

import org.infinity.passport.domain.App;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the App entity.
 */
public interface AppRepository extends MongoRepository<App, String> {

    List<App> findByEnabled(Boolean enabled);
}
