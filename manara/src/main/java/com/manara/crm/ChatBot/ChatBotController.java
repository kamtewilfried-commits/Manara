package com.manara.crm.ChatBot;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat2")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ChatBotController {

    private final ChatService chatService;

    public ChatBotController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String reply = chatService.processMessage(request.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}