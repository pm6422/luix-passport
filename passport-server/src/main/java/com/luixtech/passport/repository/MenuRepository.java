package com.luixtech.passport.repository;

import com.luixtech.passport.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the {@link Menu} entity.
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {

    Optional<Menu> findOneByAppIdAndDepthAndSequence(String appId, Integer depth, Integer sequence);

    List<Menu> findByAppNameAndIdIn(String appId, Set<String> ids);

    List<Menu> findByAppNameAndIdInAndParentIdNotNull(String appId, Set<String> ids);

    List<Menu> findByAppName(String appId);

    List<Menu> findByAppNameAndDepth(String appId, Integer depth);

    List<Menu> findByAppIdAndDepthOrderBySequenceAsc(String appId, Integer depth);
}
