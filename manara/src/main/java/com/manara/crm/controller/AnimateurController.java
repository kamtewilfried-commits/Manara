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

import java.util.List;

@Controller
@RequestMapping("/animateur")
public class AnimateurController {

    @Autowired private UserService         userService;
    @Autowired private SessionService      sessionService;
    @Autowired private ActivityService     activityService;
    @Autowired private RegistrationService registrationService;

    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User animateur = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser",        animateur);
        model.addAttribute("totalActivites",       activityService.countByAnimateurId(animateur.getId()));
        model.addAttribute("sessionsCetteSemaine", sessionService.countThisWeek(animateur.getId()));
        model.addAttribute("totalEnfants",         registrationService.countByAnimateurId(animateur.getId()));
        model.addAttribute("sessionDuJour",        sessionService.findCurrentToday(animateur.getId()));
        model.addAttribute("prochainesSessions",   sessionService.findUpcoming(animateur.getId(), 5));
        return "Animateur/dashboard";
    }

    // ---- Liste sessions ----
    @GetMapping("/sessions")
    public String sessions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User animateur = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser", animateur);
        model.addAttribute("sessions",     sessionService.findByAnimateurId(animateur.getId()));
        model.addAttribute("activites",    activityService.findByAnimateurId(animateur.getId()));
        return "Animateur/sessions/list";
    }

    // ---- Détail session (liste inscrits) ----
    @GetMapping("/sessions/{id}")
    public String detailSession(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User animateur = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser", animateur);
        model.addAttribute("session",       sessionService.findById(id));
        model.addAttribute("registrations", registrationService.findBySessionId(id));
        return "Animateur/sessions/detail";
    }

    // ---- Feuille de présence ----
    @GetMapping("/presence/{id}")
    public String presenceForm(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User animateur = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser", animateur);
        model.addAttribute("session",       sessionService.findById(id));
        model.addAttribute("registrations", registrationService.findBySessionId(id));
        return "Animateur/presence";
    }

    @PostMapping("/presence/{id}/enregistrer")
    public String enregistrerPresences(@PathVariable Long id,
                                       @RequestParam(required = false) List<Long> presences,
                                       RedirectAttributes redirectAttributes) {
        // presences contient les IDs des enfants cochés comme présents
        registrationService.savePresences(id, presences);
        redirectAttributes.addFlashAttribute("succes", "Présences enregistrées avec succès.");
        return "redirect:/animateur/presence/" + id;
    }

    // ---- Planning ----
    @GetMapping("/planning")
    public String planning(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam(required = false) String semaine,
                           Model model) {
        User animateur = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser", animateur);
        model.addAttribute("weekLabel",     activityService.getWeekLabel(semaine));
        model.addAttribute("weekDays",      activityService.getWeekDays(semaine));
        model.addAttribute("timeSlots",     activityService.getTimeSlots());
        model.addAttribute("planningEvents",sessionService.getPlanningEvents(animateur.getId(), semaine));
        model.addAttribute("previousWeek",  activityService.getPreviousWeek(semaine));
        model.addAttribute("nextWeek",      activityService.getNextWeek(semaine));
        return "Animateur/planning";
    }

    // ---- Profil ----
    @GetMapping("/profil")
    public String profilForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User animateur = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser",      animateur);
        model.addAttribute("userForm",           animateur);
        model.addAttribute("passwordForm",       new com.manara.crm.dto.PasswordForm());
        model.addAttribute("activitesAssignees", activityService.findByAnimateurId(animateur.getId()));
        return "Animateur/profil";
    }

    @PostMapping("/profil")
    public String mettreAJourProfil(@AuthenticationPrincipal UserDetails userDetails,
                                    @ModelAttribute("userForm") User userForm,
                                    RedirectAttributes redirectAttributes) {
        User animateur = userService.findByEmail(userDetails.getUsername());
        userService.updateProfile(animateur.getId(), userForm);
        redirectAttributes.addFlashAttribute("succes", "Profil mis à jour avec succès.");
        return "redirect:/animateur/profil";
    }

    @PostMapping("/profil/mot-de-passe")
    public String changerMotDePasse(@AuthenticationPrincipal UserDetails userDetails,
                                    @ModelAttribute("passwordForm") com.manara.crm.dto.PasswordForm form,
                                    RedirectAttributes redirectAttributes) {
        User animateur = userService.findByEmail(userDetails.getUsername());
        try {
            userService.changePassword(animateur.getId(), form);
            redirectAttributes.addFlashAttribute("succes", "Mot de passe modifié avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/animateur/profil";
    }
}