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
@RequestMapping("/parent")
public class ParentController {

    @Autowired private UserService   userService;
    @Autowired private ChildService       childService;
    @Autowired private ActivityService    activityService;
    @Autowired private RegistrationService registrationService;
    @Autowired private NotificationService notificationService;

    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser",        parent);
        model.addAttribute("totalEnfants",         childService.countByParentId(parent.getId()));
        model.addAttribute("inscriptionsActives",  registrationService.countActives(parent.getId()));
        model.addAttribute("inscriptionsEnAttente",registrationService.countPending(parent.getId()));
        model.addAttribute("unreadNotifications",  notificationService.countUnread(parent.getId()));
        model.addAttribute("prochainesSessions",   registrationService.findUpcoming(parent.getId()));
        model.addAttribute("recentesNotifications",notificationService.findRecent(parent.getId(), 3));
        model.addAttribute("enfants",              childService.findByParentId(parent.getId()));
        return "Parent/dashboard";
    }

    // ---- Profil ----
    @GetMapping("/profil")
    public String profilForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser", parent);
        model.addAttribute("userForm",      parent);
        model.addAttribute("passwordForm",  new com.manara.crm.dto.PasswordForm());
        return "Parent/profile";
    }

    @PostMapping("/profil")
    public String mettreAJourProfil(@AuthenticationPrincipal UserDetails userDetails,
                                    @ModelAttribute("userForm") User userForm,
                                    RedirectAttributes redirectAttributes) {
        User parent = userService.findByEmail(userDetails.getUsername());
        userService.updateProfile(parent.getId(), userForm);
        redirectAttributes.addFlashAttribute("succes", "Profil mis à jour avec succès.");
        return "redirect:/parent/profil";
    }

    @PostMapping("/profil/mot-de-passe")
    public String changerMotDePasse(@AuthenticationPrincipal UserDetails userDetails,
                                    @ModelAttribute("passwordForm") com.manara.crm.dto.PasswordForm form,
                                    RedirectAttributes redirectAttributes) {
        User parent = userService.findByEmail(userDetails.getUsername());
        try {
            userService.changePassword(parent.getId(), form);
            redirectAttributes.addFlashAttribute("succes", "Mot de passe modifié avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/parent/profil";
    }

    // ---- Planning ----
    @GetMapping("/planning")
    public String planning(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam(required = false) String semaine,
                           Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser",   parent);
        model.addAttribute("unreadNotifications", notificationService.countUnread(parent.getId()));
        // Le service calcule les événements de la semaine demandée
        model.addAttribute("weekLabel",    activityService.getWeekLabel(semaine));
        model.addAttribute("weekDays",     activityService.getWeekDays(semaine));
        model.addAttribute("timeSlots",    activityService.getTimeSlots());
        model.addAttribute("planningEvents", registrationService.getPlanningEvents(parent.getId(), semaine));
        model.addAttribute("previousWeek", activityService.getPreviousWeek(semaine));
        model.addAttribute("nextWeek",     activityService.getNextWeek(semaine));
        return "Parent/planning";
    }

    // ---- Notifications ----
    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser",       parent);
        model.addAttribute("notifications",       notificationService.findAll(parent.getId()));
        model.addAttribute("unreadNotifications", notificationService.countUnread(parent.getId()));
        return "Parent/notifications";
    }

    @PostMapping("/notifications/lire-tout")
    public String marquerToutLu(@AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User parent = userService.findByEmail(userDetails.getUsername());
        notificationService.markAllRead(parent.getId());
        redirectAttributes.addFlashAttribute("succes", "Toutes les notifications ont été marquées comme lues.");
        return "redirect:/parent/notifications";
    }

    @PostMapping("/notifications/{id}/lire")
    public String marquerLu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.markRead(id);
        return "redirect:/parent/notifications";
    }
}
