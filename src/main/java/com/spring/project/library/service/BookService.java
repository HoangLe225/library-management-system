package com.spring.project.library.service;

import com.spring.project.library.exception.ResourceNotFoundException;
import com.spring.project.library.model.Book;
import com.spring.project.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        // Thiết lập availableCopies bằng totalCopies khi tạo mới
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long bookId, Book bookDetails) {
        Book book = findBookById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found for this id: " + bookId));

        // 1. Cập nhật các trường thông tin cơ bản
        book.setIsbn(bookDetails.getIsbn());
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setYear(bookDetails.getYear());

        // 2. Xử lý logic TotalCopies/AvailableCopies
        int oldTotal = book.getTotalCopies();
        int newTotal = bookDetails.getTotalCopies();

        // Số sách đang được mượn
        int loanedCopies = oldTotal - book.getAvailableCopies();

        if (newTotal < loanedCopies) {
            // Xử lý lỗi nghiệp vụ: không thể giảm tổng số lượng xuống thấp hơn số sách đang cho mượn
            throw new IllegalArgumentException("Total copies cannot be less than loaned copies (" + loanedCopies + ").");
        }

        // Cập nhật tổng số lượng
        book.setTotalCopies(newTotal);

        // Tính toán lại số lượng có sẵn
        book.setAvailableCopies(newTotal - loanedCopies);

        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Bạn sẽ thêm các phương thức khác ở đây:
    // - findBookById(Long id)
    // - saveBook(Book book)
    // - updateCopies(Long bookId, int change)
}