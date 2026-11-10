/*package com.manara.crm.repos;

import com.manara.crm.entity.Child;
import com.manara.crm.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class ChildRepositoryTest {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_child_requires_parent() {
       // User parent = userRepository.save(new User("parent@test.com", "pass", "PARENT"));

        Child saved = childRepository.save(
                new Child("Ali", "Diallo", LocalDate.of(2018, 5, 10), parent)
        );

        assertThat(saved.getId()).isNotNull();
        assertThat(childRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findByParent_should_return_children() {
        //User p1 = userRepository.save(new User("p1@test.com", "pass", "PARENT"));
        //User p2 = userRepository.save(new User("p2@test.com", "pass", "PARENT"));

        childRepository.save(new Child("Sara", "Kone", LocalDate.of(2017, 1, 1), p1));
        childRepository.save(new Child("Yanis", "Kone", LocalDate.of(2019, 2, 2), p1));
        childRepository.save(new Child("Nora", "Ben", LocalDate.of(2016, 3, 3), p2));

        List<Child> p1Children = childRepository.findByParent(p1);

        assertThat(p1Children).hasSize(2);
        assertThat(p1Children)
                .extracting(Child::getFirstName)
                .containsExactlyInAnyOrder("Sara", "Yanis");
    }

    @Test
    void existsByParentIdAndFirstNameIgnoreCaseAndLastNameIgnoreCase_should_work() {
        User p = userRepository.save(new User("p3@test.com", "pass", "PARENT"));

        childRepository.save(new Child("Maya", "Traore", LocalDate.of(2020, 7, 7), p));

        assertThat(
                childRepository.existsByParentIdAndFirstNameIgnoreCaseAndLastNameIgnoreCase(
                        p.getId(), "mAyA", "tRAOrE"
                )
        ).isTrue();

        assertThat(
                childRepository.existsByParentIdAndFirstNameIgnoreCaseAndLastNameIgnoreCase(
                        p.getId(), "John", "Doe"
                )
        ).isFalse();
    }
}*/