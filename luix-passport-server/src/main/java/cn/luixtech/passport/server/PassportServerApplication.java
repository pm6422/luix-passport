package cn.luixtech.passport.server;

import com.luixtech.springbootframework.EnableLuixSpringBootFramework;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableLuixSpringBootFramework
@EnableScheduling
@EnableAsync
public class PassportServerApplication {
    public static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        // Disable jooq's self-ad message
        System.setProperty("org.jooq.no-logo", "true");
        applicationContext = SpringApplication.run(PassportServerApplication.class, args);
    }
}
