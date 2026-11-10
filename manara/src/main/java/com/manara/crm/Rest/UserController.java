package com.manara.crm.Rest;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/user")
    public Map<String, String> getUser(Principal principal) {

        String email;

        if (principal instanceof OAuth2AuthenticationToken oauth) {
            email = oauth.getPrincipal().getAttribute("email");
        } else {
            email = principal.getName();
        }

        return Map.of("email", email);
    }
}