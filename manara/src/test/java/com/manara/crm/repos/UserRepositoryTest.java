package com.manara.crm.repos;

import com.manara.crm.entity.User;
import com.manara.crm.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Test
    void testSaveAnimateur() {
        User animateur = new User();
        animateur.setFirstName("Paul");
        animateur.setLastName("Josh");
        animateur.setEmail("animateur1@test.com");
        animateur.setPhone("5141234567");
        animateur.setPassword(passwordEncoder.encode("123456"));
        animateur.setRole("ANIMATEUR");
        animateur.setVerified(true);
        animateur.setTwoFactorEnabled(false);
        animateur.setActive(true);
        animateur.setStatus("ACTIF");

        User savedUser = userRepository.save(animateur);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("animateur1@test.com");
        assertThat(found.get().getRole()).isEqualTo("ANIMATEUR");
        assertTrue(passwordEncoder.matches("123456", found.get().getPassword()));
    }
    @Test
    void testSaveAdmin() {
        User admin = new User();
        admin.setFirstName("James");
        admin.setLastName("Carson");
        admin.setEmail("admin1@test.com");
        admin.setPhone("5141234567");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRole("ADMIN");
        admin.setVerified(true);
        admin.setTwoFactorEnabled(false);
        admin.setActive(true);
        admin.setStatus("ACTIF");

        User savedUser = userRepository.save(admin);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("admin1@test.com");
        assertThat(found.get().getRole()).isEqualTo("ADMIN");
        assertTrue(passwordEncoder.matches("123456", found.get().getPassword()));
    }

   @Test
   void testSaveParent() {
       User parent = new User();
       parent.setFirstName("Sara");
       parent.setLastName("Smith");
       parent.setEmail("parent1@test.com");
       parent.setPhone("5143333333");
       parent.setPassword(passwordEncoder.encode("123456"));
       parent.setRole("PARENT");
       parent.setVerified(true);
       parent.setTwoFactorEnabled(false); // 2FA OFF
       parent.setActive(true);
       parent.setStatus("ACTIF");

       userRepository.save(parent);
   }
}