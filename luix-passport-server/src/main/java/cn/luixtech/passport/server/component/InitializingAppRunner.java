package cn.luixtech.passport.server.component;

import cn.luixtech.passport.server.domain.Role;
import cn.luixtech.passport.server.repository.PromoCodeRepository;
import cn.luixtech.passport.server.service.DataDictService;
import cn.luixtech.passport.server.service.PromoCodeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@AllArgsConstructor
@Slf4j
public class InitializingAppRunner implements ApplicationRunner {
    private final DataDictService dataDictService;
    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeService promoCodeService;

    @Override
    public void run(ApplicationArguments args) {
        dataDictService.initAllTimezones();

        if (promoCodeRepository.count() == 0) {
            log.info("Initializing promo codes...");
            promoCodeService.generateCodes(100, Role.ROLE_MEMBER_PRO, 7, Instant.now().plus(365, ChronoUnit.DAYS), "INIT_BATCH");
            log.info("Initialized 100 promo codes.");
        }
    }
}
