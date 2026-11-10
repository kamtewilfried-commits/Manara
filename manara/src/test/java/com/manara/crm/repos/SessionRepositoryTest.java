package com.manara.crm.repos;

import com.manara.crm.entity.Activity;
import com.manara.crm.entity.Session;
import com.manara.crm.repos.ActivityRepository;
import com.manara.crm.repos.SessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ActivityRepository activityRepository;
/*
    @Test
    void save_session_requires_activity() {
        Activity activity = activityRepository.save(new Activity("Yoga", "Cours de yoga"));

        Session saved = sessionRepository.save(
                new Session(
                        LocalDateTime.of(2026, 3, 1, 10, 0),
                        LocalDateTime.of(2026, 3, 1, 11, 0),
                        20,
                        activity
                )
        );

        assertThat(saved.getId()).isNotNull();
        assertThat(sessionRepository.findById(saved.getId())).isPresent();
        assertThat(sessionRepository.findById(saved.getId())).get().extracting(s -> s.getActivity().getId())
                .isEqualTo(activity.getId());
    }

    @Test
    void findByActivityId_should_return_sessions_of_activity() {
        Activity a1 = activityRepository.save(new Activity("Natation", "Piscine"));
        Activity a2 = activityRepository.save(new Activity("Danse", "Hip-hop"));

        sessionRepository.save(new Session(
                LocalDateTime.of(2026, 2, 25, 9, 0),
                LocalDateTime.of(2026, 2, 25, 10, 0),
                15,
                a1
        ));
        sessionRepository.save(new Session(
                LocalDateTime.of(2026, 2, 26, 9, 0),
                LocalDateTime.of(2026, 2, 26, 10, 0),
                15,
                a1
        ));
        sessionRepository.save(new Session(
                LocalDateTime.of(2026, 2, 27, 9, 0),
                LocalDateTime.of(2026, 2, 27, 10, 0),
                15,
                a2
        ));

        List<Session> a1Sessions = sessionRepository.findByActivityId(a1.getId());

        assertThat(a1Sessions).hasSize(2);
        assertThat(a1Sessions)
                .extracting(Session::getStartDate)
                .containsExactlyInAnyOrder(
                        LocalDateTime.of(2026, 2, 25, 9, 0),
                        LocalDateTime.of(2026, 2, 26, 9, 0)
                );
    }

    @Test
    void findByStartDateBetween_should_work() {
        Activity a = activityRepository.save(new Activity("Boxe", "Entraînement"));

        sessionRepository.save(new Session(
                LocalDateTime.of(2026, 2, 20, 9, 0),
                LocalDateTime.of(2026, 2, 20, 10, 0),
                10,
                a
        ));
        sessionRepository.save(new Session(
                LocalDateTime.of(2026, 2, 22, 9, 0),
                LocalDateTime.of(2026, 2, 22, 10, 0),
                10,
                a
        ));
        sessionRepository.save(new Session(
                LocalDateTime.of(2026, 2, 28, 9, 0),
                LocalDateTime.of(2026, 2, 28, 10, 0),
                10,
                a
        ));

        List<Session> results = sessionRepository.findByStartDateBetween(
                LocalDateTime.of(2026, 2, 21, 0, 0),
                LocalDateTime.of(2026, 2, 27, 23, 59)
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStartDate()).isEqualTo(LocalDateTime.of(2026, 2, 22, 9, 0));
    }

    @Test
    void findByStartDateAfterOrderByStartDateAsc_should_return_sorted_future_sessions() {
        Activity a = activityRepository.save(new Activity("Karaté", "Arts martiaux"));

        sessionRepository.save(new Session(
                LocalDateTime.of(2026, 3, 10, 9, 0),
                LocalDateTime.of(2026, 3, 10, 10, 0),
                10,
                a
        ));
        sessionRepository.save(new Session(
                LocalDateTime.of(2026, 3, 1, 9, 0),
                LocalDateTime.of(2026, 3, 1, 10, 0),
                10,
                a
        ));

        List<Session> results = sessionRepository.findByStartDateAfterOrderByStartDateAsc(
                LocalDateTime.of(2026, 2, 28, 0, 0)
        );

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getStartDate()).isEqualTo(LocalDateTime.of(2026, 3, 1, 9, 0));
        assertThat(results.get(1).getStartDate()).isEqualTo(LocalDateTime.of(2026, 3, 10, 9, 0));
    }*/
}