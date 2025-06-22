package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.repository.BorrowRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookService bookService;

    @Transactional
    public BorrowRecord addBorrowRecord(BorrowRecord borrowRecord) {
        Book book = borrowRecord.getBookId();

        // 检查图书是否可借
        if (book.getStatus() != 1) {
            throw new RuntimeException("图书当前不可借阅");
        }

        // 设置借阅日期和截止日期（默认14天后）
        Date borrowDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(borrowDate);
        calendar.add(Calendar.DATE, 14);
        
        borrowRecord.setBorrowDate(borrowDate);
        borrowRecord.setDueDate(calendar.getTime());

        // 保存借阅记录
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);

        // 更新图书状态为不可借
        book.setStatus(0);
        bookService.updateBook(book.getBookId(), book);

        return savedRecord;
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }

    public BorrowRecord getBorrowRecordById(Integer id) {
        return borrowRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Borrow record not found"));
    }

    public void deleteBorrowRecord(Integer id) {
        borrowRecordRepository.deleteById(id);
    }

    @Transactional
    public BorrowRecord returnBook(Integer id) {
        // 获取借阅记录
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        // 检查是否已归还
        if (record.getReturnDate() != null) {
            throw new RuntimeException("该图书已经归还");
        }

        // 设置归还日期
        record.setReturnDate(new Date());

        // 更新图书状态为可借
        Book book = record.getBookId();
        book.setStatus(1); // 1表示可借阅
        bookService.updateBook(book.getBookId(), book);

        // 保存借阅记录
        return borrowRecordRepository.save(record);
    }

    public List<BorrowRecord> getBorrowRecordsByUserId(Integer userId) {
        return borrowRecordRepository.findByUserId(userId);
    }
}