package com.manara.crm.repos;

import com.manara.crm.dto.ActivityRegistrationStatsDTO;
import com.manara.crm.dto.ActivityRevenueDTO;
import com.manara.crm.dto.MonthlyRevenueDTO;
import com.manara.crm.dto.StatusStatsDTO;
import com.manara.crm.entity.Child;
import com.manara.crm.entity.PaymentTransaction;
import com.manara.crm.entity.Registration;
import com.manara.crm.entity.Session;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByChildId(Long childId);

    boolean existsByChildAndSession(Child child, Session session);

    List<Registration> findByTransaction(PaymentTransaction transaction);

    List<Registration> findBySessionId(Long sessionId);

    Optional<Registration> findByChildIdAndSessionId(Long childId, Long sessionId);



    boolean existsByChildIdAndSessionId(Long childId, Long sessionId);
    @Query("""
    SELECT r FROM Registration r
    WHERE r.session.activity.animateur.id = :animateurId
""")
    List<Registration> findByAnimateurId(@Param("animateurId") Long animateurId);

    long countBySessionId(Long sessionId);
    @Query("""
    SELECT r FROM Registration r
    ORDER BY r.id DESC
""")
    List<Registration> findRecent(org.springframework.data.domain.Pageable pageable);
    //List<Registration> findRecent(PageRequest of);
    List<Registration> findByChildParentId(Long parentId);

    List<Registration> findByParentId(Long parentId);
   @Query("SELECT r FROM Registration r WHERE r.parent.id = :parentId AND r.session.startDate > :now")
   List<Registration> findUpcomingByParentId(@Param("parentId") Long parentId,
                                             @Param("now") LocalDateTime now);

    long countActivesByParentId(Long parentId);
    List<Registration> findBySessionActivityAnimateurId(Long animateurId);
    long countPendingByParentId(Long parentId);
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.status = 'ACTIVE'")
    long countAllActive();
    //long countAllActive();

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.status = 'PENDING'")
    long countAllPending();
    //long countAllPending();
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.session.animateur.id = :animateurId")
    long countChildrenByAnimateurId(@Param("animateurId") Long animateurId);
   //long countChildrenByAnimateurId(Long animateurId);

    //List<Registration> findUpcomingByParentId(Long parentId, LocalDateTime now);

    List<Registration> findWaitlistBySessionId(Long sessionId);

    @Query("""
    SELECT new com.manara.crm.dto.ActivityRegistrationStatsDTO(
        a.name,
        COUNT(r.id)
    )
    FROM Registration r
    JOIN r.session s
    JOIN s.activity a
    GROUP BY a.name
    ORDER BY COUNT(r.id) DESC
""")
    List<ActivityRegistrationStatsDTO> countRegistrationsByActivity();
    @Query("""
    SELECT new com.manara.crm.dto.ActivityRevenueDTO(
        a.name,
        SUM(
            (FUNCTION('TIMESTAMPDIFF', MINUTE, s.startDate, s.endDate) / 60.0) * a.prixHoraire
        )
    )
    FROM Registration r
    JOIN r.session s
    JOIN s.activity a
    WHERE r.status = 'CONFIRMED'
    GROUP BY a.name
    ORDER BY SUM(
        (FUNCTION('TIMESTAMPDIFF', MINUTE, s.startDate, s.endDate) / 60.0) * a.prixHoraire
    ) DESC
""")
    List<ActivityRevenueDTO> revenueByActivity();
    @Query("""
    SELECT new com.manara.crm.dto.MonthlyRevenueDTO(
        YEAR(r.createdAt),
        MONTH(r.createdAt),
        SUM(
            (FUNCTION('TIMESTAMPDIFF', MINUTE, s.startDate, s.endDate) / 60.0) * a.prixHoraire
        )
    )
    FROM Registration r
    JOIN r.session s
    JOIN s.activity a
    WHERE r.status = 'CONFIRMED'
    GROUP BY YEAR(r.createdAt), MONTH(r.createdAt)
    ORDER BY YEAR(r.createdAt), MONTH(r.createdAt)
""")
    List<MonthlyRevenueDTO> revenueByMonth();
    @Query("""
    SELECT new com.manara.crm.dto.StatusStatsDTO(
        r.status,
        COUNT(r.id)
    )
    FROM Registration r
    GROUP BY r.status
""")
    List<StatusStatsDTO> countByStatus();

    @Modifying
    @Transactional
    @Query("UPDATE Registration r SET r.parent = null WHERE r.parent.id = :userId")
    int detachParent(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Registration r SET r.confirmedBy = null WHERE r.confirmedBy.id = :userId")
    int detachConfirmedBy(@Param("userId") Long userId);



}
