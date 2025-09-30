package com.spring.project.library.dto.BookDto;

import lombok.Data;

@Data
public class BookUpdateDto {
    private String isbn;
    private String title;
    private String author;
    private Integer year;
    private Integer totalCopies;
}
