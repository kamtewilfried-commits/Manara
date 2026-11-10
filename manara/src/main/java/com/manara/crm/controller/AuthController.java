package com.manara.crm.controller;

import com.manara.crm.entity.User;
import com.manara.crm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

   /*@GetMapping("/")
    public String accueil(Model model) {
        model.addAttribute("activities", userService.findActiveActivities());
        return "accueil";
    }
*/
    // ---- Connexion ----
    // La page est gérée par Spring Security, on affiche juste le template
  /*  @GetMapping("/connexion")
    public String connexion(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null)  model.addAttribute("erreur", "Courriel ou mot de passe incorrect.");
        if (logout != null) model.addAttribute("succes", "Vous avez été déconnecté.");
        return "connexion";
    }
*/
    // ---- Inscription ----
   /* @GetMapping("/inscription")
    public String inscriptionForm(Model model) {
        model.addAttribute("userForm", new User());
        return "creationcompte";
    }
*/
    /*@PostMapping("/inscription")
    public String inscrire(@Valid @ModelAttribute("userForm") User userForm,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            return "creationcompte";
        }
        try {
            userService.inscrireParent(userForm);
            redirectAttributes.addFlashAttribute("succes",
                    "Compte créé avec succès ! Vous pouvez maintenant vous connecter.");
            return "redirect:/connexion";
        } catch (Exception e) {
            model.addAttribute("erreur", e.getMessage());
            return "creationcompte";
        }
    }*/
}