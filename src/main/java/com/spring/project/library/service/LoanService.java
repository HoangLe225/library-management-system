package com.spring.project.library.service;

import com.spring.project.library.dto.LoanDto.LoanResponseDto;
import com.spring.project.library.dto.LoanDto.LoanCreationDto;
import com.spring.project.library.dto.LoanDto.LoanUpdateDto;
import com.spring.project.library.exception.ResourceNotFoundException;
import com.spring.project.library.mapper.LoanMapper;
import com.spring.project.library.model.Book;
import com.spring.project.library.model.Loan;
import com.spring.project.library.model.User;
import com.spring.project.library.repository.BookRepository;
import com.spring.project.library.repository.LoanRepository;
import com.spring.project.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanMapper = loanMapper;
    }

    // --- 1. Cho mượn sách ---
    @Transactional
    public LoanResponseDto createNewLoan(Long userId, LoanCreationDto requestDto) {
        // 1. Tải User và Book
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại: " + userId));

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book không tồn tại: " + requestDto.getBookId()));

        // 2. Kiểm tra nghiệp vụ
        if (book.getAvailableCopies() <= 0) {
            throw new ResourceNotFoundException("Sách '" + book.getTitle() + "' đã hết bản sao khả dụng.");
        }

        // Bạn có thể thêm các luật khác ở đây: 
        // - Kiểm tra số lượng sách tối đa mà user được mượn.
        // - Kiểm tra user có sách quá hạn hay không.

        // 3. Cập nhật số lượng sách
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // 4. Tạo bản ghi Loan mới
        LocalDateTime loanDate = LocalDateTime.now();
        LocalDateTime dueDate = loanDate.plusDays(requestDto.getDurationDays());

//        Loan newLoan = new Loan();
        Loan newLoan = loanMapper.toEntity(requestDto);
        newLoan.setUser(user);
//        newLoan.setBook(book);
        newLoan.setLoanDate(loanDate);
        newLoan.setDueDate(dueDate);
        newLoan.setStatus("LOANED");

        Loan savedLoan = loanRepository.save(newLoan);

        return loanMapper.toResponseDto(savedLoan);
    }

    // --- 2. Trả sách ---
    @Transactional
    public LoanResponseDto updateLoan(Long loanId, LoanUpdateDto dto) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan ID không tồn tại: " + loanId));

        if (!"LOANED".equals(loan.getStatus())) {
            throw new ResourceNotFoundException("Loan ID " + loanId + " không ở trạng thái 'LOANED'.");
        }

        // 1. Cập nhật trạng thái Loan
        if ("RETURNED".equals(dto.getStatus())) {
            // Chỉ gán ngày trả sách khi trạng thái là RETURNED
            loan.setReturnDate(dto.getReturnDate() != null ? dto.getReturnDate() : LocalDateTime.now());
        }
        loan.setStatus(dto.getStatus());
        Loan returnedLoan = loanRepository.save(loan);

        // 2. Cập nhật số lượng sách
        Book book = returnedLoan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // 3. Xử lý phạt (nếu returnDate > dueDate) - Tùy chọn

        return loanMapper.toResponseDto(returnedLoan);
    }

    // --- 3. Lấy danh sách Loan của người dùng ---
    @Transactional(readOnly = true)
    public List<LoanResponseDto> getLoansByUserId(Long userId) {
        // Cần phương thức tối ưu trong LoanRepository để tải Book và User
        // Ví dụ: List<Loan> findLoansByUserIdWithDetails(Long userId);

//        List<Loan> loans = loanRepository.findByUserIdAndStatus(userId, "LOANED");

        List<Loan> loans = loanRepository.findAllByUserIdWithDetails(userId);

        // Hoặc, nếu bạn không muốn thay đổi Repository, bạn buộc phải
        // truy cập các thuộc tính Lazy trong Transaction:
//        return loans.stream()
//                .map(LoanResponseDto::new)
//                .collect(Collectors.toList());
        return loanMapper.toResponseDtoList(loans);
    }
}