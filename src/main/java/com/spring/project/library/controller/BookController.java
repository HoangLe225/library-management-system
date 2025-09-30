package com.spring.project.library.controller;

import com.spring.project.library.dto.BookDto.BookCreationDto;
import com.spring.project.library.dto.BookDto.BookResponseDto;
import com.spring.project.library.dto.BookDto.BookUpdateDto;
import com.spring.project.library.model.Book;
import com.spring.project.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public List<BookResponseDto> getAllBooks() {
        return bookService.findAllBooks();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.findBookById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDto> createBook(@RequestBody BookCreationDto bookDto) {
        BookResponseDto newBook = bookService.saveBook(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBook);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponseDto updateBook(@PathVariable Long id, @RequestBody BookUpdateDto bookDto) {
        return bookService.updateBook(id, bookDto);
    }

    // --- 5. DELETE: Xóa sách (deleteBook) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}