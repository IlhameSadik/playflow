package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;
import com.example.demo.entity.UserStatus;
import org.springframework.data.repository.query.Param;
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
List<User> findByStatus(UserStatus status);
    boolean existsByEmail(String email);
@Query("""
    SELECT COUNT(u)
    FROM User u
    WHERE u.status = :status
""")
long countByStatus(@Param("status") UserStatus status);
boolean existsByIdAndRoles_Name(Long userId, String roleName);
}