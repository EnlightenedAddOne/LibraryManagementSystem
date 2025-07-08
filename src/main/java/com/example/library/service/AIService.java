package com.example.library.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class AIService {

    @Value("${ai.api.url}")
    private String aiApiUrl;

    @Value("${ai.api.key}")
    private String aiApiKey;

    @Value("${ai.api.model}")
    private String aiModel;

    private static final String SYSTEM_PROMPT = "你是一个乐于助人的图书馆AI助手，请用简短的、口语化的中文回答用户问题。";

    public String callAI(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        // 构造请求体，参考官方文档
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiModel); // 官方推荐模型，可根据实际情况更换
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(aiApiUrl, entity, Map.class);

        // 解析返回内容
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map body = response.getBody();
            List choices = (List) body.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map choice = (Map) choices.get(0);
                Map message = (Map) choice.get("message");
                return (String) message.get("content");
            }
        }
        return "AI接口调用失败";
    }
}