package com.spring.project.library.dto;
// LoanRequestDto.java

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class LoanRequestDto {
    @NotNull(message = "Book ID is required")
    private Long bookId;

    // Bạn có thể không cần userId nếu lấy từ Spring Security Context, 
    // nhưng nếu là Admin mượn cho người khác thì cần. Ở đây ta giả định 
    // mượn cho chính người đang đăng nhập.

    @NotNull(message = "Loan duration is required (in days)")
    private Integer durationDays; // Ví dụ: 7 ngày, 14 ngày
}