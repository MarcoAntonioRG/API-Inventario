package com.neutron.inventory_service.service;

import com.neutron.inventory_service.dto.ProductCreateDTO;
import com.neutron.inventory_service.dto.ProductDTO;
import com.neutron.inventory_service.dto.ProductUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {
    List<ProductDTO> getAllProductDTOs();
    Optional<ProductDTO> getProductDTOById(Long id);
    Optional<Map<String, Object>> getProductDTOsById(List<Long> ids);
    Optional<ProductDTO> getProductDTOBySku(String sku);
    Page<ProductDTO> getProductDTOsByCategoriesIgnoreCaseSorted(
            List<String> categories, String sortBy, int page, int size);
    Page<ProductDTO> getProductDTOsByBrandsIgnoreCaseSorted(
            List<String> brands, String sortBy, int page, int size);
    Page<ProductDTO> getProductDTOsSortedByPriceAsc(int page, int size);
    Page<ProductDTO> getProductDTOsSortedByPriceDesc(int page, int size);
    Page<ProductDTO> getProductDTOsByPriceGreaterThanEqual(int price, int page, int size, String sortBy);
    Page<ProductDTO> getProductDTOsByPriceLessThanEqual(int price, int page, int size, String sortBy);
    Page<ProductDTO> getProductDTOsByPriceBetween(int low, int high, int page, int size, String sortBy);
    ProductDTO createProductDTO(ProductCreateDTO productDTO, MultipartFile imageFile) throws IOException;
    List<ProductDTO> createProductDTOs(List<ProductCreateDTO> productDTOS);
    ProductDTO updateProductDTO(Long id, ProductUpdateDTO productDTO);
    void deleteProductById(Long id);
    List<Long> deleteProductsById(List<Long> ids);
    List<Long> deleteAllProducts();
}
