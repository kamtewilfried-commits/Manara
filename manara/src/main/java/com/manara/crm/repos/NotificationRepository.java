package com.manara.crm.repos;

import com.manara.crm.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    // Récentes (pour dashboard)
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId " +
            "ORDER BY n.sentAt DESC")
    List<Notification> findRecentByUserId(@Param("userId") Long userId,
                                          org.springframework.data.domain.Pageable pageable);

    // Marquer toutes lues pour un utilisateur
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllReadByUserId(@Param("userId") Long userId);

    // Emails non encore envoyés (pour retry)
    @Query("SELECT n FROM Notification n WHERE n.emailSent = false ORDER BY n.sentAt ASC")
    List<Notification> findUnsentEmails();

    // Historique admin
    @Query("SELECT n FROM Notification n ORDER BY n.sentAt DESC")
    List<Notification> findAllOrderBySentAtDesc();

    long deleteByUserId(Long userId);
}