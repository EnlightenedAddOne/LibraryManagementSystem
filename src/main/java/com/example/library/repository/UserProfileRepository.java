package com.example.library.repository;

import com.example.library.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    UserProfile findByUser_Id(Integer userId);
}