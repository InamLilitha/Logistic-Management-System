/*
 * 28/07/2026
 * InventoryService.java
 * Service implementation for Inventory business logic
 */

package za.ac.cput.logisticmanagementsystem.service;

import org.springframework.stereotype.Service;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.repository.InventoryRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;

import java.util.List;

@Service
public class InventoryService implements IInventoryService {

    private final IInventoryRepository repository;

    public InventoryService() {
        this.repository = InventoryRepository.getInstance();
    }

    @Override
    public Inventory create(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        return repository.create(inventory);
    }

    @Override
    public Inventory read(String inventoryId) {
        return repository.read(inventoryId);
    }

    @Override
    public Inventory update(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        return repository.update(inventory);
    }

    @Override
    public boolean delete(String inventoryId) {
        return repository.delete(inventoryId);
    }

    @Override
    public List<Inventory> getAll() {
        return repository.getAll();
    }

    @Override
    public Inventory updateQuantity(String inventoryId, int newQuantity) {
        Inventory inventory = repository.read(inventoryId);
        if (inventory != null && newQuantity >= 0) {
            Inventory updated = new Inventory.Builder()
                    .copy(inventory)
                    .setQuantityAvailable(newQuantity)
                    .build();
            return repository.update(updated);
        }
        return null;
    }
}
