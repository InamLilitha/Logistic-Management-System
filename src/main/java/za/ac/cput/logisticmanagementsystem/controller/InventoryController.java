/*
 * 28/07/2026
 * InventoryController.java
 * REST Controller for Inventory endpoints
 */

package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.factory.InventoryFactory;
import za.ac.cput.logisticmanagementsystem.service.IInventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class InventoryController {

    @Autowired
    private IInventoryService inventoryService;

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
        return new ResponseEntity<>(created, HttpStatus.CREATED);
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
    public ResponseEntity<Inventory> update(@RequestBody Inventory inventory) {
        Inventory updated = inventoryService.update(inventory);
        if (updated == null) {
            return ResponseEntity.badRequest().build();
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
        Inventory updated = inventoryService.updateQuantity(id, quantity);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}
