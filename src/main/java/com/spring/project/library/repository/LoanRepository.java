package com.spring.project.library.repository;

import com.spring.project.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    // Tìm các sách đang được mượn bởi một người dùng cụ thể
    List<Loan> findByUserIdAndStatus(Long userId, String status);
    // 🎯 Phương thức MỚI: Lấy tất cả lịch sử mượn (status bất kỳ)
    @Query("SELECT l FROM Loan l JOIN FETCH l.book b JOIN FETCH l.user u WHERE l.user.id = :userId ORDER BY l.loanDate DESC")
    List<Loan> findAllByUserIdWithDetails(@Param("userId") Long userId);

    // Thống kê các bản ghi mượn theo book_id và status
    long countByBookIdAndStatus(Long bookId, String status);
}