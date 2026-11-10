package com.manara.crm.controller;

import com.manara.crm.entity.User;
import com.manara.crm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/parent/activites")
class ActivityParentController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChildService childService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private SessionService sessionService;

    // Liste des activités disponibles
    @GetMapping
    public String liste(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser", parent);
        model.addAttribute("sessionList",sessionService.findAll());
        model.addAttribute("activities", activityService.findAllActive());
        model.addAttribute("unreadNotifications", notificationService.countUnread(parent.getId()));
        return "Parent/activities/list";
    }

    // Détail d'une activité avec ses sessions
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails userDetails,
                         Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser", parent);
        model.addAttribute("activity", activityService.findById(id));
        model.addAttribute("children", childService.findByParentId(parent.getId()));
        model.addAttribute("unreadNotifications", notificationService.countUnread(parent.getId()));
        return "Parent/activities/detail";
    }
}
