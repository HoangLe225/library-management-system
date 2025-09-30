package com.spring.project.library.dto.LoanDto;
// LoanDetailsDto.java

import com.spring.project.library.model.Loan;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LoanResponseDto {
    private Long id;
    private String bookTitle;
    private String username;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

//    public LoanResponseDto(Loan loan) {
//        this.id = loan.getId();
//        // Giả sử Book Entity đã được tải (Eager hoặc Join Fetch)
//        this.bookTitle = loan.getBook().getTitle();
//        // Giả sử User Entity cũng đã được tải
//        this.username = loan.getUser().getUsername();
//        this.loanDate = loan.getLoanDate();
//        this.dueDate = loan.getDueDate();
//        this.returnDate = loan.getReturnDate();
//        this.status = loan.getStatus();
//    }
}