package com.spring.project.library.mapper;

import com.spring.project.library.dto.BookDto.BookCreationDto;
import com.spring.project.library.dto.BookDto.BookResponseDto;
import com.spring.project.library.dto.BookDto.BookUpdateDto;
import com.spring.project.library.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract Book toEntity(BookCreationDto dto);
    public abstract void updateEntityFromDto(BookUpdateDto dto, @MappingTarget Book entity);
    public abstract BookResponseDto toResponseDto(Book entity);
    public abstract List<BookResponseDto> toResponseDtoList(List<Book> entities);
}
