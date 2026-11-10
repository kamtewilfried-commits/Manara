package com.manara.crm.repos;

import com.manara.crm.dto.ActivityCategoryPriceDTO;
import com.manara.crm.dto.ActivityCategoryStatsDTO;
import com.manara.crm.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {


    Optional<Activity> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    List<Activity> findByAnimateurId(Long animateurId);

    List<Activity> findByActiveTrue();

    long countByActiveTrue();

    @Query("""
    SELECT new com.manara.crm.dto.ActivityCategoryStatsDTO(
        COALESCE(a.categorie, 'Sans catégorie'),
        COUNT(a.id)
    )
    FROM Activity a
    GROUP BY a.categorie
    ORDER BY COUNT(a.id) DESC
""")
    List<ActivityCategoryStatsDTO> countActivitiesByCategorie();

    @Query("""
    SELECT new com.manara.crm.dto.ActivityCategoryPriceDTO(
        COALESCE(a.categorie, 'Sans catégorie'),
        AVG(a.prixHoraire)
    )
    FROM Activity a
    GROUP BY a.categorie
    ORDER BY AVG(a.prixHoraire) DESC
""")
    List<ActivityCategoryPriceDTO> averagePriceByCategorie();

    long countByActiveFalse();

    @Query("SELECT a FROM Activity a LEFT JOIN FETCH a.sessions WHERE a.active = true")
    List<Activity> findActiveWithSessions();

    @Modifying
    @Transactional
    @Query("UPDATE Activity a SET a.animateur = null WHERE a.animateur.id = :userId")
    int detachAnimateur(@Param("userId") Long userId);
}