package com.example.library.controller;


import com.example.library.dto.UserResponseDTO;
import com.example.library.entity.User;
import com.example.library.response.Response;
import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Response<Map<String, Object>> login(@RequestBody User loginRequest) {
        try {
            // 调用服务层的登录方法
            Map<String, Object> tokens = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return Response.success(tokens);
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Response<UserResponseDTO> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            Integer userId = userService.getUserIdFromToken(token);
            User user = userService.getUserById(userId);
            return Response.success(UserResponseDTO.fromUser(user));
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    @PostMapping
    public Response<User> addUser(@RequestBody User user) {
        User addedUser = userService.addUser(user);
        return Response.success(addedUser);
    }

    @GetMapping("/{id}")
    public Response<UserResponseDTO> getUserById(@PathVariable Integer id) {
        try {
            User user = userService.getUserById(id);
            return Response.success(UserResponseDTO.fromUser(user));
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return Response.success(null);
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Response<UserResponseDTO> updateUser(@PathVariable Integer id, @RequestBody User user) {
        try {
            // 设置用户ID
            user.setUserId(id);
            User updatedUser = userService.updateUser(id, user);
            return Response.success(UserResponseDTO.fromUser(updatedUser));
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }
    @GetMapping
    public Response<List<UserResponseDTO>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            boolean hasAnyFilter = keyword != null || phone != null || email != null || date != null || startDate != null || endDate != null;
    
            List<User> users;
            if (hasAnyFilter) {
                // 条件搜索
                LocalDateTime dateTime = date != null ? LocalDateTime.parse(date) : null;
                LocalDateTime startDateTime = startDate != null ? LocalDateTime.parse(startDate) : null;
                LocalDateTime endDateTime = endDate != null ? LocalDateTime.parse(endDate) : null;
    
                users = userService.searchUsers(keyword, phone, email, dateTime, startDateTime, endDateTime);
            } else {
                // 查询全部
                users = userService.getAllUsers();
            }
    
            List<UserResponseDTO> userDTOs = users.stream()
                    .map(UserResponseDTO::fromUser)
                    .collect(Collectors.toList());
    
            return Response.success(userDTOs);
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }
    

}