package sn.sdley.springbootstarter0.mappers;

import org.mapstruct.Mapper;
import sn.sdley.springbootstarter0.dtos.CartDto;
import sn.sdley.springbootstarter0.entities.Cart;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);
}
