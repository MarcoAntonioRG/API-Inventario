package com.neutron.inventory_service.service;

import com.neutron.inventory_service.error.InsufficientStockException;
import com.neutron.inventory_service.error.ProductNotFoundException;
import com.neutron.inventory_service.model.Product;
import com.neutron.inventory_service.repository.ProductRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String STOCK_EXCHANGE = "stockExchange";
    private static final String ROUTING_KEY = "stock.low";

    // Método para reducir el stock después de un pedido
    @Override
    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Stock insuficiente");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        // Verificar si el stock es bajo y enviar una notificación si es necesario
        if (product.getStock() < 10) {
            notifyLowStock(product);
        }
    }

    // Método para incrementar el stock
    @Override
    public void increaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    // Método para verificar disponibilidad de stock
    @Override
    public boolean checkStockAvailability(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        return product.getStock() >= quantity;
    }

    // Método para enviar notificación de bajo stock
    @Override
    public void notifyLowStock(Product product) {
        String message = "Producto " + product.getName() + " está bajo en stock. Quedan " + product.getStock() + " unidades.";
        rabbitTemplate.convertAndSend(STOCK_EXCHANGE, ROUTING_KEY, message);
    }
}
