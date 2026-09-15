package sn.sdley.springbootstarter0.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.sdley.springbootstarter0.dtos.RegisterUserRequest;
import sn.sdley.springbootstarter0.dtos.UpdateUserRequest;
import sn.sdley.springbootstarter0.dtos.UserDto;
import sn.sdley.springbootstarter0.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target="createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
    void update(UpdateUserRequest request, @MappingTarget User user);
}
