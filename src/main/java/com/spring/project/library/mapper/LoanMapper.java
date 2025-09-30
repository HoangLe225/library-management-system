package com.spring.project.library.mapper;

import com.spring.project.library.dto.LoanDto.LoanCreationDto;
import com.spring.project.library.dto.LoanDto.LoanResponseDto;
import com.spring.project.library.model.Book;
import com.spring.project.library.model.Loan;
import com.spring.project.library.repository.BookRepository;
import com.spring.project.library.repository.UserRepository; // Giả định có UserRepository
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class LoanMapper {

    @Autowired
    protected BookRepository bookRepository;

    // -----------------------------------------------------------------
    // A. toEntity (Tạo Entity từ Request DTO)
    // -----------------------------------------------------------------

    // 1. Ánh xạ cơ bản: Bỏ qua ID, Book, User, và các trường tính toán/hệ thống
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "loanDate", ignore = true) // Sẽ được Service gán LocalDateTime.now()
    @Mapping(target = "dueDate", ignore = true) // Sẽ được Service tính toán
    @Mapping(target = "returnDate", ignore = true)
    @Mapping(target = "status", ignore = true) // Sẽ được Service gán "LOANED"
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract Loan toEntity(LoanCreationDto dto);

    /**
     * Chạy sau toEntity để tìm nạp và gán Book Entity.
     * Note: durationDays không được ánh xạ vì nó chỉ là thông tin tạm thời cho Service.
     */
    @AfterMapping
    protected void linkBookToLoan(LoanCreationDto dto, @MappingTarget Loan loan) {
        if (dto.getBookId() != null) {
            Book book = bookRepository.findById(dto.getBookId())
                    .orElseThrow(() -> new EntityNotFoundException("Book with ID " + dto.getBookId() + " not found."));

            loan.setBook(book);
        }
    }


    // -----------------------------------------------------------------
    // B. toResponseDto (Chuyển Entity sang Response DTO)
    // -----------------------------------------------------------------

    // 1. Ánh xạ các trường đơn giản (id, loanDate, dueDate, returnDate, status)
    // 2. Ánh xạ lồng nhau cho thông tin Book
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "user.username", target = "username")
    public abstract LoanResponseDto toResponseDto(Loan entity);

    // PHƯƠNG THỨC MỚI: Ánh xạ từ List Entity sang List DTO
    public abstract List<LoanResponseDto> toResponseDtoList(List<Loan> entities);

}
