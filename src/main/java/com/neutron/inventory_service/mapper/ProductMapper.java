package com.neutron.inventory_service.mapper;

import com.neutron.inventory_service.dto.ProductCreateDTO;
import com.neutron.inventory_service.dto.ProductDTO;
import com.neutron.inventory_service.model.Category;
import com.neutron.inventory_service.model.Product;
import com.neutron.inventory_service.model.Tag;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    // Convierte ProductDTO a Product
    public Product toEntity(ProductCreateDTO dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .stock(dto.getStock())
                .brand(dto.getBrand())
                .price(dto.getPrice())
                .weight(dto.getWeight())
                .dimensions(dto.getDimensions())
                .status(dto.getStatus())
                .imagePath(dto.getImagePath())
                .build();

        // Buscar categorías por nombre y agregar al producto
        Set<Category> categories = new HashSet<>();
        if (dto.getCategoryNames() != null) {
            for (String categoryName : dto.getCategoryNames()) {
                // Aquí debes buscar la categoría por nombre (asegurando que sea un nombre existente)
                if (categoryName != null && !categoryName.trim().isEmpty()) {
                    Category category = new Category();
                    category.setName(categoryName);
                    categories.add(category); // Puedes guardar aquí directamente o hacer una consulta a la BD
                }
            }
        }
        product.setCategories(categories);

        // Buscar etiquetas por nombre y agregar al producto
        Set<Tag> tags = new HashSet<>();
        if (dto.getTagNames() != null) {
            for (String tagName : dto.getTagNames()) {
                // Aquí debes buscar la etiqueta por nombre (asegurando que sea un nombre existente)
                if (tagName != null && !tagName.trim().isEmpty()) {
                    Tag tag = new Tag();
                    tag.setName(tagName);
                    tags.add(tag); // Puedes guardar aquí directamente o hacer una consulta a la BD
                }
            }
        }
        product.setTags(tags);

        return product;
    }

    // Convierte Product a ProductDTO
    public ProductDTO toDto(Product product) {
        Set<String> categoryNames = product.getCategories()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toSet());

        Set<String> tagNames = product.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .stock(product.getStock())
                .brand(product.getBrand())
                .price(product.getPrice())
                .weight(product.getWeight())
                .dimensions(product.getDimensions())
                .status(product.getStatus())
                .imagePath(product.getImagePath())
                .categoryNames(categoryNames)
                .tagNames(tagNames)
                .build();
    }
}