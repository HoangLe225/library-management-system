package com.spring.project.library.mapper;

import com.spring.project.library.dto.UserDto.UserCreationDto;
import com.spring.project.library.dto.UserDto.UserResponseDto;
import com.spring.project.library.dto.UserDto.UserUpdateDto;
import com.spring.project.library.model.Book;
import com.spring.project.library.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "loans", ignore = true)
    public abstract User toEntity(UserCreationDto dto);
    public abstract void updateEntityFromDto(UserUpdateDto dto, @MappingTarget Book entity);
    public abstract UserResponseDto toResponseDto(User entity);
    public abstract List<UserResponseDto> toResponseDtoList(List<User> entities);
}
