package com.spring.project.library.controller;

import com.spring.project.library.dto.LoanDetailsDto;
import com.spring.project.library.dto.LoanRequestDto;
import com.spring.project.library.model.Loan;
import com.spring.project.library.service.LoanService;
import com.spring.project.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    public LoanController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    // Lấy ID người dùng hiện tại từ Spring Security Context
    private Long getCurrentUserId(Authentication authentication) {
        // Lấy username từ Authentication object
        String username = authentication.getName();
        // Tìm kiếm User theo username trong UserService/Repository
        return userService.findByUsername(username).getId();
    }

    // 🎯 Giả định: Frontend gửi cả userId và bookId trong request

    // POST: Tạo Loan mới (Cho mượn)
    @PostMapping("/borrow")
    // Yêu cầu quyền MEMBER hoặc ADMIN
    public ResponseEntity<?> borrowBook(Authentication authentication, @Valid @RequestBody LoanRequestDto requestDto) {
        Long userId = getCurrentUserId(authentication);
        Loan loan = loanService.createNewLoan(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    // PUT: Trả sách
    @PutMapping("/return/{loanId}")
    // Yêu cầu quyền MEMBER hoặc ADMIN
    public ResponseEntity<?> returnBook(@PathVariable Long loanId) {
        Loan loan = loanService.returnLoan(loanId);
        return ResponseEntity.ok(loan);
    }

    // GET: Xem lịch sử mượn (chỉ cho người dùng hiện tại)
    @GetMapping("/my-history")
    public ResponseEntity<List<LoanDetailsDto>> getActiveLoansForUser(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);

        List<LoanDetailsDto> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }
}