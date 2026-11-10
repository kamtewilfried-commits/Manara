package com.manara.crm.Twilio;

import com.manara.crm.entity.User;
import com.manara.crm.repos.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final SmsService smsService;

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login-step1")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        User user = userRepository.findByEmail(email).orElse(null);

        System.out.println("user = " + (user != null ? user.getEmail() : null));

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "Email ou mot de passe invalide.");
            return "login";
        }

        System.out.println("verified = " + user.isVerified());
        System.out.println("2FA = " + user.isTwoFactorEnabled());

        if (!user.isVerified()) {
            model.addAttribute("error", "Vous devez vérifier votre email avant de vous connecter.");
            return "login";
        }

        if (user.isTwoFactorEnabled()) {
            String otp = otpService.generateOtp(user.getPhone());
            smsService.sendOtp(user.getPhone(), otp);

            session.setAttribute("pendingUserId", user.getId());
            session.setAttribute("otpKey", user.getPhone());

            System.out.println("OTP envoyé à : " + user.getPhone());
            return "redirect:/verification";
        }
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole()))
                );

        org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        springUser,
                        null,
                        springUser.getAuthorities()
                );

        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute("SPRING_SECURITY_CONTEXT",
                org.springframework.security.core.context.SecurityContextHolder.getContext());

        session.setAttribute("loggedInUser", user.getId());
        //session.setAttribute("loggedInUser", user.getId());
        //System.out.println("loggedInUser = " + session.getAttribute("loggedInUser"));

        return "redirect:/";
    }
}