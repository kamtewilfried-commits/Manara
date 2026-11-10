package com.manara.crm.Twilio;
import com.manara.crm.entity.User;

import com.manara.crm.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.authentication.AnonymousAuthenticationToken;

import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class VerificationController {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    @GetMapping("/verification")
    public String verificationPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "verification";
    }
    @PostMapping("/verification/check")
    public String checkOtp(@RequestParam String code,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           HttpSession session,
                           Model model) {

        String key = (String) session.getAttribute("otpKey");
        Long userId = (Long) session.getAttribute("pendingUserId");

        if (key == null || userId == null) {
            model.addAttribute("error", "Session expirée.");
            return "login";
        }

        boolean valid = otpService.validateOtp(key, code);

        if (!valid) {
            model.addAttribute("error", "Code invalide ou expiré.");
            return "verification";
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Utilisateur introuvable.");
            return "login";
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.saveContext(context, request, response);

        session.removeAttribute("otpKey");
        session.removeAttribute("pendingUserId");

        return "redirect:/";
    }

    /*@PostMapping("/verification/check")
    public String checkOtp(@RequestParam String code,
                           HttpSession session,
                           Model model) {

        String key = (String) session.getAttribute("otpKey");
        Long userId = (Long) session.getAttribute("pendingUserId");

        if (key == null || userId == null) {
            model.addAttribute("error", "Session expirée.");
            return "login";
        }

        boolean valid = otpService.validateOtp(key, code);

        if (!valid) {
            model.addAttribute("error", "Code invalide ou expiré.");
            return "verification";
        }

        session.removeAttribute("otpKey");
        session.removeAttribute("pendingUserId");
        session.setAttribute("loggedInUser", userId);

        return "redirect:/";
    }*/
}