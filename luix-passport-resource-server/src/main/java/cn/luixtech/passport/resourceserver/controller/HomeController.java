package cn.luixtech.passport.resourceserver.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class HomeController {

    private final Environment                         env;
    private final Optional<SpringDocConfigProperties> springDocConfigProperties;

    /**
     * Home page.
     */
    @GetMapping("/")
    public ResponseEntity<String> home(HttpServletResponse response) throws IOException {
        if (springDocConfigProperties.isPresent() && springDocConfigProperties.get().getApiDocs().isEnabled()) {
            response.sendRedirect("swagger-ui/index.html");
        }
        return ResponseEntity.ok(env.getProperty("spring.application.name") + " Home Page");
    }
}
