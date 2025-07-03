package com.example.library.controller;

import com.example.library.service.AIService;
import com.example.library.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/ask")
    public Response<String> askAI(@RequestBody String prompt) {
        try {
            String aiResult = aiService.callAI(prompt);
            return Response.success(aiResult);
        } catch (Exception e) {
            return Response.error("AI接口调用失败: " + e.getMessage());
        }
    }
}