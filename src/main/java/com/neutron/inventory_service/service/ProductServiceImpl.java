package com.neutron.inventory_service.service;

import com.neutron.inventory_service.dto.ProductCreateDTO;
import com.neutron.inventory_service.dto.ProductDTO;
import com.neutron.inventory_service.dto.ProductUpdateDTO;
import com.neutron.inventory_service.error.DuplicateSkuException;
import com.neutron.inventory_service.error.ProductNotFoundException;
import com.neutron.inventory_service.mapper.ProductMapper;
import com.neutron.inventory_service.model.Category;
import com.neutron.inventory_service.model.Product;
import com.neutron.inventory_service.model.Tag;
import com.neutron.inventory_service.repository.CategoryRepository;
import com.neutron.inventory_service.repository.ProductRepository;
import com.neutron.inventory_service.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<ProductDTO> getAllProductDTOs() {
        // Obtener la lista de productos desde el repositorio
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            throw new ProductNotFoundException("No hay productos");
        }

        // Convertir la lista de Product a ProductDTO usando el mapeador
        return products.stream()
                .map(productMapper::toDto)  // Convertir cada Product a ProductDTO
                .collect(Collectors.toList());  // Recoger todo en una lista de ProductDTO
    }

    @Override
    public Optional<ProductDTO> getProductDTOById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        // Convertir Product a ProductDTO usando el mapeador
        ProductDTO productDTO = productMapper.toDto(product); // Convertir Product a ProductDTO

        return Optional.of(productDTO);
    }

    @Override
    public Optional<Map<String, Object>> getProductDTOsById(List<Long> ids) {
        List<Product> foundProducts = productRepository.findAllById(ids);

        if (foundProducts.isEmpty()) {
            return Optional.empty(); // Si no se encontraron productos
        }

        List<Long> foundIds = foundProducts.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        List<Long> notFoundIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());

        // Convertir los productos encontrados a ProductDTO
        List<ProductDTO> foundProductsDTO = foundProducts.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("foundProducts", foundProductsDTO);
        response.put("notFoundIds", notFoundIds);

        return Optional.of(response);
    }

    @Override
    public Optional<ProductDTO> getProductDTOBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        // Convertir Product a ProductDTO usando el mapeador
        ProductDTO productDTO = productMapper.toDto(product); // Convertir Product a ProductDTO

        return Optional.of(productDTO);
    }

    @Override
    public Page<ProductDTO> getProductDTOsByCategoriesIgnoreCaseSorted(
            List<String> categories, String sortBy, int page, int size) {

        // Definir el tipo de ordenamiento
        Sort sort = switch (sortBy) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.unsorted(); // Sin ordenamiento si no se especifica
        };

        // Crear el objeto Pageable con el tamaño, página y ordenamiento
        Pageable pageable = PageRequest.of(page, size, sort);

        // Obtener productos paginados por categorías ignorando mayúsculas y minúsculas
        Page<Product> productPage = productRepository.findByCategories_NameInIgnoreCase(categories, pageable);

        // Convertir la página de productos a una página de ProductDTO
        return productPage.map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> getProductDTOsByBrandsIgnoreCaseSorted(
            List<String> brands, String sortBy, int page, int size) {

        // Definir el tipo de ordenamiento
        Sort sort = switch (sortBy) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.unsorted(); // Sin ordenamiento si no se especifica
        };

        // Crear el objeto Pageable con el tamaño, página y ordenamiento
        Pageable pageable = PageRequest.of(page, size, sort);

        // Obtener productos paginados por categorías ignorando mayúsculas y minúsculas
        Page<Product> productPage = productRepository.findByBrandInIgnoreCase(brands, pageable);

        // Convertir la página de productos a una página de ProductDTO
        return productPage.map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> getProductDTOsSortedByPriceAsc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        Page<Product> productPage = productRepository.findAllByOrderByPriceAsc(pageable);
        return productPage.map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> getProductDTOsSortedByPriceDesc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").descending());
        Page<Product> productPage = productRepository.findAllByOrderByPriceDesc(pageable);
        return productPage.map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> getProductDTOsByPriceGreaterThanEqual(int price, int page, int size, String sortBy) {
        // Definir el tipo de ordenamiento
        Sort sort = switch (sortBy) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.unsorted(); // Sin ordenamiento si no se especifica
        };

        // Crear el objeto Pageable con el tamaño, página y ordenamiento
        Pageable pageable = PageRequest.of(page, size, sort);

        // Obtener productos paginados donde el precio sea mayor o igual al dado
        Page<Product> productPage = productRepository.findByPriceGreaterThanEqual(price, pageable);

        // Convertir la página de productos a una página de ProductDTO
        return productPage.map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> getProductDTOsByPriceLessThanEqual(int price, int page, int size, String sortBy) {
        // Definir el tipo de ordenamiento
        Sort sort = switch (sortBy) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.unsorted(); // Sin ordenamiento si no se especifica
        };

        // Crear el objeto Pageable con el tamaño, página y ordenamiento
        Pageable pageable = PageRequest.of(page, size, sort);

        // Obtener productos paginados donde el precio sea menor o igual al dado
        Page<Product> productPage = productRepository.findByPriceLessThanEqual(price, pageable);

        // Convertir la página de productos a una página de ProductDTO
        return productPage.map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> getProductDTOsByPriceBetween(int low, int high, int page, int size, String sortBy) {
        // Definir el tipo de ordenamiento
        Sort sort = switch (sortBy) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.unsorted(); // Sin ordenamiento si no se especifica
        };

        // Crear el objeto Pageable con el tamaño, página y ordenamiento
        Pageable pageable = PageRequest.of(page, size, sort);

        // Obtener productos paginados donde el precio se encuentre entre dos valores
        Page<Product> productPage = productRepository.findByPriceBetween(low, high, pageable);

        // Convertir la página de productos a una página de ProductDTO
        return productPage.map(productMapper::toDto);
    }

    @Override
    public ProductDTO createProductDTO(ProductCreateDTO productDTO, MultipartFile imageFile) throws IOException {
        // Verificar si ya existe un producto con el mismo SKU
        if (productRepository.existsBySku(productDTO.getSku())) {
            throw new DuplicateSkuException("El SKU ya está en uso: " + productDTO.getSku());
        }

        // Convertir DTO a entidad Product
        Product product = productMapper.toEntity(productDTO);

        // Si se proporciona una imagen, procesarla
        String imagePath = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            // Generar un nombre único para la imagen
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            // String uploadDir = "src/main/resources/static/images/";
            String uploadDir = "/app/images/";
            Path path = Paths.get(uploadDir + fileName);

            // Guardar la imagen en la carpeta del proyecto
            Files.copy(imageFile.getInputStream(), path);

            // Establecer la ruta de la imagen relativa
            imagePath = "/images/" + fileName;
        }

        // Establecer la ruta de la imagen
        product.setImagePath(imagePath);

        // Manejar las categorías
        Set<Category> categories = handleCategories(productDTO.getCategoryNames());
        product.setCategories(categories);

        // Manejar las etiquetas
        Set<Tag> tags = handleTags(productDTO.getTagNames());
        product.setTags(tags);

        // Guardar el producto en la base de datos
        Product savedProduct = productRepository.save(product);

        // Convertir la entidad guardada de nuevo a DTO
        return productMapper.toDto(savedProduct);
    }

    private Set<Category> handleCategories(Set<String> categoryNames) {
        Set<Category> categories = new HashSet<>();
        if (categoryNames != null) {
            for (String categoryName : categoryNames) {
                Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(categoryName);
                if (existingCategory.isPresent()) {
                    categories.add(existingCategory.get());
                } else {
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    categories.add(categoryRepository.save(newCategory));
                }
            }
        }
        return categories;
    }

    private Set<Tag> handleTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(tagName);
                if (existingTag.isPresent()) {
                    tags.add(existingTag.get());
                } else {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    tags.add(tagRepository.save(newTag));
                }
            }
        }
        return tags;
    }

    @Override
    public List<ProductDTO> createProductDTOs(List<ProductCreateDTO> productDTOS) {

        List<Product> products = productDTOS.stream().map(productMapper::toEntity)
                .collect(Collectors.toList());

        // Crear una lista para almacenar los productos creados
        List<Product> createdProducts = new ArrayList<>();

        // Procesar cada producto de la lista
        for (Product product : products) {
            // Manejar las categorías
            if (Objects.nonNull(product.getCategories()) && !product.getCategories().isEmpty()) {
                Set<Category> categories = new HashSet<>();

                for (Category category : product.getCategories()) {
                    // Buscar la categoría por nombre
                    Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(category.getName());
                    if (existingCategory.isPresent()) {
                        // Si la categoría ya existe, agregarla al conjunto
                        categories.add(existingCategory.get());
                    } else {
                        // Si la categoría no existe, crear una nueva y agregarla
                        Category newCategory = new Category();
                        newCategory.setName(category.getName());
                        categories.add(categoryRepository.save(newCategory)); // Guarda la nueva categoría
                    }
                }
                // Asignar las categorías al producto
                product.setCategories(categories);
            }
            // Manejar las etiquetas (mismo proceso que las categorías)
            if (Objects.nonNull(product.getTags()) && !product.getTags().isEmpty()) {
                Set<Tag> tags = new HashSet<>();
                for (Tag tag : product.getTags()) {
                    // Buscar la etiqueta por nombre (ignorar mayúsculas y minúsculas)
                    Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(tag.getName());
                    if (existingTag.isPresent()) {
                        // Si la etiqueta ya existe, agregarla al conjunto
                        tags.add(existingTag.get());
                    } else {
                        // Si la etiqueta no existe, crear una nueva y agregarla
                        Tag newTag = new Tag();
                        newTag.setName(tag.getName());
                        tags.add(tagRepository.save(newTag)); // Guardar la nueva etiqueta
                    }
                }
                // Asignar las etiquetas al producto
                product.setTags(tags);
            }

            // Guardar el producto en la base de datos y añadirlo a la lista de productos creados
            createdProducts.add(productRepository.save(product));
        }

        // Retornar la lista de productos creados
        return createdProducts.stream().map(productMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateProductDTO(Long id, ProductUpdateDTO productDTO) {
        // Obtener el producto de la base de datos
        Product productDB = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        // Actualizar campos si no son nulos o vacíos
        if (Objects.nonNull(productDTO.getName()) && !productDTO.getName().trim().isEmpty()) {
            productDB.setName(productDTO.getName());
        }

        if (Objects.nonNull(productDTO.getDescription()) && !productDTO.getDescription().trim().isEmpty()) {
            productDB.setDescription(productDTO.getDescription());
        }

        if (Objects.nonNull(productDTO.getSku()) && !productDTO.getSku().trim().isEmpty()) {
            productDB.setSku(productDTO.getSku());
        }

        if (Objects.nonNull(productDTO.getBrand()) && !productDTO.getBrand().trim().isEmpty()) {
            productDB.setBrand(productDTO.getBrand());
        }

        // Actualizar stock y precio directamente
        productDB.setStock(productDTO.getStock());
        productDB.setPrice(productDTO.getPrice());

        // Actualizar las categorías si no son nulas o vacías
        if (Objects.nonNull(productDTO.getCategoryNames()) && !productDTO.getCategoryNames().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (String categoryName : productDTO.getCategoryNames()) {
                // Buscar la categoría por nombre
                Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(categoryName);
                if (existingCategory.isPresent()) {
                    // Si la categoría ya existe, agregarla al conjunto
                    categories.add(existingCategory.get());
                } else {
                    // Si la categoría no existe, crear una nueva y agregarla
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    categories.add(categoryRepository.save(newCategory)); // Guarda la nueva categoría
                }
            }
            // Actualizar las categorías del producto
            productDB.setCategories(categories);
        }

        // Actualizar las etiquetas si no son nulas o vacías
        if (Objects.nonNull(productDTO.getTagNames()) && !productDTO.getTagNames().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : productDTO.getTagNames()) {
                // Buscar la etiqueta por nombre (ignorar mayúsculas y minúsculas)
                Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(tagName);
                if (existingTag.isPresent()) {
                    // Si la etiqueta ya existe, agregarla al conjunto
                    tags.add(existingTag.get());
                } else {
                    // Si la etiqueta no existe, crear una nueva y agregarla
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    tags.add(tagRepository.save(newTag)); // Guardar la nueva etiqueta
                }
            }
            // Asignar las etiquetas al producto
            productDB.setTags(tags);
        }

        // Guardar el producto actualizado
        Product updatedProduct = productRepository.save(productDB);

        // Devolver el DTO del producto actualizado
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        productRepository.deleteById(id);
    }

    @Override
    public List<Long> deleteProductsById(List<Long> ids) {
        // Buscar los productos que existen en la base de datos
        List<Product> foundProducts = productRepository.findAllById(ids);

        // Obtener los IDs de los productos encontrados
        List<Long> foundIds = foundProducts.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        // Identificar los IDs que no se encontraron
        List<Long> notFoundIds = ids.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());

        // Eliminar los productos encontrados
        productRepository.deleteAll(foundProducts);

        // Retornar los IDs no encontrados
        return notFoundIds;
    }

    @Override
    public List<Long> deleteAllProducts() {
        // Obtener todos los productos de la base de datos
        List<Product> allProducts = productRepository.findAll();

        // Obtener los IDs de los productos
        List<Long> productIds = allProducts.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        // Eliminar todos los productos
        productRepository.deleteAll();

        // Devolver los IDs de los productos eliminados
        return productIds;
    }


}
