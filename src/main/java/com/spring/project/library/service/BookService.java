package com.spring.project.library.service;

import com.spring.project.library.model.Book;
import com.spring.project.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Lấy tất cả sách hiện có trong thư viện.
     */
    @Transactional(readOnly = true)
    public List<Book> findAllBooks() {
        // Trả về tất cả sách (không cần DTO nếu Book Entity không có quan hệ Lazy)
        return bookRepository.findAll();
    }

    // Bạn sẽ thêm các phương thức khác ở đây:
    // - findBookById(Long id)
    // - saveBook(Book book)
    // - updateCopies(Long bookId, int change)
}