package org.infinity.passport.repository;

import org.infinity.passport.domain.AppAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the AppAuthority entity.
 */
@Repository
public interface AppAuthorityRepository extends MongoRepository<AppAuthority, String> {

    List<AppAuthority> findByAppName(String appName);

    // 会发生空指针异常，参照
    // https://lishman.io/spring-data-mongodb-repository-queries
    // https://stackoverflow.com/questions/58425869/how-to-query-upon-the-given-parameters-in-mongo-repository
//    @Query("{'$and':[{'$or':[{?0:null}, {'appName':?0}]}, {'$or':[{?1:null}, {'authorityName':?1}]}]}")
//    @Query(value = "{$or:[{?0:null},{appName:?0}], $or:[{?1:null},{authorityName:?1}]}")
//    Page<AppAuthority> findByAppNameAndAuthorityName(String appName, String authorityName, Pageable pageable);

    Optional<AppAuthority> findOneByAppNameAndAuthorityName(String appName, String authorityName);

    void deleteByAppName(String name);

}
