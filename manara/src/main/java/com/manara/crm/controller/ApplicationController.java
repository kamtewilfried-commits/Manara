package com.manara.crm.controller;

import com.manara.crm.entity.User;
import com.manara.crm.repos.UserRepository;
import com.manara.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller

public class ApplicationController {
@Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

   /*@GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }

        model.addAttribute("email", authentication.getName());
        return "acceuil";
    }*/
   @GetMapping("/")
   public String home(Authentication authentication, Model model) {
       if (authentication == null) {
           return "redirect:/login";
       }

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

       model.addAttribute("activities", userService.findActiveActivities());

       return "pageAcceuil";
   }
    @GetMapping("/portailParents")
    public String portailParent() {

        return "portailParents";
    }

    @GetMapping("/planning")
    public String pagePlanning() {

        return "planning";
    }
    @GetMapping("/inscriptionActivités")
    public String IncrtiptionActivités() {

        return "inscription";
    }


    @GetMapping("/administration")
    public String Admin() {

        return "administration";
    }
    @GetMapping("/index")
    public String Index(){
        return "index";
    }


    @GetMapping("/videocall")
    public String VideoCall(){
        return "videocall";
    }
   /* @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }*/

    @PostMapping("/utilisateurs/save")
    public String saveUser(@ModelAttribute User utilisateur) {
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        utilisateur.setActive(true);
        userRepository.save(utilisateur);
        return "redirect:/login";
    }
}