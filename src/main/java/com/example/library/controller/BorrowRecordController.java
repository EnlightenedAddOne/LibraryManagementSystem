package com.example.library.controller;

import com.example.library.entity.BorrowRecord;
import com.example.library.service.BorrowRecordService;
import com.example.library.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @PostMapping
    public Response<BorrowRecord> addBorrowRecord(@RequestBody BorrowRecord borrowRecord) {
        try {
            BorrowRecord record = borrowRecordService.addBorrowRecord(borrowRecord);
            return Response.success(record);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    @GetMapping
    public Response<List<BorrowRecord>> getAllBorrowRecords() {
        try {
            List<BorrowRecord> records = borrowRecordService.getAllBorrowRecords();
            return Response.success(records);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }


    @GetMapping("/user/{userId}")
    public Response<List<BorrowRecord>> getBorrowRecordsByUserId(@PathVariable Integer userId) {
        try {
            List<BorrowRecord> records = borrowRecordService.getBorrowRecordsByUserId(userId);
            return Response.success(records);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/return")
    public Response<BorrowRecord> returnBook(@PathVariable Integer id) {
        try {
            BorrowRecord record = borrowRecordService.returnBook(id);
            return Response.success(record);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}