package com.manara.crm.Rest;

import com.manara.crm.dto.NotificationDTO;
import com.manara.crm.dto.NotificationForm;
import com.manara.crm.mapper.NotificationMapper;
import com.manara.crm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
//@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
public class NotificationApiController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public List<NotificationDTO> getNotificationsByUser(@PathVariable Long userId) {
        return NotificationMapper.toDTOList(notificationService.findAll(userId));
    }

    @GetMapping("/admin")
    public List<NotificationDTO> getAllAdminNotifications() {
        return NotificationMapper.toDTOList(notificationService.findAllAdmin());
    }

    @GetMapping("/user/{userId}/recent")
    public List<NotificationDTO> getRecentNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return NotificationMapper.toDTOList(notificationService.findRecent(userId, limit));
    }

    @GetMapping("/user/{userId}/unread-count")
    public long countUnread(@PathVariable Long userId) {
        return notificationService.countUnread(userId);
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        notificationService.markRead(id);
    }

    @PatchMapping("/user/{userId}/read-all")
    public void markAllRead(@PathVariable Long userId) {
        notificationService.markAllRead(userId);
    }

    @PostMapping("/admin/send")
    public void sendAdminNotification(@RequestBody NotificationForm form) {
        notificationService.envoyerAdmin(form);
    }
}