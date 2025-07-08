package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository   //标识这是一个Spring的数据访问组件
//继承Spring Data JPA提供的核心接口
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByTitleContaining(String title); // 模糊查询书名
    List<Book> findByAuthorContaining(String author); // 模糊查询作者
    List<Book> findByCategory(String category); // 根据分类查询书籍


    @Query("SELECT COUNT(b) FROM Book b")
    int countAll();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.createTime >= :start AND b.createTime < :end")
    int countByCreateTimeBetween(@Param("start") Date start, @Param("end") Date end);

    @Query("SELECT b.category, COUNT(b) FROM Book b GROUP BY b.category")
    List<Object[]> countGroupByCategory();
}

