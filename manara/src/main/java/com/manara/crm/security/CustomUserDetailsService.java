package com.manara.crm.security;

import com.manara.crm.entity.User;
import com.manara.crm.repos.UserRepository;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override

    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User utilisateur = repo.getUtilisateurByEmail(email);


        if (utilisateur == null) {
            throw new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email);
        }
        if (!utilisateur.isVerified()) {
            throw new RuntimeException("Compte non vérifié");
        }
        return new CustomUserDetails(utilisateur);
    }
}
