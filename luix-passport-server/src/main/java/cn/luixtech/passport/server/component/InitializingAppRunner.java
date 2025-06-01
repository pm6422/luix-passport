package cn.luixtech.passport.server.component;

import cn.luixtech.passport.server.service.DataDictService;
import cn.luixtech.passport.server.service.TableSeqNumberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class InitializingAppRunner implements ApplicationRunner {
    private final TableSeqNumberService tableSeqNumberService;
    private final DataDictService       dataDictService;

    @Override
    public void run(ApplicationArguments args) {
        tableSeqNumberService.init();
        dataDictService.initAllTimezones();
    }
}
