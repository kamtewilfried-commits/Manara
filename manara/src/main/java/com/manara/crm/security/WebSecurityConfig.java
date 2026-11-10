package com.manara.crm.security;

import com.manara.crm.entity.User;
import com.manara.crm.repos.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebSecurityConfig {

    private final UserRepository userRepository;

    public WebSecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/api/auth/**",
                                "/uploads/**",
                                "/api/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) -> {

                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

                            String email = oauthUser.getAttribute("email");
                            String firstName = oauthUser.getAttribute("given_name");
                            String lastName = oauthUser.getAttribute("family_name");

                            System.out.println("========== GOOGLE LOGIN ==========");
                            System.out.println("EMAIL = " + email);
                            System.out.println("FIRST NAME = " + firstName);
                            System.out.println("LAST NAME = " + lastName);
                            System.out.println("ATTRIBUTES = " + oauthUser.getAttributes());

                            User user = userRepository.findByEmail(email)
                                    .orElseGet(() -> {
                                        User newUser = new User();

                                        newUser.setEmail(email);
                                        newUser.setFirstName(firstName != null ? firstName : "Google");
                                        newUser.setLastName(lastName != null ? lastName : "User");
                                        newUser.setPhone("0000000000");

                                        newUser.setRole("PARENT");
                                        newUser.setVerified(true);
                                        newUser.setActive(true);
                                        newUser.setTwoFactorEnabled(false);
                                        newUser.setPassword(passwordEncoder().encode("GOOGLE_AUTH"));

                                        return userRepository.save(newUser);
                                    });

                            HttpSession session = request.getSession(true);
                            session.setAttribute("loggedInUser", user.getId());

                            response.sendRedirect("http://localhost:4200/oauth-success");
                        })
                )
                .logout(logout -> logout.disable())
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://localhost:4200"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}