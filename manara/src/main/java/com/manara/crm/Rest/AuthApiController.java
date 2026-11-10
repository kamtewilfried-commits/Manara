package com.manara.crm.Rest;
import com.manara.crm.Twilio.EmailService;
import com.manara.crm.dto.*;
import com.manara.crm.mapper.UserMapper;
import jakarta.validation.Valid;
import com.manara.crm.Twilio.OtpService;
import com.manara.crm.Twilio.SmsService;
import com.manara.crm.entity.User;
import com.manara.crm.repos.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
//@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class AuthApiController {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final SmsService smsService;

    private String digitsOnlyPhone(String phone) {
        return phone == null ? null : phone.replaceAll("\\D", "");
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new LoginResponse(false, false, "Email ou mot de passe invalide.", null);
        }

        if (!user.isVerified()) {
            return new LoginResponse(false, false, "Vous devez vérifier votre email avant de vous connecter.", null);
        }

        if (user.isTwoFactorEnabled()) {
            String phone = user.getPhone();
            if (phone == null || phone.isBlank()) {
                return new LoginResponse(
                        false,
                        false,
                        "Connexion impossible: aucun numero de telephone n'est configure pour l'OTP.",
                        null
                );
            }

            try {
                String otp = otpService.generateOtp(phone);
                smsService.sendOtp(phone, otp);

                session.setAttribute("pendingUserId", user.getId());
                session.setAttribute("otpKey", phone);

                return new LoginResponse(true, true, "OTP envoye par SMS.",
                        UserMapper.toDTO(user));
            } catch (Exception e) {
                return new LoginResponse(
                        false,
                        false,
                        "Impossible d'envoyer le code OTP. Verifiez la configuration SMS ou le numero de telephone.",
                        null
                );
            }
        }

        authenticateUser(user, session);

        return new LoginResponse(true, false, "Connexion réussie.",
                UserMapper.toDTO(user));
    }

    @PostMapping("/verify-otp")
    public LoginResponse verifyOtp(@Valid @RequestBody OtpLoginRequest request, HttpSession session) {
        Long pendingUserId = (Long) session.getAttribute("pendingUserId");
        String otpKey = (String) session.getAttribute("otpKey");

        if (pendingUserId == null || otpKey == null) {
            return new LoginResponse(false, false, "Session expirée. Veuillez recommencer.", null);
        }

        if (!pendingUserId.equals(request.getUserId())) {
            return new LoginResponse(false, false, "Utilisateur invalide.", null);
        }

        boolean valid = otpService.validateOtp(otpKey, request.getOtp());

        if (!valid) {
            return new LoginResponse(false, true, "Code OTP invalide.", null);
        }

        User user = userRepository.findById(pendingUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        authenticateUser(user, session);

        session.removeAttribute("pendingUserId");
        session.removeAttribute("otpKey");

        return new LoginResponse(true, false, "Connexion réussie.",
                UserMapper.toDTO(user));
    }

    @PostMapping("/resend-otp")
    public LoginResponse resendOtp(HttpSession session) {
        Long pendingUserId = (Long) session.getAttribute("pendingUserId");

        if (pendingUserId == null) {
            return new LoginResponse(false, false, "Session expirée. Veuillez recommencer.", null);
        }

        User user = userRepository.findById(pendingUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String otp = otpService.generateOtp(user.getPhone());
        smsService.sendOtp(user.getPhone(), otp);

        session.setAttribute("otpKey", user.getPhone());

        return new LoginResponse(true, true, "Un nouveau code OTP a été envoyé.", null);
    }


    @GetMapping("/me")
    public LoginResponse currentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUser");

        if (userId == null) {
            return new LoginResponse(false, false, "Non authentifié.", null);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new LoginResponse(false, false, "Utilisateur introuvable.", null);
        }

        return new LoginResponse(true, false, "Utilisateur connecté.",
                UserMapper.toDTO(user));
    }
    @PostMapping("/logout")
    public LoginResponse logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return new LoginResponse(true, false, "Déconnexion réussie.", null);
    }

    private void authenticateUser(User user, HttpSession session) {
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                );

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        springUser,
                        null,
                        springUser.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        session.setAttribute("loggedInUser", user.getId());
    }

    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return new LoginResponse(false, false, "Cet email existe déjà.", null);
        }

        String token = UUID.randomUUID().toString();

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(digitsOnlyPhone(request.getPhone()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole("PARENT");
        user.setVerified(false);
        user.setActive(true);
        user.setTwoFactorEnabled(true);

        user.setEmailVerificationToken(token);
        user.setEmailVerificationExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        String verificationLink = "http://localhost:4200/verify-email?token=" + token;

        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationLink);
        } catch (Exception e) {
            System.out.println("⚠️ Email non envoyé");
        }

        return new LoginResponse(true, false, "Compte créé. Vérifiez votre email.", null);
    }

    @GetMapping("/api/auth/verify-email")
    public ResponseEntity<?> verifyEmailApi(@RequestParam String token) {

        User user = userRepository.findByEmailVerificationToken(token).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lien invalide."));
        }

        if (user.getEmailVerificationExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lien expiré."));
        }

        user.setVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiry(null);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Email vérifié avec succès."));
    }
}