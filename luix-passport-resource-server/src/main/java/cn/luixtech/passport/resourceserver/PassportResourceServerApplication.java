package cn.luixtech.passport.resourceserver;

import com.luixtech.springbootframework.EnableLuixSpringBootFramework;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableLuixSpringBootFramework
public class PassportResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PassportResourceServerApplication.class, args);
    }

}
