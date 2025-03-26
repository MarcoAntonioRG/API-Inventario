package com.neutron.inventory_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    //@NotBlank(message = "El sku es obligatorio")
    private String sku;

    //@Min(value = 0, message = "El stock no puede ser negativo")
    private int stock;

    //@NotBlank(message = "La marca es obligatoria")
    private String brand;

    /*@Min(value = 1, message = "El precio debe ser mayor a 0")
    @NotNull(message = "El precio es obligatorio")*/
    private Integer price;

    //@Min(value = 1, message = "El peso debe ser mayor a 0")
    private double weight;

    private String dimensions; // Ejemplo: "102mm x 53mm"

    private String status; // Ejemplo: "Disponible", "Agotado", "Descontinuado"

    private String imagePath; // Ruta relativa de la imagen

    private double averageRating;

    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    //@NotNull(message = "Las categorías son obligatorias")
    private Set<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    // Campo para fecha de creación
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Campo para fecha de actualización
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
