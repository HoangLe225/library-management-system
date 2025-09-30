package com.spring.project.library.dto.BookDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookResponseDto {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private Integer year;
    private Integer totalCopies;
    private Integer availableCopies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
