package sn.sdley.springbootstarter0.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.sdley.springbootstarter0.dtos.UserDto;
import sn.sdley.springbootstarter0.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target="createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);
}
