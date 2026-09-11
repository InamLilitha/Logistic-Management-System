/*
 * 28/07/2026
 * InventoryServiceTest.java
 * Test class for InventoryService
 */

package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.*;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.factory.InventoryFactory;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryServiceTest {

    private static final IInventoryService service = new InventoryService();
    private static final Inventory inventory = InventoryFactory.createInventory("Keyboard",
            "SKU-98765", 50, 0.5, "company-003");

    @Test
    @Order(1)
    void a_create() {
        Inventory created = service.create(inventory);
        assertNotNull(created);
        assertEquals(inventory.getInventoryId(), created.getInventoryId());
        System.out.println("Created " + created);
    }

    @Test
    @Order(2)
    void b_read() {
        Inventory read = service.read(inventory.getInventoryId());
        assertNotNull(read);
        assertEquals("Keyboard", read.getItemName());
        System.out.println("Read " + read);
    }

    @Test
    @Order(3)
    void c_update() {
        Inventory updatedInventory = new Inventory.Builder()
                .copy(inventory).setQuantityAvailable(60).build();

        Inventory updated = service.update(updatedInventory);
        assertNotNull(updated);
        assertEquals(60, updated.getQuantityAvailable());
        System.out.println("Updated " + updated);
    }

    @Test
    @Order(4)
    void d_updateQuantity() {
        Inventory updated = service.updateQuantity(inventory.getInventoryId(), 75);
        assertNotNull(updated);
        assertEquals(75, updated.getQuantityAvailable());
        System.out.println("Updated Quantity " + updated);
    }

    @Test
    @Order(5)
    void e_getAll() {
        assertTrue(service.getAll().size() > 0);
        System.out.println("All inventories: " + service.getAll());
    }

    @Test
    @Order(6)
    void f_delete() {
        boolean deleted = service.delete(inventory.getInventoryId());
        assertTrue(deleted);
        assertNull(service.read(inventory.getInventoryId()));
        System.out.println("Deleted Successfully: " + inventory.getInventoryId());
    }

    @Test
    void testCreateWithNullInventory() {
        Inventory created = service.create(null);
        assertNull(created);
    }

    @Test
    void testUpdateQuantityWithNegative() {
        Inventory inventory = InventoryFactory.createInventory("Mouse",
                "SKU-11111", 20, 0.2, "company-004");
        service.create(inventory);
        Inventory updated = service.updateQuantity(inventory.getInventoryId(), -5);
        assertNull(updated);
    }
}
