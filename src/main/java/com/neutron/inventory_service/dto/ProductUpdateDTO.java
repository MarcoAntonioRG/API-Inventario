package com.neutron.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdateDTO {
    private String name;
    private String description;
    private String sku;
    private int stock;
    private String brand;
    private int price;
    private double weight;
    private String dimensions;
    private String status;
    private String imagePath;
    private Set<String> categoryNames;
    private Set<String> tagNames;
}
