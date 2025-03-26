package com.neutron.inventory_service.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private Long id;

    //@NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    private String description; // Podría ser opcional

    //@NotBlank(message = "El sku es obligatorio")
    @Column(nullable = false, unique = true)
    private String sku;

    //@Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    //@NotBlank(message = "La marca es obligatoria")
    private String brand;

    /*@Min(value = 1, message = "El precio debe ser mayor a 0")
    @NotNull(message = "El precio es obligatorio")*/
    private Integer price;

    //@Min(value = 0, message = "El peso debe ser mayor o igual a 0")
    private double weight;

    private String dimensions; // Ejemplo: "102mm x 53mm"

    private String status; // Ejemplo: "Disponible", "Agotado", "Descontinuado"

    private String imagePath; // Ruta relativa de la imagen

    private Set<String> categoryNames; // Para referencia a las categorías

    private Set<String> tagNames; // Para referencia a las etiquetas

}
