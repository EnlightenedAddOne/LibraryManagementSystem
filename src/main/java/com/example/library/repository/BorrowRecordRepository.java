package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {
    List<BorrowRecord> findByUserId(Integer userId);
}