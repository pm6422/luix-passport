package cn.luixtech.passport.client.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ApplicationErrorController {

    private static final Set<String> DEVICE_GRANT_ERRORS = new HashSet<>(Arrays.asList(
            "authorization_pending",
            "slow_down",
            "access_denied",
            "expired_token"
    ));

    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {
        String errorMessage = getErrorMessage(request);
        if (errorMessage.startsWith("[access_denied]")) {
            model.addAttribute("errorTitle", "Access Denied");
            model.addAttribute("errorMessage", "You have denied access.");
        } else {
            model.addAttribute("errorTitle", "Error");
            model.addAttribute("errorMessage", errorMessage);
        }
        return "error";
    }

    private String getErrorMessage(HttpServletRequest request) {
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        return StringUtils.hasText(errorMessage) ? errorMessage : "";
    }

    @ExceptionHandler(OAuth2AuthorizationException.class)
    public ResponseEntity<OAuth2Error> handleError(OAuth2AuthorizationException ex) {
        String errorCode = ex.getError().getErrorCode();
        if (DEVICE_GRANT_ERRORS.contains(errorCode)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getError());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getError());
    }

//    @ExceptionHandler(WebClientResponseException.class)
//    public String handleError(Model model, WebClientResponseException ex) {
//        model.addAttribute("error", ex.getMessage());
//        return "index";
//    }
}
