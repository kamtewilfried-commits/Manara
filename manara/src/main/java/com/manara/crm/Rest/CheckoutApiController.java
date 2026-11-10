package com.manara.crm.Rest;

import com.manara.crm.dto.CheckoutRequest;
import com.manara.crm.entity.User;
import com.manara.crm.repos.UserRepository;
import com.manara.crm.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
public class CheckoutApiController {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequest request,
            Authentication authentication
    ) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Utilisateur non authentifié."));
            }

            String email = authentication.getName();

            User parent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));

            String stripeUrl = checkoutService.checkout(request, parent);

            return ResponseEntity.ok(Map.of("url", stripeUrl));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}