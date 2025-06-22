package com.example.library.dto;

import com.example.library.entity.User;
import com.example.library.entity.UserProfile;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer userId;
    private String username;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfile profile;

    public static UserResponseDTO fromUser(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setProfile(user.getProfile());
        return dto;
    }
}