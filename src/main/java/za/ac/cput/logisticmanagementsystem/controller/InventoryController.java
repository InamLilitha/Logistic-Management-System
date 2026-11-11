/*
 * 28/07/2026
 * InventoryController.java
 * REST Controller for Inventory endpoints
 */

package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.factory.InventoryFactory;
import za.ac.cput.logisticmanagementsystem.service.IInventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class InventoryController {

    private final IInventoryService inventoryService;

    public InventoryController(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<Inventory> create(@RequestParam String itemName,
                                           @RequestParam String sku,
                                           @RequestParam int quantity,
                                           @RequestParam double unitWeight,
                                           @RequestParam String companyId) {
        Inventory inventory = InventoryFactory.createInventory(itemName, sku, quantity, unitWeight, companyId);
        if (inventory == null) {
            return ResponseEntity.badRequest().build();
        }
        Inventory created = inventoryService.create(inventory);
        return created == null
                ? ResponseEntity.badRequest().build()
                : ResponseEntity.status(201).body(created);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<Inventory> read(@PathVariable String id) {
        Inventory inventory = inventoryService.read(id);
        if (inventory == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(inventory);
    }

    @PutMapping("/update")
    public ResponseEntity<Inventory> update(@RequestBody InventoryRequest request) {
        if (request == null || request.inventoryId() == null || request.itemName() == null
                || request.sku() == null || request.quantityAvailable() == null
                || request.unitWeight() == null || request.companyId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Inventory inventory = new Inventory.Builder()
                .setInventoryId(request.inventoryId())
                .setItemName(request.itemName())
                .setSku(request.sku())
                .setQuantityAvailable(request.quantityAvailable())
                .setUnitWeight(request.unitWeight())
                .setCompanyId(request.companyId())
                .build();
        Inventory updated = inventoryService.update(inventory);
        if (updated == null) {
            return inventoryService.read(request.inventoryId()) == null
                    ? ResponseEntity.notFound().build()
                    : ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean deleted = inventoryService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Inventory>> getAll() {
        return ResponseEntity.ok(inventoryService.getAll());
    }

    @PatchMapping("/update-quantity/{id}")
    public ResponseEntity<Inventory> updateQuantity(@PathVariable String id,
                                                   @RequestParam int quantity) {
        if (quantity < 0) {
            return ResponseEntity.badRequest().build();
        }
        Inventory updated = inventoryService.updateQuantity(id, quantity);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    public record InventoryRequest(
            String inventoryId,
            String itemName,
            String sku,
            Integer quantityAvailable,
            Double unitWeight,
            String companyId
    ) {
    }
}
