package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {
    List<BorrowRecord> findByUserId(Integer userId);

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.borrowDate >= :start AND br.borrowDate <= :end")
    int countByBorrowDateBetween(@Param("start") Date start, @Param("end") Date end);

    // 借阅时间区间统计
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.borrowDate >= :start AND br.borrowDate <= :end")
    int countByBorrowDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 归还时间区间统计
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.returnDate >= :start AND br.returnDate <= :end")
    int countByReturnDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 当前在借（未归还）统计
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.returnDate IS NULL")
    int countByReturnDateIsNull();

    @Query("SELECT COUNT(br) FROM BorrowRecord br")
    int countAllBorrowRecords();
}