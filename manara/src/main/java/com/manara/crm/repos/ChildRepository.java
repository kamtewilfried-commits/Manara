package com.manara.crm.repos;

import com.manara.crm.entity.Child;
import com.manara.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {

    //  récupérer les enfants d’un parent
    List<Child> findByParent(User parent);

    // recherche par parent + nom
    List<Child> findByParentId(Long parentId);

    boolean existsByParentIdAndFirstNameIgnoreCaseAndLastNameIgnoreCase(
            Long parentId, String firstName, String lastName
    );

    long countByParentId(Long parentId);

    boolean existsByIdAndParentId(Long childId, Long parentId);
}