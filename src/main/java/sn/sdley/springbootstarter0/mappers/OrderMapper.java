package sn.sdley.springbootstarter0.mappers;

import org.mapstruct.Mapper;
import sn.sdley.springbootstarter0.dtos.OrderDto;
import sn.sdley.springbootstarter0.entities.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
