package com.luixtech.passport.server.service;

import com.luixtech.passport.server.domain.PromoCode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface PromoCodeService {

    Page<PromoCode> find(Pageable pageable, String code, String batchId, Boolean enabled);

    List<PromoCode> generateCodes(int count, String type, int durationDays, Instant expiry, String batchId);

    void redeemCode(String code, String username);

    void updateEnabled(String id, boolean enabled);

    void sendEmail(String id, String email, String baseUrl);
}
