package com.spring.project.library.controller;

import com.spring.project.library.dto.LoanDetailsDto;
import com.spring.project.library.dto.LoanRequestDto;
import com.spring.project.library.dto.StatusResponse;
import com.spring.project.library.exception.LibraryOperationException;
import com.spring.project.library.exception.ResourceNotFoundException;
import com.spring.project.library.model.Loan;
import com.spring.project.library.model.User;
import com.spring.project.library.service.LoanService;
import com.spring.project.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        // Bạn cần phương thức tìm kiếm User theo username trong UserService/Repository
        // Giả sử bạn đã thêm phương thức này trong UserService
        return userService.findByUsername(username).getId();

        // Tạm thời, do bạn không cung cấp UserService.findUserByUsername, 
        // và bạn đang dùng Basic Auth, ta sẽ giả định userId là Long và bỏ qua 
        // việc lấy ID thực sự. 
        // TRONG THỰC TẾ, BẠN PHẢI TÌM USER ID TỪ USERNAME ĐỂ LẤY LOAN.

        // Vì mục đích demo, ta sẽ yêu cầu Frontend truyền ID cho Loan/Return.
        // HOẶC, bạn cần thay đổi Authentication để lưu User ID.
        // Tốt nhất là thêm: 
        // User user = userRepository.findByUsername(username).get();
        // return user.getId();
        // throw new UnsupportedOperationException("Cần implement logic lấy User ID từ username.");
    }

    // 🎯 Giả định: Frontend gửi cả userId và bookId trong request

    // POST: Tạo Loan mới (Cho mượn)
    @PostMapping("/borrow")
    // Yêu cầu quyền MEMBER hoặc ADMIN
    public ResponseEntity<?> borrowBook(Authentication authentication, @Valid @RequestBody LoanRequestDto requestDto) {
        try {
            Long userId = getCurrentUserId(authentication);

            Loan loan = loanService.createNewLoan(userId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new StatusResponse("SUCCESS", "Mượn sách thành công, Loan ID: " + loan.getId()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StatusResponse("ERROR", e.getMessage()));
        } catch (LibraryOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse("ERROR", e.getMessage()));
        }
    }

    // PUT: Trả sách
    @PutMapping("/return/{loanId}")
    // Yêu cầu quyền MEMBER hoặc ADMIN
    public ResponseEntity<?> returnBook(@PathVariable Long loanId) {
        try {
            Loan loan = loanService.returnLoan(loanId);
            return ResponseEntity.ok(new StatusResponse("SUCCESS", "Trả sách thành công, Book: " + loan.getBook().getTitle()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StatusResponse("ERROR", e.getMessage()));
        } catch (LibraryOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StatusResponse("ERROR", e.getMessage()));
        }
    }

    // GET: Xem lịch sử mượn (chỉ cho người dùng hiện tại)
    @GetMapping("/my-history")
    public ResponseEntity<List<LoanDetailsDto>> getActiveLoansForUser(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);

        List<LoanDetailsDto> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }
}