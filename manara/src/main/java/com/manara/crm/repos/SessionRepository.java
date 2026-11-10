package com.manara.crm.repos;

import com.manara.crm.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByActivityId(Long activityId);

    List<Session> findByStartDateBetween(LocalDateTime from, LocalDateTime to);

    List<Session> findByStartDateAfterOrderByStartDateAsc(LocalDateTime dateTime);

    @Query("SELECT s FROM Session s JOIN FETCH s.activity")
    List<Session> findAllWithActivity();

    List<Session> findByActivityAnimateurId(Long animateurId);

    @Query("""
        SELECT s FROM Session s
        WHERE s.activity.animateur.id = :animateurId
        AND :now BETWEEN s.startDate AND s.endDate
    """)
    Optional<Session> findCurrentSession(@Param("animateurId") Long animateurId,
                                         @Param("now") LocalDateTime now);

    @Query("""
        SELECT s FROM Session s
        WHERE s.activity.animateur.id = :animateurId
        AND s.startDate > :now
        ORDER BY s.startDate ASC
    """)
    List<Session> findUpcomingByAnimateur(@Param("animateurId") Long animateurId,
                                          @Param("now") LocalDateTime now);

    @Query("""
        SELECT COUNT(s) FROM Session s
        WHERE s.activity.animateur.id = :animateurId
        AND s.startDate BETWEEN :start AND :end
    """)
    long countThisWeek(@Param("animateurId") Long animateurId,
                       @Param("start") LocalDateTime start,
                       @Param("end") LocalDateTime end);

    List<Session> findByAnimateurId(Long animateurId);

    @Query("""
    SELECT new com.manara.crm.dto.SessionTimeStatsDTO(
        YEAR(s.startDate),
        MONTH(s.startDate),
        COUNT(s.id)
    )
    FROM Session s
    GROUP BY YEAR(s.startDate), MONTH(s.startDate)
    ORDER BY YEAR(s.startDate), MONTH(s.startDate)
""")
    List<com.manara.crm.dto.SessionTimeStatsDTO> sessionsByMonth();

    @Query("""
    SELECT new com.manara.crm.dto.AnimatorSessionStatsDTO(
        u.firstName,
        u.lastName,
        COUNT(s.id)
    )
    FROM Session s
    JOIN s.activity.animateur u
    GROUP BY u.id, u.firstName, u.lastName
    ORDER BY COUNT(s.id) DESC
""")
    List<com.manara.crm.dto.AnimatorSessionStatsDTO> sessionsByAnimator();
    List<Session> findByStartDateBeforeOrderByStartDateDesc(LocalDateTime date);

    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.animateur = null WHERE s.animateur.id = :userId")
    int detachAnimateur(@Param("userId") Long userId);
}