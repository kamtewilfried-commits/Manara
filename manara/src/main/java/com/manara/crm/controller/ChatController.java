package com.manara.crm.controller;


import com.manara.crm.entity.User;
import com.manara.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
@Controller
@RequiredArgsConstructor

public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
@Autowired
    UserService userService;
    @GetMapping("/Chat")
    public String chat(Model model, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String email;

        if (principal instanceof OidcUser oidcUser) {
            email= oidcUser.getEmail();

        } else {
            email=authentication.getName();

        }
        model.addAttribute("email", email);
        User connectedUser = userService.findByEmail(email);
        model.addAttribute("connectedUser", connectedUser);
        return "Chat";
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal
    ) {

        String email;

        if (principal instanceof OAuth2AuthenticationToken oauth) {
            email = oauth.getPrincipal().getAttribute("email");
        } else {
            email = principal.getName();
        }

        chatMessage.setSender(email);
        headerAccessor.getSessionAttributes().put("username", email);

        return chatMessage;
    }

    @MessageMapping("/chat.sendPublic")
    @SendTo("/topic/public")
    public ChatMessage sendPublic(@Payload ChatMessage chatMessage,
                                  Principal principal) {

        chatMessage.setSender(principal.getName());
        return chatMessage;
    }

    @MessageMapping("/chat.sendPrivate")
    public void sendPrivate(@Payload ChatMessage chatMessage,
                            Principal principal) {

        chatMessage.setSender(principal.getName());

        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient(),
                "/queue/messages",
                chatMessage
        );
    }

    @MessageMapping("/chat.sendGroup")
    public void sendGroup(@Payload ChatMessage chatMessage,
                          Principal principal) {

        chatMessage.setSender(principal.getName());

        messagingTemplate.convertAndSend(
                "/topic/group/" + chatMessage.getGroupId(),
                chatMessage
        );
    }
}