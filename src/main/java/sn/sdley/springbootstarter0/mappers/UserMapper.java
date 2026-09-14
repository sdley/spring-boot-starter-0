package sn.sdley.springbootstarter0.mappers;

import org.mapstruct.Mapper;
import sn.sdley.springbootstarter0.dtos.UserDto;
import sn.sdley.springbootstarter0.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
