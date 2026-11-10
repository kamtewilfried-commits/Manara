package com.manara.crm.service;



import com.manara.crm.dto.PasswordForm;
import com.manara.crm.entity.User;
import com.manara.crm.repos.ActivityRepository;
import com.manara.crm.repos.NotificationRepository;
import com.manara.crm.repos.RegistrationRepository;
import com.manara.crm.repos.SessionRepository;
import com.manara.crm.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private SessionRepository sessionRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + email));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public long countByRole(String role) {
        return userRepository.countByRole(role);
    }

    public long countNewThisMonth(String role) {
        return userRepository.countNewThisMonthByRole(role);
    }

    // ---- Inscription parent (auto-création) ----
    public User inscrireParent(User user) {
        if (userRepository.existsByEmail(user.getEmail().toLowerCase().trim())) {
            throw new RuntimeException("Cette adresse courriel est déjà utilisée.");
        }
        user.setEmail(user.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("PARENT");
        user.setActive(true);
        return userRepository.save(user);
    }


    // ---- Création par admin (animateur ou admin) ----
    public User creerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail().toLowerCase().trim())) {
            throw new RuntimeException("Cette adresse courriel est déjà utilisée.");
        }
        user.setEmail(user.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        return userRepository.save(user);
    }

    // ---- Mise à jour du profil ----
    public User updateProfile(Long id, User form) {
        User user = findById(id);
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setEmail(form.getEmail().toLowerCase().trim());
        user.setPhone(form.getPhone());
        return userRepository.save(user);
    }

    // ---- Changement de mot de passe ----
   public void changePassword(Long id, PasswordForm form) {
        User user = findById(id);
        if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Le mot de passe actuel est incorrect.");
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new RuntimeException("Les nouveaux mots de passe ne correspondent pas.");
        }
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
    }

    // ---- Activer / désactiver ----
    public User toggleActive(Long id) {
        User user = findById(id);
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    public List<Object> findActiveActivities() {
        return java.util.Collections.emptyList();
    }
    public void creerUtilisateur(User utilisateur) {
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        userRepository.save(utilisateur);
    }
//private static final List<User> USER_LIST = new ArrayList<>();


    public List<User> afficherUtilisateurs(){
        return ( List<User> ) userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        notificationRepository.deleteByUserId(id);
        registrationRepository.detachParent(id);
        registrationRepository.detachConfirmedBy(id);
        activityRepository.detachAnimateur(id);
        sessionRepository.detachAnimateur(id);
        userRepository.delete(user);
    }
}
