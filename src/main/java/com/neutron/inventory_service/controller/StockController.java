package com.neutron.inventory_service.controller;

import com.neutron.inventory_service.error.InsufficientStockException;
import com.neutron.inventory_service.error.ProductNotFoundException;
import com.neutron.inventory_service.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping("/reduce/{productId}")
    public ResponseEntity<?> reduceStock(@PathVariable Long productId, @RequestBody int quantity) {

        try {
            stockService.reduceStock(productId, quantity);
            return ResponseEntity.ok("Stock reducido exitosamente");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.ok("Producto no encontrado");
        } catch (InsufficientStockException e) {
            return ResponseEntity.ok("Stock insuficiente");
        }

    }

    @PostMapping("/increase/{productId}")
    public ResponseEntity<?> increaseStock(@PathVariable Long productId, @RequestBody int quantity) {

        try {
            stockService.increaseStock(productId, quantity);
            return ResponseEntity.ok("Stock aumentado exitosamente");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.ok("Producto no encontrado");
        }
    }

}
