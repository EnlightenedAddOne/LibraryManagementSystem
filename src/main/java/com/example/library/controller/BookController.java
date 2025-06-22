package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.service.BookService;
import com.example.library.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

  
    // 增加图书
    @PostMapping
    public Response<Book> addBook(@RequestBody Book book) {
        try {
            Book addedBook = bookService.addBook(book);
            return Response.success(addedBook);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // 根据 ID 删除图书
    @DeleteMapping("/{id}")
    public Response<Void> deleteBook(@PathVariable Integer id) {
        try {
            bookService.deleteBook(id);
            return Response.success(null);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // 更新图书信息
    @PutMapping("/{id}")
    public Response<Book> updateBook(@PathVariable Integer id, @RequestBody Book updatedBook) {
        try {
            Book book = bookService.updateBook(id, updatedBook);
            return Response.success(book);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // 根据 ID 查询图书
    @GetMapping("/{id}")
    public Response<Book> getBookById(@PathVariable Integer id) {
        try {
            Book book = bookService.getBookById(id);
            return Response.success(book);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    // 查询图书列表
    @GetMapping
    public Response<Map<String, Object>> getAllBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "category", required = false) String category) {
        try {
            List<Book> books;

            // 根据条件筛选图书
            if (title != null && !title.isEmpty()) {
                books = bookService.getBooksByTitleContaining(title); // 模糊查询书名
            } else if (author != null && !author.isEmpty()) {
                books = bookService.getBooksByAuthorContaining(author); // 模糊查询作者
            } else if (category != null && !category.isEmpty()) {
                books = bookService.getBooksByCategory(category); // 根据类别查询
            } else {
                // 默认返回所有图书
                books = bookService.getAllBooks();
            }

            // 构造返回数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("records", books); // 图书列表
            responseData.put("total", books.size()); // 总数
            return Response.success(responseData);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}