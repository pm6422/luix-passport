package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, String> {
    Optional<PromoCode> findByCode(String code);

    boolean existsByBatchIdAndUsedBy(String batchId, String usedBy);
}
