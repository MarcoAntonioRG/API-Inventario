package com.neutron.inventory_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neutron.inventory_service.dto.ProductCreateDTO;
import com.neutron.inventory_service.dto.ProductDTO;
import com.neutron.inventory_service.dto.ProductUpdateDTO;
import com.neutron.inventory_service.error.DuplicateSkuException;
import com.neutron.inventory_service.error.ProductNotFoundException;
import com.neutron.inventory_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<ProductDTO> productDTOs = productService.getAllProductDTOs();
            return ResponseEntity.ok(productDTOs);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.ok("No hay productos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener productos");
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Optional<ProductDTO> productDTO = productService.getProductDTOById(id);
            return ResponseEntity.ok(productDTO);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.ok("Producto no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el producto");
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsById(@RequestParam List<Long> ids) {
        Optional<Map<String, Object>> response = productService.getProductDTOsById(ids);

        if (response.isEmpty()) {
            // Si no se encontraron productos, devolvemos un 404 Not Found
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "No se encontraron productos con los IDs proporcionados");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Si se encontraron productos, devolvemos la respuesta con estado OK
        return new ResponseEntity<>(response.get(), HttpStatus.OK);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<?> getProductBySku(@PathVariable String sku) {
        try {
            Optional<ProductDTO> productDTO = productService.getProductDTOBySku(sku);
            return ResponseEntity.ok(productDTO);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.ok("Producto no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el producto");
        }
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ProductDTO>> getProductByCategoriesSort(
            @RequestParam List<String> categories,
            @RequestParam(required = false, defaultValue = "price-asc") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(productService.getProductDTOsByCategoriesIgnoreCaseSorted(
                categories, sortBy, page, size), HttpStatus.OK);
    }

    @GetMapping("by-brands")
    public ResponseEntity<Page<ProductDTO>> getProductsByBrandSort(
            @RequestParam List<String> brands,
            @RequestParam(required = false, defaultValue = "price-asc") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(productService.getProductDTOsByBrandsIgnoreCaseSorted(
                brands, sortBy, page, size), HttpStatus.OK);
    }

    @GetMapping("/sorted/asc")
    public ResponseEntity<Page<ProductDTO>> getProductsSortedByPriceAsc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return new ResponseEntity<>(productService.getProductDTOsSortedByPriceAsc(page, size), HttpStatus.OK);
    }

    @GetMapping("/sorted/desc")
    public ResponseEntity<Page<ProductDTO>> getProductsSortedByPriceDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return new ResponseEntity<>(productService.getProductDTOsSortedByPriceDesc(page, size), HttpStatus.OK);
    }

    @GetMapping("/by-price-greater-than-equal")
    public ResponseEntity<Page<ProductDTO>> getProductsByPriceGreaterThan(
            @RequestParam int price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "price-asc") String sortBy) {
        return new ResponseEntity<>(productService.getProductDTOsByPriceGreaterThanEqual(
                price, page, size, sortBy), HttpStatus.OK);
    }

    @GetMapping("/by-price-less-than-equal")
    public ResponseEntity<Page<ProductDTO>> getProductsByPriceLessThan(
            @RequestParam int price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "price-asc") String sortBy) {
        return new ResponseEntity<>(productService.getProductDTOsByPriceLessThanEqual(
                price, page, size, sortBy), HttpStatus.OK);
    }

    @GetMapping("/price-range")
    public ResponseEntity<Page<ProductDTO>> getProductByPriceBetween(
            @RequestParam int low,
            @RequestParam int high,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "price-asc") String sortBy) {
        return ResponseEntity.ok(productService.getProductDTOsByPriceBetween(low, high, page, size, sortBy));
    }

    /*@PostMapping
    public ResponseEntity<?> createProductDTO(@Valid @RequestBody ProductCreateDTO productDTO) {
        try {
            ProductDTO createdProduct = productService.createProductDTO(productDTO);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (DuplicateSkuException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Código de estado 409: Conflicto
        }
    }*/

    @PostMapping
    public ResponseEntity<?> createProductWithImage(
            @RequestPart("product") @Valid String productJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            // Convertir JSON a objeto DTO
            ObjectMapper objectMapper = new ObjectMapper();
            ProductCreateDTO productDTO = objectMapper.readValue(productJson, ProductCreateDTO.class);

            ProductDTO createdProduct = productService.createProductDTO(productDTO, imageFile);

            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (DuplicateSkuException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Código 409: Conflicto
        } catch (IOException e) {
            return new ResponseEntity<>("Error al guardar la imagen", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/bulk")
    public ResponseEntity<List<ProductDTO>> createProductDTOs(@Valid @RequestBody List<ProductCreateDTO> productDTOs) {
        return new ResponseEntity<>(productService.createProductDTOs(productDTOs), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO productDTO) {
        try {
            ProductDTO updatedProduct = productService.updateProductDTO(id, productDTO);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el producto");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable Long id) {

        try {
            productService.deleteProductById(id);
            return ResponseEntity.ok("Producto eliminado con éxito" );
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el producto");
        }
    }

    @DeleteMapping("delete/by-ids")
    public ResponseEntity<?> deleteProductsById(@RequestParam(required = false) List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ResponseEntity<>("Debe proporcionar al menos un ID", HttpStatus.BAD_REQUEST);
        }

        try {
            List<Long> notFoundIds = productService.deleteProductsById(ids);

            if (notFoundIds.isEmpty()) {
                return new ResponseEntity<>("Todos los productos fueron eliminados con éxito", HttpStatus.OK);
            } else if (notFoundIds.size() < ids.size()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Algunos productos fueron eliminados, " +
                        "pero no se encontraron los siguientes IDs");
                response.put("notFoundIds", notFoundIds);
                return new ResponseEntity<>(response, HttpStatus.PARTIAL_CONTENT);
            } else {
                return new ResponseEntity<>("No se encontraron productos con los IDs proporcionados",
                        HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar productos: "
                    + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAllProducts() {
        try {
            // Llamar al servicio para eliminar todos los productos y obtener los IDs
            List<Long> deletedProductIds = productService.deleteAllProducts();

            if (deletedProductIds.isEmpty()) {
                return new ResponseEntity<>("No hay productos para eliminar", HttpStatus.OK);
            }

            // Devolver los IDs de los productos eliminados
            return new ResponseEntity<>("Productos eliminados: " + deletedProductIds, HttpStatus.OK);
        } catch (Exception e) {
            // Manejar otros posibles errores
            return new ResponseEntity<>("Error al eliminar productos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
