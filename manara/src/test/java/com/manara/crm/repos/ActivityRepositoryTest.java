package com.manara.crm.repos;

import com.manara.crm.entity.Activity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class ActivityRepositoryTest {
    @Autowired
    private ActivityRepository activityRepository;

    @Test
    void save_and_findById() {
        Activity a = new Activity("Yoga", "Cours de yoga");
        Activity saved = activityRepository.save(a);

        Optional<Activity> found = activityRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Yoga");
        assertThat(found.get().getDescription()).isEqualTo("Cours de yoga");
    }

    @Test
    void findByNameIgnoreCase_should_work() {
        activityRepository.save(new Activity("Natation", "Piscine"));

        Optional<Activity> found = activityRepository.findByNameIgnoreCase("nAtAtIoN");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Natation");
    }

    @Test
    void existsByNameIgnoreCase_should_work() {
        activityRepository.save(new Activity("Boxe", "Entraînement"));

        assertThat(activityRepository.existsByNameIgnoreCase("boxe")).isTrue();
        assertThat(activityRepository.existsByNameIgnoreCase("karaté")).isFalse();
    }

    @Test
    void delete_should_remove_entity() {
        Activity saved = activityRepository.save(new Activity("Danse", "Hip-hop"));

        activityRepository.deleteById(saved.getId());

        assertThat(activityRepository.findById(saved.getId())).isEmpty();
    }

}