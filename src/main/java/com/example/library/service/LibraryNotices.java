// src/main/java/com/example/library/service/LibraryNoticeService.java
package com.example.library.service;

import com.example.library.entity.LibraryNotice;
import com.example.library.repository.LibraryNoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryNotices {

    @Autowired
    private LibraryNoticeRepository noticeRepository;

    public List<LibraryNotice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public Optional<LibraryNotice> getNoticeById(Integer id) {
        return noticeRepository.findById(id);
    }

    public LibraryNotice createNotice(LibraryNotice notice) {
        notice.setCreatedAt(LocalDateTime.now());
        notice.setUpdatedAt(LocalDateTime.now());
        return noticeRepository.save(notice);
    }

    public LibraryNotice updateNotice(Integer id, LibraryNotice updatedNotice) {
        return noticeRepository.findById(id).map(notice -> {
            notice.setTitle(updatedNotice.getTitle());
            notice.setContent(updatedNotice.getContent());
            notice.setUpdatedAt(LocalDateTime.now());
            return noticeRepository.save(notice);
        }).orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    public void deleteNotice(Integer id) {
        noticeRepository.deleteById(id);
    }
}