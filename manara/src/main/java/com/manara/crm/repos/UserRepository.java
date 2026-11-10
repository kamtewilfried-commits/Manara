package com.manara.crm.repos;

import com.manara.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailVerificationToken(String token);
    @Query("SELECT u FROM User  u WHERE u.email = :email")
    //@Param est utilisé pour lier le paramètre method au paramètre Query.
    public User getUtilisateurByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.email = :email")
    User getUserByEmailFetchRole(@Param("email") String email);

    List<User> findByRole(String role);

    long countByRole(String role);

    long countNewThisMonthByRole(String role);
    @Query("""
SELECT u.role, COUNT(u)
FROM User u
GROUP BY u.role
""")
    List<Object[]> countUsersByRole();
    @Query("""
SELECT YEAR(u.createdAt), MONTH(u.createdAt), COUNT(u)
FROM User u
GROUP BY YEAR(u.createdAt), MONTH(u.createdAt)
ORDER BY YEAR(u.createdAt), MONTH(u.createdAt)
""")
    List<Object[]> newUsersPerMonth();
    @Query("""
SELECT u.active, COUNT(u)
FROM User u
GROUP BY u.active
""")
    List<Object[]> activeUsers();
    @Query("""
SELECT u.verified, COUNT(u)
FROM User u
GROUP BY u.verified
""")
    List<Object[]> verifiedUsers();
    @Query("""
SELECT u.firstName, u.lastName, SIZE(u.children)
FROM User u
WHERE u.role = 'PARENT'
""")
    List<Object[]> childrenPerParent();

}