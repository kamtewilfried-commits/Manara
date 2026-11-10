package com.manara.crm.Twilio;

import com.manara.crm.entity.User;
import com.manara.crm.repos.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private String digitsOnlyPhone(String phone) {
        return phone == null ? null : phone.replaceAll("\\D", "");
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String prenom,
            @RequestParam String nom,
            @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           HttpServletRequest request,
                           Model model) {

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Cet email existe déjà.");
            return "register";
        }

        String token = UUID.randomUUID().toString();

        User user = new User();
        user.setFirstName(prenom);
        user.setLastName(nom);
        user.setEmail(email);
        user.setPhone(digitsOnlyPhone(phone));
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setVerified(false);
        user.setActive(true);
        user.setTwoFactorEnabled(true);
        user.setEmailVerificationToken(token);
        user.setEmailVerificationExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String verificationLink = appUrl + "/verify-email?token=" + token;

        System.out.println("🔗 Lien de vérification : " + verificationLink);

        try {
            emailService.sendVerificationEmail(email, verificationLink);
        } catch (Exception e) {
            System.out.println("⚠️ Email non envoyé (mode dev)");
        }

        model.addAttribute("message", "Compte créé. Vérifiez votre email.");
        return "login";
    }

    @PostMapping("/register/ajax")
    @ResponseBody
    public ResponseEntity<?> registerAjax(@RequestBody Map<String, String> payload,
                                          HttpServletRequest request) {
        String nom = payload.get("nom");
        String prenom= payload.get("prenom");
        String email = payload.get("email");
        String phone = payload.get("phone");
        String password = payload.get("password");

        if (nom == null || nom.isBlank() ||
                prenom == null ||prenom.isBlank() ||
                email == null || email.isBlank() ||
                phone == null || phone.isBlank() ||
                password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Tous les champs sont obligatoires."));
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Cet email existe déjà."));
        }

        String token = UUID.randomUUID().toString();

        User user = new User();
        user.setFirstName(prenom);
        user.setLastName(nom);
        user.setEmail(email);
        user.setPhone(digitsOnlyPhone(phone));
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("PARENT");
        user.setVerified(false);
        user.setActive(true);
        user.setTwoFactorEnabled(true);
        user.setEmailVerificationToken(token);
        user.setEmailVerificationExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String verificationLink = appUrl + "/verify-email?token=" + token;

        System.out.println("🔗 Lien de vérification AJAX : " + verificationLink);

        try {
            emailService.sendVerificationEmail(email, verificationLink);
        } catch (Exception e) {
            System.out.println("⚠️ Email non envoyé (mode dev)");
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Compte créé. Vérifiez votre email.",
                        "redirectUrl", "/login"
                )
        );
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, Model model) {

        User user = userRepository.findByEmailVerificationToken(token).orElse(null);

        if (user == null) {
            model.addAttribute("error", "Lien invalide.");
            return "login";
        }

        if (user.getEmailVerificationExpiry() == null ||
                user.getEmailVerificationExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Lien expiré.");
            return "login";
        }

        user.setVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiry(null);

        userRepository.save(user);

        model.addAttribute("message", "Email vérifié avec succès !");
        return "login";
    }
}