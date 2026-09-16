package sn.sdley.springbootstarter0.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.sdley.springbootstarter0.dtos.ProductDto;
import sn.sdley.springbootstarter0.entities.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

    @Mapping(target = "id", ignore = true)
    void  update(ProductDto productDto, @MappingTarget Product product);
}
