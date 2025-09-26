package com.spring.project.library.dto;
// LoanDetailsDto.java

import com.spring.project.library.model.Loan;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LoanDetailsDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId; // Cần thiết nếu muốn hiển thị User
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;

    public LoanDetailsDto(Loan loan) {
        this.id = loan.getId();
        // Giả sử Book Entity đã được tải (Eager hoặc Join Fetch)
        this.bookId = loan.getBook().getId();
        this.bookTitle = loan.getBook().getTitle();
        // Giả sử User Entity cũng đã được tải
        this.userId = loan.getUser().getId();
        this.loanDate = loan.getLoanDate();
        this.dueDate = loan.getDueDate();
        this.returnDate = loan.getReturnDate();
        this.status = loan.getStatus();
    }
}