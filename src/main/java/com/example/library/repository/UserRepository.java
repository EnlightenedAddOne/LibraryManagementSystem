package com.example.library.repository;

import com.example.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u JOIN u.profile p WHERE u.username LIKE %:keyword% OR p.realName LIKE %:keyword%")
    List<User> findByUsernameOrRealNameContaining(@Param("keyword") String keyword);
    
    @Query("SELECT u FROM User u JOIN u.profile p WHERE p.phone LIKE %:phone%")
    List<User> findByPhoneContaining(@Param("phone") String phone);
    
    @Query("SELECT u FROM User u JOIN u.profile p WHERE p.email LIKE %:email%")
    List<User> findByEmailContaining(@Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.createdAt = :date")
    List<User> findByCreatedAt(@Param("date") LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByCreatedAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}