package com.manara.crm.Rest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.manara.crm.entity.User;
import com.manara.crm.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatting")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
//@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
public class ChatTokenController {

    private final UserService userService;

    private final String apiSecret = System.getenv("STREAM_API_SECRET");

    public ChatTokenController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/token")
    public Map<String, String> getToken(Authentication authentication) {

        String email = authentication.getName();

        User user = userService.findByEmail(email);

        String streamUserId = user.getId().toString();

        String token = JWT.create()
                .withClaim("user_id", streamUserId)
                .sign(Algorithm.HMAC256(apiSecret));

        return Map.of(
                "token", token,
                "userId", streamUserId,
                "name", user.getFirstName() + " " + user.getLastName(),
                "email", user.getEmail()
        );
    }
    @GetMapping("/users")
    public List<Map<String, String>> getChatUsers(Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);

        return userService.findAll()
                .stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .map(user -> Map.of(
                        "id", user.getId().toString(),
                        "name", user.getFirstName() + " " + user.getLastName(),
                        "email", user.getEmail()
                ))
                .toList();
    }
}