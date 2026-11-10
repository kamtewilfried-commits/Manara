package com.manara.crm.repos;

import com.manara.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<User, Long> {

    String PARENT_ROLE = "PARENT";

    Optional<User> findByIdAndRole(Long id, String role);

    Optional<User> findByEmailAndRole(String email, String role);

    boolean existsByEmailAndRole(String email, String role);

    boolean existsByEmailAndRoleAndIdNot(String email, String role, Long id);

    List<User> findAllByRole(String role);

    default Optional<User> findParentById(Long id) {
        return findByIdAndRole(id, PARENT_ROLE);
    }

    default Optional<User> findParentByEmail(String email) {
        return findByEmailAndRole(email, PARENT_ROLE);
    }

    default boolean existsParentByEmail(String email) {
        return existsByEmailAndRole(email, PARENT_ROLE);
    }

    default boolean existsParentByEmailAndIdNot(String email, Long id) {
        return existsByEmailAndRoleAndIdNot(email, PARENT_ROLE, id);
    }

    default List<User> findAllParents() {
        return findAllByRole(PARENT_ROLE);
    }
}
