package com.example.library.service;

import com.example.library.entity.User;
import com.example.library.entity.UserProfile;
import com.example.library.repository.UserProfileRepository;
import com.example.library.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.security.MessageDigest;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Value("${jwt.secret}")
    private String secretKey; // 替换为你的密钥
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1小时
    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24小时

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public Map<String, Object> login(String username, String password) {
        // 验证用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 对输入的密码进行 SHA-256 加密
        String encryptedPassword = encryptPassword(password);

        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("用户名或密码错误");
        }
        Integer userId = user.getUserId();
        // 生成 token 和 refreshToken
        String token = generateToken(userId, EXPIRATION_TIME);
        String refreshToken = generateToken(userId, REFRESH_EXPIRATION_TIME);

        // 返回 token、refreshToken 和角色信息
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("refreshToken", refreshToken);
        response.put("role", user.getRole()); // 添加角色信息
        return response;
    }
    
    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    private String generateToken(Integer userId, long expirationTime) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 设置主题为用户 ID
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 设置过期时间
                .claim("userId", userId) // 添加用户 ID 到 Token 的 Claims
                .signWith(SignatureAlgorithm.HS256, secretKey) // 使用 HS256 算法和密钥签名
                .compact(); // 生成最终的 JWT 字符串
    }

    public Integer getUserIdFromToken(String token) {
        try {
            // 处理 Bearer token
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", Integer.class); // 直接获取为 Integer 类型
        } catch (Exception e) {
            e.printStackTrace(); // 添加异常打印，方便调试
            throw new RuntimeException("无效的 Token");
        }
    }

    public Map<String, Object> register(User user, UserProfile profile) {
        // 创建用户
        user.setPassword(encryptPassword(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        // 关联并保存用户资料
        profile.setUser(savedUser);
        userProfileRepository.save(profile);
        
        return Map.of("userId", savedUser.getUserId());
    }

    public UserProfile getUserProfile(Integer userId) {
        return userProfileRepository.findByUser_Id(userId);
    }

    public UserProfile updateUserProfile(Integer userId, UserProfile profile) {
        // 使用正确的方法名 findByUser_UserId
        UserProfile existingProfile = userProfileRepository.findByUser_Id(userId);
        if (existingProfile == null) {
            throw new RuntimeException("用户资料不存在");
        }
        
        // 更新资料
        BeanUtils.copyProperties(profile, existingProfile, "profileId", "userId", "createdAt");
        return userProfileRepository.save(existingProfile);
    }

    public User updateUser(Integer id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新基本信息
        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getPassword() != null) {
            // 加密新密码
            existingUser.setPassword(encryptPassword(updatedUser.getPassword()));
        }
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }

        // 更新用户档案信息
        if (updatedUser.getProfile() != null) {
            UserProfile existingProfile = existingUser.getProfile();
            if (existingProfile == null) {
                existingProfile = new UserProfile();
                existingProfile.setUser(existingUser);
            }
            
            UserProfile updatedProfile = updatedUser.getProfile();
            if (updatedProfile.getRealName() != null) {
                existingProfile.setRealName(updatedProfile.getRealName());
            }
            if (updatedProfile.getNickname() != null) {
                existingProfile.setNickname(updatedProfile.getNickname());
            }
            if (updatedProfile.getEmail() != null) {
                existingProfile.setEmail(updatedProfile.getEmail());
            }
            if (updatedProfile.getPhone() != null) {
                existingProfile.setPhone(updatedProfile.getPhone());
            }
            if (updatedProfile.getSchool() != null) {
                existingProfile.setSchool(updatedProfile.getSchool());
            }
            if (updatedProfile.getSex() != null) {
                existingProfile.setSex(updatedProfile.getSex());
            }
            if (updatedProfile.getDescription() != null) {
                existingProfile.setDescription(updatedProfile.getDescription());
            }
            if (updatedProfile.getAvatar() != null) {
                existingProfile.setAvatar(updatedProfile.getAvatar());
            }
            if (updatedProfile.getBackgroundUrl() != null) {
                existingProfile.setBackgroundUrl(updatedProfile.getBackgroundUrl());
            }
            
            existingUser.setProfile(existingProfile);
        }

        return userRepository.save(existingUser);
    }

    public List<User> searchUsers(String keyword, String phone, String email, LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return userRepository.findByUsernameOrRealNameContaining(keyword);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            return userRepository.findByPhoneContaining(phone);
        }
        if (email != null && !email.trim().isEmpty()) {
            return userRepository.findByEmailContaining(email);
        }
        if (date != null) {
            return userRepository.findByCreatedAt(date);
        }
        if (startDate != null && endDate != null) {
            return userRepository.findByCreatedAtBetween(startDate, endDate);
        }
        return getAllUsers();
    }
}