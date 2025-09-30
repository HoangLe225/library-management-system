package com.spring.project.library.service;

import com.spring.project.library.dto.BookDto.BookCreationDto;
import com.spring.project.library.dto.BookDto.BookResponseDto;
import com.spring.project.library.dto.BookDto.BookUpdateDto;
import com.spring.project.library.exception.ResourceNotFoundException;
import com.spring.project.library.mapper.BookMapper;
import com.spring.project.library.model.Book;
import com.spring.project.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Lấy tất cả sách hiện có trong thư viện.
     */
    @Transactional(readOnly = true)
    public List<BookResponseDto> findAllBooks() {
        // Trả về tất cả sách (không cần DTO nếu Book Entity không có quan hệ Lazy)
        List<Book> books = bookRepository.findAll();
        return bookMapper.toResponseDtoList(books);
    }

    @Transactional(readOnly = true)
    public BookResponseDto findBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        return bookMapper.toResponseDto(book);
    }

    public BookResponseDto saveBook(BookCreationDto bookDto) {
        Book newBook = bookMapper.toEntity(bookDto);
        // Thiết lập availableCopies bằng totalCopies khi tạo mới
        if (newBook.getAvailableCopies() == null) {
            newBook.setAvailableCopies(newBook.getTotalCopies());
        }
        Book savedBook = bookRepository.save(newBook);
        return bookMapper.toResponseDto(savedBook);
    }

    public BookResponseDto updateBook(Long bookId, BookUpdateDto bookDto) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));

        // 1. Cập nhật các trường thông tin cơ bản
//        book.setIsbn(bookDto.getIsbn());
//        book.setTitle(bookDto.getTitle());
//        book.setAuthor(bookDto.getAuthor());
//        book.setYear(bookDto.getYear());

        // 2. Xử lý logic TotalCopies/AvailableCopies
        int oldTotal = book.getTotalCopies();
        int newTotal = bookDto.getTotalCopies();

        // Số sách đang được mượn
        int loanedCopies = oldTotal - book.getAvailableCopies();

        if (newTotal < loanedCopies) {
            // Xử lý lỗi nghiệp vụ: không thể giảm tổng số lượng xuống thấp hơn số sách đang cho mượn
            throw new IllegalArgumentException("Total copies cannot be less than loaned copies (" + loanedCopies + ").");
        }

        bookMapper.updateEntityFromDto(bookDto, book);

        // Cập nhật tổng số lượng
//        book.setTotalCopies(newTotal);

        // Tính toán lại số lượng có sẵn
        book.setAvailableCopies(newTotal - loanedCopies);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponseDto(savedBook);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
    }
}