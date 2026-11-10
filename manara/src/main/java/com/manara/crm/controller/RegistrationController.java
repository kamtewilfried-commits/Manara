package com.manara.crm.controller;

import com.manara.crm.entity.User;
import com.manara.crm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/parent/inscriptions")
class RegistrationController {

    @Autowired private RegistrationService registrationService;
    @Autowired private UserService         userService;
    @Autowired private NotificationService notificationService;

    // Liste des inscriptions du parent
    @GetMapping
    public String liste(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser",       parent);
        model.addAttribute("registrations",       registrationService.findByParentId(parent.getId()));
        model.addAttribute("unreadNotifications", notificationService.countUnread(parent.getId()));
        return "Parent/registrations/list";
    }

    // Créer une inscription
    @PostMapping("/nouveau")
    public String inscrire(@RequestParam Long sessionId,
                           @RequestParam Long childId,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        try {
            registrationService.inscrire(childId, sessionId);
            redirectAttributes.addFlashAttribute("succes", "Inscription effectuée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/parent/inscriptions";
    }

    // Reçu de confirmation
    @GetMapping("/{id}/confirmation")
    public String confirmation(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser",       parent);
        model.addAttribute("registration",        registrationService.findById(id));
        model.addAttribute("unreadNotifications", notificationService.countUnread(parent.getId()));
        return "Parent/registrations/confirmation";
    }

    // Annuler une inscription
    @PostMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        registrationService.annuler(id);
        redirectAttributes.addFlashAttribute("succes", "Inscription annulée.");
        return "redirect:/parent/inscriptions";
    }
}