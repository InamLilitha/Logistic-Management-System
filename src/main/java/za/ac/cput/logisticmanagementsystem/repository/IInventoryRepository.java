/*
 * 28/07/2026
 * IInventoryRepository.java
 * Repository interface for Inventory CRUD operations
 */

package za.ac.cput.logisticmanagementsystem.repository;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;

import java.util.List;

public interface IInventoryRepository {

    Inventory create(Inventory inventory);

    Inventory read(String inventoryId);

    Inventory update(Inventory inventory);

    boolean delete(String inventoryId);

    List<Inventory> getAll();
}
