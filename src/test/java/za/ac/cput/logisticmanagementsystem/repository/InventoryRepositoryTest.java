/*
 * 28/07/2026
 * InventoryRepositoryTest.java
 * Test class for InventoryRepository
 */

package za.ac.cput.logisticmanagementsystem.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.*;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.factory.InventoryFactory;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryRepositoryTest {

    private static final IInventoryRepository repository = InventoryRepository.getInstance();
    private static final Inventory inventory = InventoryFactory.createInventory("Monitor",
            "SKU-54321", 25, 5.0, "company-002");

    @Test
    @Order(1)
    void a_create() {
        Inventory created = repository.create(inventory);
        assertNotNull(created);
        assertEquals(inventory.getInventoryId(), created.getInventoryId());
        System.out.println("Created " + created);
    }

    @Test
    @Order(2)
    void b_read() {
        Inventory read = repository.read(inventory.getInventoryId());
        assertNotNull(read);
        assertEquals("Monitor", read.getItemName());
        System.out.println("Read " + read);
    }

    @Test
    @Order(3)
    void c_update() {
        Inventory updatedInventory = new Inventory.Builder()
                .copy(inventory).setQuantityAvailable(30).build();

        Inventory updated = repository.update(updatedInventory);
        assertNotNull(updated);
        assertEquals(30, updated.getQuantityAvailable());
        System.out.println("Updated " + updated);
    }

    @Test
    @Order(4)
    void d_getAll() {
        assertTrue(repository.getAll().size() > 0);
        System.out.println("All inventories: " + repository.getAll());
    }

    @Test
    @Order(5)
    void e_delete() {
        boolean deleted = repository.delete(inventory.getInventoryId());
        assertTrue(deleted);
        assertNull(repository.read(inventory.getInventoryId()));
        System.out.println("Deleted Successfully: " + inventory.getInventoryId());
    }
}
