// src/main/java/com/example/library/controller/LibraryNoticeController.java
package com.example.library.controller;

import com.example.library.entity.LibraryNotice;
import com.example.library.response.Response;
import com.example.library.service.LibraryNotices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class LibraryNoticeController {

    @Autowired
    private LibraryNotices noticeService;

    @GetMapping
    public Response<List<LibraryNotice>> getAllNotices() {
        List<LibraryNotice> notices = noticeService.getAllNotices();
        return Response.success(notices);
    }

    @GetMapping("/{id}")
    public Response<LibraryNotice> getNoticeById(@PathVariable Integer id) {
        return noticeService.getNoticeById(id)
                .map(Response::success)
                .orElse(Response.error("Notice not found"));
    }

    @PostMapping
    public Response<LibraryNotice> createNotice(@RequestBody LibraryNotice notice) {
        LibraryNotice createdNotice = noticeService.createNotice(notice);
        return Response.success(createdNotice);
    }

    @PutMapping("/{id}")
    public Response<LibraryNotice> updateNotice(@PathVariable Integer id, @RequestBody LibraryNotice notice) {
        LibraryNotice updatedNotice = noticeService.updateNotice(id, notice);
        return Response.success(updatedNotice);
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteNotice(@PathVariable Integer id) {
        noticeService.deleteNotice(id);
        return Response.success(null);
    }
}