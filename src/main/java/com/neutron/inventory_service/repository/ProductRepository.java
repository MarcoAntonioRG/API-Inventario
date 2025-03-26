package com.neutron.inventory_service.repository;

import com.neutron.inventory_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    // Buscar productos por lista de categorías, ignorando mayúsculas y minúsculas
    Page<Product> findByCategories_NameInIgnoreCase(List<String> categoryNames, Pageable pageable);

    // Buscar productos por lista de marcas, ignorando mayúsculas y minúsculas
    Page<Product> findByBrandInIgnoreCase(List<String> brands, Pageable pageable);

    // Devuelve los productos ordenados por precio, con paginación
    Page<Product> findAllByOrderByPriceDesc(Pageable pageable);

    // Devuelve los productos ordenados por precio, con paginación
    Page<Product> findAllByOrderByPriceAsc(Pageable pageable);

    // Buscar productos por un precio mayor que un valor específico
    Page<Product> findByPriceGreaterThanEqual(int price, Pageable pageable);

    // Buscar productos por un precio menor que un valor específico
    Page<Product> findByPriceLessThanEqual(int price, Pageable pageable);

    // Buscar productos en un rango de precio
    Page<Product> findByPriceBetween(int low, int high, Pageable pageable);

    boolean existsBySku(String sku);

}
