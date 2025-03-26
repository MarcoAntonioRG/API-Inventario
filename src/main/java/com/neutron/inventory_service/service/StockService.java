package com.neutron.inventory_service.service;

import com.neutron.inventory_service.model.Product;

public interface StockService {
    void reduceStock(Long productId, int quantity);
    void increaseStock(Long productId, int quantity);
    boolean checkStockAvailability(Long productId, int quantity);
    void notifyLowStock(Product product);
}
