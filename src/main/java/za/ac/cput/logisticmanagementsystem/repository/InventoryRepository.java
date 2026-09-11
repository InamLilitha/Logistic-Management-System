/*
 * 28/07/2026
 * InventoryRepository.java
 * Repository implementation for Inventory with singleton pattern
 */

package za.ac.cput.logisticmanagementsystem.repository;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryRepository implements IInventoryRepository {

    private static InventoryRepository repo;
    private final List<Inventory> inventoryList = new ArrayList<>();

    private InventoryRepository() {}

    public static synchronized InventoryRepository getInstance() {
        if (repo == null) {
            repo = new InventoryRepository();
        }
        return repo;
    }

    @Override
    public Inventory create(Inventory inventory) {
        if (inventory != null) {
            inventoryList.add(inventory);
            return inventory;
        }
        return null;
    }

    @Override
    public Inventory read(String inventoryId) {
        return inventoryList.stream()
                .filter(i -> i.getInventoryId().equals(inventoryId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Inventory update(Inventory inventory) {
        if (inventory != null) {
            delete(inventory.getInventoryId());
            inventoryList.add(inventory);
            return inventory;
        }
        return null;
    }

    @Override
    public boolean delete(String inventoryId) {
        return inventoryList.removeIf(i ->
                i.getInventoryId().equals(inventoryId));
    }

    @Override
    public List<Inventory> getAll() {
        return new ArrayList<>(inventoryList);
    }
}