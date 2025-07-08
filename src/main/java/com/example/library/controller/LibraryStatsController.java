// src/main/java/com/example/library/controller/LibraryStatsController.java
package com.example.library.controller;

import com.example.library.dto.LibraryStatsDTO;
import com.example.library.response.Response;
import com.example.library.service.LibraryStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryStatsController {

    @Autowired
    private LibraryStatsService libraryStatsService;

    @GetMapping("/api/library/stats")
    public Response<LibraryStatsDTO> getStats() {
        LibraryStatsDTO stats = libraryStatsService.getLibraryStats();
        return Response.success(stats);
    }
}