package com.spring.project.library.dto.LoanDto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoanUpdateDto {

    // Yêu cầu trạng thái mới phải nằm trong danh sách cho phép (Validation)
    @Pattern(regexp = "RETURNED|OVERDUE", message = "Status must be 'RETURNED' or 'OVERDUE'")
    private String status;

    // Nếu là hành động 'return', bạn có thể cần thêm ngày trả thực tế
    private LocalDateTime returnDate;
}