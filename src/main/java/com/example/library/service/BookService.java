package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // 增加图书
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    // 根据 ID 删除图书
    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }

    // 更新图书信息
// 更新图书信息
    public Book updateBook(Integer id, Book updatedBook) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book existingBook = optionalBook.get();

            if (updatedBook.getTitle() != null) {
                existingBook.setTitle(updatedBook.getTitle());
            }
            if (updatedBook.getAuthor() != null) {
                existingBook.setAuthor(updatedBook.getAuthor());
            }
            if (updatedBook.getIsbn() != null) {
                existingBook.setIsbn(updatedBook.getIsbn());
            }
            if (updatedBook.getPublisher() != null) {
                existingBook.setPublisher(updatedBook.getPublisher());
            }
            if (updatedBook.getPublishDate() != null) {
                existingBook.setPublishDate(updatedBook.getPublishDate());
            }
            if (updatedBook.getCategory() != null) {
                existingBook.setCategory(updatedBook.getCategory());
            }
            if (updatedBook.getDescription() != null) {
                existingBook.setDescription(updatedBook.getDescription());
            }
            if (updatedBook.getCover() != null) {
                existingBook.setCover(updatedBook.getCover());
            }
            // 状态字段更新逻辑
            if (updatedBook.getStatus() != null) {
                existingBook.setStatus(updatedBook.getStatus());
            } else {
                existingBook.setStatus(1);
            }

            return bookRepository.save(existingBook);
        } else {
            throw new RuntimeException("Book not found with id: " + id);
        }
    }


    // 根据 ID 查询图书
    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    // 查询所有图书
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // 根据书名模糊查询图书
    public List<Book> getBooksByTitleContaining(String title) {
        return bookRepository.findByTitleContaining(title);
    }
    // 根据作者模糊查询图书
    public List<Book> getBooksByAuthorContaining(String author) {
        return bookRepository.findByAuthorContaining(author);
    }

    // 根据分类查询图书
    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
}