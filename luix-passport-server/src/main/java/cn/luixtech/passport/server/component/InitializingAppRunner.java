package cn.luixtech.passport.server.component;

import cn.luixtech.passport.server.service.SupportedTimezoneService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class InitializingAppRunner implements ApplicationRunner {
    private final SupportedTimezoneService supportedTimezoneService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        updateUtcOffset();
    }


    private void updateUtcOffset() {
        supportedTimezoneService.updateUtcOffset();
    }
}
