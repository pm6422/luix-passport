package cn.luixtech.passport.client.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping(value = {"/", "/index"})
    public String index(Model model) {
        // todo: refactor
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
            model.addAttribute("username", oidcUser.getName());
        }
        return "index";
    }

    @GetMapping("/logged-out")
    public String loggedOut() {
        return "logged-out";
    }
}
