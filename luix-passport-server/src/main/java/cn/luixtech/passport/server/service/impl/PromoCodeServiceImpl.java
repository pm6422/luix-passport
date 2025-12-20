package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.PromoCode;
import cn.luixtech.passport.server.domain.Role;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.repository.PromoCodeRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.PromoCodeService;
import cn.luixtech.passport.server.service.UserRoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

    private static final String PROMO_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PromoCodeRepository promoCodeRepository;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    @Override
    public Page<PromoCode> find(Pageable pageable, String code, String batchId, Boolean enabled) {
        PromoCode probe = new PromoCode();
        if (code != null && !code.isEmpty()) {
            probe.setCode(code);
        }
        if (batchId != null && !batchId.isEmpty()) {
            probe.setBatchId(batchId);
        }

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        if (enabled != null) {
            probe.setEnabled(enabled);
        } else {
            matcher = matcher.withIgnorePaths("enabled");
        }
        return promoCodeRepository.findAll(Example.of(probe, matcher), pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PromoCode> generateCodes(int count, String type, int durationDays, Instant expiry, String batchId) {
        List<PromoCode> codes = new ArrayList<>();
        if (batchId == null || batchId.isEmpty()) {
            batchId = String.valueOf(System.currentTimeMillis());
        }

        for (int i = 0; i < count; i++) {
            PromoCode promoCode = new PromoCode();
            // Generate a readable random code, e.g., 12 chars, avoiding ambiguous
            // characters (I, 1, L, O, 0)
            StringBuilder sb = new StringBuilder(12);
            for (int j = 0; j < 12; j++) {
                sb.append(PROMO_CODE_CHARS.charAt(RANDOM.nextInt(PROMO_CODE_CHARS.length())));
            }
            String code = sb.toString();
            promoCode.setCode(code);
            promoCode.setType(type);
            promoCode.setDurationDays(durationDays);
            promoCode.setExpiresAt(expiry);
            promoCode.setBatchId(batchId);
            codes.add(promoCode);
        }
        return promoCodeRepository.saveAll(codes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void redeemCode(String code, String username) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid promo code"));

        if (!promoCode.isEnabled()) {
            throw new IllegalArgumentException("Promo code is disabled");
        }
        if (promoCode.getUsedAt() != null) {
            throw new IllegalArgumentException("Promo code has already been used");
        }
        if (promoCode.getExpiresAt() != null && promoCode.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Promo code has expired");
        }

        // Check if user has already redeemed a code from this batch
        if (promoCodeRepository.existsByBatchIdAndUsedBy(promoCode.getBatchId(), username)) {
            throw new IllegalArgumentException("You have already redeemed a code from this batch");
        }

        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update user membership
        String newType = promoCode.getType();
        int durationDays = promoCode.getDurationDays();

        Instant now = Instant.now();
        Instant newExpiry;

        if (newType.equals(user.getMembershipType()) &&
                user.getMembershipExpiresAt() != null &&
                user.getMembershipExpiresAt().isAfter(now)) {
            // Extend existing membership
            newExpiry = user.getMembershipExpiresAt().plus(durationDays, ChronoUnit.DAYS);
        } else {
            // New membership or override
            newExpiry = now.plus(durationDays, ChronoUnit.DAYS);
        }

        user.setMembershipType(newType);
        user.setMembershipExpiresAt(newExpiry);
        userRepository.save(user);

        // Update User Roles
        Set<String> roles = userRoleService.findRoleIds(username);
        // Remove old membership roles to ensure only one active (or clean up)
        roles.remove(Role.ROLE_MEMBER_PRO);
        roles.remove(Role.ROLE_MEMBER_PREMIUM);

        // Add new role
        roles.add(newType);

        userRoleService.update(username, roles);

        // Mark code as used
        promoCode.setUsedAt(now);
        promoCode.setUsedBy(username);
        promoCodeRepository.save(promoCode);

        log.info("User {} redeemed code {} for {} days of {}", username, code, durationDays, newType);
    }

    @Override
    public void updateEnabled(String id, boolean enabled) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promo code not found"));
        promoCode.setEnabled(enabled);
        promoCodeRepository.save(promoCode);
        log.info("Promo code {} status updated to {}", id, enabled);
    }
}
