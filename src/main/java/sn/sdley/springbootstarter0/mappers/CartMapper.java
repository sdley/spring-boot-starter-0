package sn.sdley.springbootstarter0.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.sdley.springbootstarter0.dtos.CartDto;
import sn.sdley.springbootstarter0.dtos.CartItemDto;
import sn.sdley.springbootstarter0.entities.Cart;
import sn.sdley.springbootstarter0.entities.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
