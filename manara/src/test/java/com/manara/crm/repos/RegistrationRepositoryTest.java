/*package com.manara.crm.repos;

import com.manara.crm.entity.*;
import com.manara.crm.repos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class RegistrationRepositoryTest {

    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ChildRepository childRepository;
    @Autowired private ActivityRepository activityRepository;
    @Autowired private SessionRepository sessionRepository;

    @Test
    void save_and_queries_should_work_end_to_end() {
        // Parent + enfant
        User parent = userRepository.save(new User("parent@test.com", "pass", "PARENT"));
        Child child = childRepository.save(new Child("Ali", "Diallo", LocalDate.of(2018, 5, 10), parent));

        // Activité + session
        Activity activity = activityRepository.save(new Activity("Yoga", "Cours de yoga"));
        Session session = sessionRepository.save(new Session(
                LocalDateTime.of(2026, 3, 1, 10, 0),
                LocalDateTime.of(2026, 3, 1, 11, 0),
                20,
                activity
        ));

        // Inscription
        Registration saved = registrationRepository.save(new Registration(child, session));

        assertThat(saved.getId()).isNotNull();

        // findByChildId
        List<Registration> byChild = registrationRepository.findByChildId(child.getId());
        assertThat(byChild).hasSize(1);
        assertThat(byChild.get(0).getSession().getId()).isEqualTo(session.getId());

        // findBySessionId
        List<Registration> bySession = registrationRepository.findBySessionId(session.getId());
        assertThat(bySession).hasSize(1);
        assertThat(bySession.get(0).getChild().getId()).isEqualTo(child.getId());

        // findByChildIdAndSessionId
        assertThat(registrationRepository.existsByChildIdAndSessionId(child.getId(), session.getId())).isTrue();

        assertThat(registrationRepository.findByChildIdAndSessionId(child.getId(), session.getId()))
                .isPresent();

        assertThat(registrationRepository.countBySessionId(session.getId())).isEqualTo(1);
    }

    @Test
    void unique_constraint_child_session_should_prevent_duplicates() {
        User parent = userRepository.save(new User("parent2@test.com", "pass", "PARENT"));
        Child child = childRepository.save(new Child("Maya", "Traore", LocalDate.of(2020, 7, 7), parent));

        Activity activity = activityRepository.save(new Activity("Natation", "Piscine"));
        Session session = sessionRepository.save(new Session(
                LocalDateTime.of(2026, 3, 5, 9, 0),
                LocalDateTime.of(2026, 3, 5, 10, 0),
                10,
                activity
        ));

        registrationRepository.save(new Registration(child, session));

        // 2e inscription identique => doit échouer à cause de la contrainte unique
        assertThatThrownBy(() -> {
            registrationRepository.saveAndFlush(new Registration(child, session));
        }).isInstanceOf(RuntimeException.class);
    }
}*/