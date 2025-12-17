package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.PromoCode;

import java.time.Instant;
import java.util.List;

public interface PromoCodeService {

    List<PromoCode> generateCodes(int count, String type, int durationDays, Instant expiry);

    void redeemCode(String code, String username);
}
