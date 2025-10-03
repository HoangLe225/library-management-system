package com.spring.project.library.dto.BookDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookUpdateDto {
    @NotBlank(message = "ISBN cannot be blank")
    @Size(min = 10, max = 13, message = "Password must be at least 6 characters long")
    private String isbn;
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "Author cannot be blank")
    private String author;
    @NotNull(message = "Published year cannot be null")
    private Integer year;
    @NotNull(message = "Total Copies cannot be null")
    private Integer totalCopies;
}
