package com.manara.crm.controller;

import com.manara.crm.entity.Child;
import com.manara.crm.entity.User;
import com.manara.crm.service.ChildService;
import com.manara.crm.service.NotificationService;
import com.manara.crm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parent/enfants")
public class ChildController {

    @Autowired private ChildService        childService;
    @Autowired private UserService         userService;
    @Autowired private NotificationService notificationService;

    // ---- Liste des enfants ----
    @GetMapping
    public String liste(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser",       parent);
        model.addAttribute("children",            childService.findByParentId(parent.getId()));
        model.addAttribute("unreadNotifications", notificationService.countUnread(parent.getId()));
        return "Parent/children/list";
    }

    // ---- Formulaire ajout ----
    @GetMapping("/ajouter")
    public String ajouterForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("connectedUser",       parent);
        model.addAttribute("childForm",           new Child());
        model.addAttribute("unreadNotifications", notificationService.countUnread(parent.getId()));
        return "Parent/children/add";
    }

    @PostMapping("/ajouter")
    public String ajouter(@AuthenticationPrincipal UserDetails userDetails,
                          @Valid @ModelAttribute("childForm") Child childForm,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        User parent = userService.findByEmail(userDetails.getUsername());
        if (result.hasErrors()) {
            model.addAttribute("connectedUser", parent);
            return "Parent/children/add";
        }
        childForm.setParent(parent);
        childService.save(childForm);
        redirectAttributes.addFlashAttribute("succes",
                childForm.getFirstName() + " a été ajouté avec succès.");
        return "redirect:/parent/enfants";
    }

    // ---- Formulaire modification ----
    @GetMapping("/{id}/modifier")
    public String modifierForm(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User parent = userService.findByEmail(userDetails.getUsername());
        Child child  = childService.findById(id);

        // Sécurité : le parent ne peut modifier que ses propres enfants
        if (!child.getParent().getId().equals(parent.getId())) {
            return "redirect:/parent/enfants";
        }

        model.addAttribute("connectedUser",       parent);
        model.addAttribute("childForm",           child);
        model.addAttribute("unreadNotifications", notificationService.countUnread(parent.getId()));
        return "Parent/children/edit";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails userDetails,
                           @Valid @ModelAttribute("childForm") Child childForm,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        User parent = userService.findByEmail(userDetails.getUsername());
        if (result.hasErrors()) {
            model.addAttribute("connectedUser", parent);
            return "Parent/children/edit";
        }
        childForm.setId(id);
        childForm.setParent(parent);
        childService.save(childForm);
        redirectAttributes.addFlashAttribute("succes", "Profil mis à jour avec succès.");
        return "redirect:/parent/enfants";
    }

    // ---- Suppression ----
    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        User parent = userService.findByEmail(userDetails.getUsername());
        Child child  = childService.findById(id);

        if (!child.getParent().getId().equals(parent.getId())) {
            return "redirect:/parent/enfants";
        }

        childService.deleteById(id);
        redirectAttributes.addFlashAttribute("succes",
                child.getFirstName() + " a été supprimé.");
        return "redirect:/parent/enfants";
    }
}