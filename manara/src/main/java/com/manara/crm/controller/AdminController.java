package com.manara.crm.controller;


import com.manara.crm.dto.NotificationForm;
import com.manara.crm.entity.Activity;
import com.manara.crm.entity.Notification;
import com.manara.crm.entity.Session;
import com.manara.crm.entity.User;
import com.manara.crm.repos.ActivityRepository;
import com.manara.crm.repos.NotificationRepository;
import com.manara.crm.repos.UserRepository;
import com.manara.crm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    SessionService sessionService;
    @Autowired
    ActivityService activityService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Autowired private RegistrationService registrationService;
    @Autowired private NotificationService notificationService;
    @Autowired
    ActivityRepository activityRepository;

    private void addConnectedUser(UserDetails userDetails, Model model) {
        model.addAttribute("connectedUser", userService.findByEmail(userDetails.getUsername()));
    }

    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("totalParents",          userService.countByRole("PARENT"));
        model.addAttribute("nouveauxParents",        userService.countNewThisMonth("PARENT"));
        model.addAttribute("totalActivites",         activityService.countActive());
        model.addAttribute("totalInscriptions",      registrationService.countAll());
        model.addAttribute("inscriptionsEnAttente",  registrationService.countPendingAll());
        model.addAttribute("recentesInscriptions",   registrationService.findRecent(10));
        return "Admin/dashboard";
    }

    // ================================================================
    //  UTILISATEURS
    // ================================================================

    @GetMapping("/utilisateurs")
    public String listeUtilisateurs(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("users", userService.findAll());
        return "Admin/users/list";
    }

    @GetMapping("/utilisateurs/{id}")
    public String detailUtilisateur(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        addConnectedUser(userDetails, model);
        User user = userService.findById(id);
        model.addAttribute("user",         user);
        model.addAttribute("inscriptions", registrationService.findByParentId(id));
        return "Admin/users/detail";
    }

    @GetMapping("/utilisateurs/nouveau")
    public String nouveauUtilisateurForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("activityForm", new Activity());
        model.addAttribute("userForm", new User());
        return "Admin/users/form";
    }

    @PostMapping("/utilisateurs/nouveau")
    public String creerUtilisateur(@ModelAttribute("userForm") User userForm,
                                   RedirectAttributes redirectAttributes,
                                   Model model,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        try {
            userService.creerUtilisateur(userForm);
            redirectAttributes.addFlashAttribute("succes",
                    "Compte créé pour " + userForm.getFirstName() + " " + userForm.getLastName() + ".");
            return "redirect:/utilisateurs";
        } catch (Exception e) {
            addConnectedUser(userDetails, model);
            model.addAttribute("erreur", e.getMessage());
            return "Admin/users/form";
        }
    }

    @GetMapping("/utilisateurs/{id}/modifier")
    public String modifierUtilisateurForm(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("userForm", userService.findById(id));
        return "Admin/users/form";
    }

    @PostMapping("/utilisateurs/{id}/modifier")
    public String modifierUtilisateur(@PathVariable Long id,
                                      @ModelAttribute("userForm") User userForm,
                                      RedirectAttributes redirectAttributes) {
        userService.updateProfile(id, userForm);
        redirectAttributes.addFlashAttribute("succes", "Utilisateur mis à jour.");
        return "redirect:/utilisateurs/" + id;
    }

    @PostMapping("/utilisateurs/{id}/toggle")
    public String toggleUtilisateur(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        User user = userService.toggleActive(id);
        redirectAttributes.addFlashAttribute("succes",
                "Compte " + (user.isActive() ? "réactivé" : "désactivé") + ".");
        return "redirect:/utilisateurs";
    }

    // ================================================================
    //  ACTIVITÉS
    // ================================================================

    @GetMapping("/activites")
    public String listeActivites(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("activities", activityService.findAll());
        return "Admin/activities/list";
    }

    @GetMapping("/activites/nouveau")
    public String nouvelleActiviteForm(@AuthenticationPrincipal UserDetails userDetails,  Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("activityForm", new com.manara.crm.entity.Activity());
        model.addAttribute("animateurs",   userService.findByRole("ANIMATEUR"));
        return "Admin/activities/form";
    }

    @PostMapping("/activites/nouveau")
    public String creerActivite(@ModelAttribute("activityForm") com.manara.crm.entity.Activity form,@RequestParam("animateurId") Long animateurId,
                                RedirectAttributes redirectAttributes) {

        User animateur = userRepository.findById(animateurId)
                .orElseThrow(() -> new RuntimeException("Animateur introuvable"));

        form.setAnimateur(animateur);
        activityService.save(form);
        redirectAttributes.addFlashAttribute("succes", "Activité créée avec succès.");
        return "redirect:/activites";
    }

    @GetMapping("/activites/{id}/modifier")
    public String modifierActiviteForm(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("activityForm", activityService.findById(id));
        model.addAttribute("animateurs",   userService.findByRole("ANIMATEUR"));
        return "Admin/activities/form";
    }

    @PostMapping("/activites/{id}/modifier")
    public String modifierActivite(@PathVariable Long id,
                                   @ModelAttribute("activityForm") com.manara.crm.entity.Activity form,
                                   RedirectAttributes redirectAttributes) {
        form.setId(id);
        activityService.save(form);
        redirectAttributes.addFlashAttribute("succes", "Activité mise à jour.");
        return "redirect:/activites";
    }

    @PostMapping("/activites/{id}/toggle")
    public String toggleActivite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        activityService.toggleActive(id);
        redirectAttributes.addFlashAttribute("succes", "Statut de l'activité modifié.");
        return "redirect:/activites";
    }

    // ================================================================
    //  SESSIONS
    // ================================================================

    @GetMapping("/sessions")
    public String listeSessions(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(required = false) Long activiteId,
                                Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("sessions",    activiteId != null
                ? sessionService.findByActivityId(activiteId)
                : sessionService.findAll());
        model.addAttribute("activities", activityService.findAll());
        return "Admin/sessions/list";
    }

    @GetMapping("/sessions/nouveau")
    public String nouvelleSessionForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addConnectedUser(userDetails, model);
        Session session = new Session();
        session.setActivity(new Activity());
        model.addAttribute("sessionForm", new Session());
        model.addAttribute("activities",  activityService.findAll());
        return "Admin/sessions/form";
    }

    @PostMapping("/sessions/nouveau")
    public String creerSession(@ModelAttribute("sessionForm") Session form,
                               @RequestParam("activityId") Long activityId,
                               RedirectAttributes redirectAttributes) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activité introuvable"));

        form.setActivity(activity);

        sessionService.save(form);
        redirectAttributes.addFlashAttribute("succes", "Session créée avec succès.");
        return "redirect:/sessions";
    }

    @GetMapping("/sessions/{id}/modifier")
    public String modifierSessionForm(@PathVariable Long id,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("sessionForm", sessionService.findById(id));
        model.addAttribute("activities",  activityService.findAll());
        return "Admin/sessions/form";
    }

    @PostMapping("/sessions/{id}/modifier")
    public String modifierSession(@PathVariable Long id,
                                  @ModelAttribute("sessionForm") com.manara.crm.entity.Session form,
                                  @RequestParam("activityId") Long activityId,
                                  RedirectAttributes redirectAttributes) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activité introuvable"));

        form.setActivity(activity);
        form.setId(id);
        sessionService.save(form);
        redirectAttributes.addFlashAttribute("succes", "Session mise à jour.");
        return "redirect:/sessions";
    }

    @PostMapping("/sessions/{id}/annuler")
    public String annulerSession(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        sessionService.annuler(id);
        redirectAttributes.addFlashAttribute("succes", "Session annulée.");
        return "redirect:/sessions";
    }

    // ================================================================
    //  INSCRIPTIONS
    // ================================================================

    @GetMapping("/inscriptions")
    public String listeInscriptions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("registrations", registrationService.findAll());
        return "Admin/registrations/list";
    }

    @GetMapping("/inscriptions/voir/{id}")
    public String detailInscription(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("registration", registrationService.findById(id));
        return "Admin/registrations/detail";
    }

    @PostMapping("/inscriptions/{id}/confirmer")
    public String confirmerInscription(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {
        User admin = userService.findByEmail(userDetails.getUsername());
        registrationService.confirmer(id, admin.getId());
        redirectAttributes.addFlashAttribute("succes", "Inscription confirmée.");
        return "redirect:/inscriptions";
    }

    @PostMapping("/inscriptions/{id}/annuler")
    public String annulerInscription(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        registrationService.annuler(id);
        redirectAttributes.addFlashAttribute("succes", "Inscription annulée.");
        return "redirect:/inscriptions";
    }

    @PostMapping("/inscriptions/{id}/promouvoir")
    public String promouvoir(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User admin = userService.findByEmail(userDetails.getUsername());
        registrationService.promouvoir(id, admin.getId());
        redirectAttributes.addFlashAttribute("succes", "Inscription promue depuis la liste d'attente.");
        return "redirect:/inscriptions";
    }



    @GetMapping("/notifications")
    public String listeNotifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addConnectedUser(userDetails, model);
        model.addAttribute("notifications", notificationService.findAllAdmin());
        model.addAttribute("activities",    activityService.findAll());
        model.addAttribute("parents",       userService.findByRole("PARENT"));
        model.addAttribute("notifForm",     new com.manara.crm.dto.NotificationForm());
        return "Admin/notifications/list";
    }

    @PostMapping("/notifications/envoyer")
    public String envoyerNotification(@ModelAttribute("notifForm") NotificationForm form,
                                      RedirectAttributes redirectAttributes) {

        try {

            if ("ALL".equals(form.getRecipientType())) {

                List<User> parents = userRepository.findByRole("PARENT");

                for (User parent : parents) {
                    Notification notif = buildNotification(form, parent);
                    notificationRepository.save(notif);
                }

            } else if ("ACTIVITY".equals(form.getRecipientType())) {

                if (form.getActivityId() == null) {
                    throw new RuntimeException("Choisis une activité");
                }

                Activity activity = activityRepository.findById(form.getActivityId())
                        .orElseThrow(() -> new RuntimeException("Activité introuvable"));

                List<User> parents = activity.getSessions()
                        .stream()
                        .flatMap(session -> session.getRegistrations().stream())
                        .map(registration -> registration.getParent())
                        .distinct()
                        .toList();

                for (User parent : parents) {
                    Notification notif = buildNotification(form, parent);
                    notificationRepository.save(notif);
                }

            } else if ("ONE".equals(form.getRecipientType())) {

                if (form.getParentId() == null) {
                    throw new RuntimeException("Choisis un parent");
                }

                User parent = userRepository.findById(form.getParentId())
                        .orElseThrow(() -> new RuntimeException("Parent introuvable"));

                Notification notif = buildNotification(form, parent);
                notificationRepository.save(notif);
            }

            redirectAttributes.addFlashAttribute("succes", "Notification envoyée avec succès.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/notifications";
    }
    private Notification buildNotification(NotificationForm form, User parent) {
        Notification notif = new Notification();
        notif.setUser(parent);
        notif.setSubject(form.getSubject());
        notif.setMessage(form.getMessage());
        notif.setType(form.getType());
        notif.setSentAt(LocalDateTime.now());
        notif.setEmailSent(true);
        return notif;
    }
    /*
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activities",activityService.afficherActivites());
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("title", "Dashboard");
        model.addAttribute("totalUsers", 120);
        model.addAttribute("totalActivities", 15);
        model.addAttribute("activeSubscriptions", 88);
        model.addAttribute("annualRevenue", 14500);
        return "admin/dashboard";
    }
*/
    @GetMapping("/admin/users")
    public String users(Model model) {
        model.addAttribute("users",userService.afficherUtilisateurs());
        model.addAttribute("activeMenu", "users");
        return "admin/users";
    }

    @GetMapping("/admin/activities")
    public String activities(Model model) {
        model.addAttribute("activeMenu", "activities");
        return "admin/activities";
    }

    @GetMapping("/évènements")
    public String showCalendar(Model model, Authentication authentication) {
        List<Session> sessions = sessionService.findAll();
        for (Session s : sessions) {
            System.out.println("ID: " + s.getId());
            System.out.println("Activité: " + s.getActivity().getName());
            System.out.println("Début: " + s.getStartDate());
            System.out.println("Fin: " + s.getEndDate());
            System.out.println("-------------------");
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

        model.addAttribute("title", "Calendrier des activités");
        model.addAttribute("sessions", sessions);

        // adapte selon ton système de rôles
       // model.addAttribute("isAdmin", true);

        return "admin/calendar";
    }
    @GetMapping("/api/session")
    @ResponseBody
    public List<Map<String, Object>> getSessions() {
        return sessionService.findAll().stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("title", s.getActivity().getName());
            map.put("description", s.getActivity().getDescription());
            map.put("start", s.getStartDate().toString());
            map.put("end", s.getEndDate().toString());
            map.put("capacity", s.getCapacity());
            return map;
        }).toList();
    }
    @GetMapping("/admin/statistics")
    public String statistics(Model model) {
        model.addAttribute("activeMenu", "statistics");
        return "admin/statistics";
    }

    @GetMapping("/admin/subscriptions")
    public String subscriptions(Model model) {
        model.addAttribute("activeMenu", "subscriptions");
        return "admin/subscriptions";
    }
}
