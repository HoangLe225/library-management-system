package com.spring.project.library.controller;

import com.spring.project.library.dto.LoanDto.LoanResponseDto;
import com.spring.project.library.dto.LoanDto.LoanCreationDto;
import com.spring.project.library.dto.LoanDto.LoanUpdateDto;
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
@RequestMapping("/api/v1/loans")
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
    @PostMapping
    public ResponseEntity<LoanResponseDto> createLoan(Authentication authentication, @Valid @RequestBody LoanCreationDto requestDto) {
        Long userId = getCurrentUserId(authentication);
        LoanResponseDto loan = loanService.createNewLoan(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    // PUT: Trả sách
    @PutMapping("/{loanId}/return")
    public LoanResponseDto updateLoan(@PathVariable Long loanId, @RequestBody LoanUpdateDto dto) {
        return loanService.updateLoan(loanId, dto);
    }

    // GET: Xem lịch sử mượn (chỉ cho người dùng hiện tại)
    @GetMapping("/history")
    public List<LoanResponseDto> getActiveLoansForUser(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return loanService.getLoansByUserId(userId);
    }
}