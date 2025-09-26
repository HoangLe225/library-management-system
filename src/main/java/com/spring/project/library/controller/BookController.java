package com.spring.project.library.controller;

import com.spring.project.library.model.Book;
import com.spring.project.library.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Lấy danh sách tất cả sách trong thư viện.
     * Cần cho trang home.html để hiển thị sách có thể mượn.
     * Yêu cầu quyền MEMBER hoặc ADMIN.
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.findAllBooks();
        return ResponseEntity.ok(books);
    }
}