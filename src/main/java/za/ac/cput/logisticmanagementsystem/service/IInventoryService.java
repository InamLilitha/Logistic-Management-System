/*
 * 28/07/2026
 * IInventoryService.java
 * Service interface for Inventory business logic
 */

package za.ac.cput.logisticmanagementsystem.service;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;

import java.util.List;

public interface IInventoryService {

    Inventory create(Inventory inventory);

    Inventory read(String inventoryId);

    Inventory update(Inventory inventory);

    boolean delete(String inventoryId);

    List<Inventory> getAll();

    Inventory updateQuantity(String inventoryId, int newQuantity);
}
