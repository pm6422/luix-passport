package org.infinity.passport.repository;

import org.infinity.passport.domain.Menu;
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

    List<Menu> findByAppIdAndIdIn(String appId, Set<String> ids);

    List<Menu> findByAppIdAndIdInAndParentIdNotNull(String appId, Set<String> ids);

    List<Menu> findByAppId(String appId);

    List<Menu> findByAppIdAndDepth(String appId, Integer depth);

    List<Menu> findByAppIdAndDepthOrderBySequenceAsc(String appId, Integer depth);
}