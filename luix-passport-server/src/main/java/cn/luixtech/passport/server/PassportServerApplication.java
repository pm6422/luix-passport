package cn.luixtech.passport.server;

import com.luixtech.springbootframework.EnableLuixSpringBootFramework;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableLuixSpringBootFramework
public class PassportServerApplication {
    public static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        // Disable jooq's self-ad message
        System.setProperty("org.jooq.no-logo", "true");
        applicationContext = SpringApplication.run(PassportServerApplication.class, args);
    }
}
