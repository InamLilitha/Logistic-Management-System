/*
 * 28/07/2026
 * InventoryFactoryTest.java
 * Test class for InventoryFactory
 */

package za.ac.cput.logisticmanagementsystem.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;

public class InventoryFactoryTest {

    @Test
    void testCreateInventorySuccess() {
        Inventory inventory = InventoryFactory.createInventory("Laptop",
                "SKU-12345", 10, 2.5, "company-001");
        assertNotNull(inventory);
        assertNotNull(inventory.getInventoryId());
        assertEquals("Laptop", inventory.getItemName());
        assertEquals("SKU-12345", inventory.getSku());
        assertEquals(10, inventory.getQuantityAvailable());
        assertEquals(2.5, inventory.getUnitWeight());
    }

    @Test
    void testCreateInventoryWithInvalidQuantity() {
        Inventory inventory = InventoryFactory.createInventory("Laptop",
                "SKU-12345", -5, 2.5, "company-001");
        assertNull(inventory);
    }

    @Test
    void testCreateInventoryWithInvalidWeight() {
        Inventory inventory = InventoryFactory.createInventory("Laptop",
                "SKU-12345", 10, -2.5, "company-001");
        assertNull(inventory);
    }

    @Test
    void testCreateInventoryWithNullItemName() {
        Inventory inventory = InventoryFactory.createInventory(null,
                "SKU-12345", 10, 2.5, "company-001");
        assertNull(inventory);
    }

    @Test
    void testCreateInventoryWithNullSku() {
        Inventory inventory = InventoryFactory.createInventory("Laptop",
                null, 10, 2.5, "company-001");
        assertNull(inventory);
    }

    @Test
    void testCreateInventoryWithNullCompanyId() {
        Inventory inventory = InventoryFactory.createInventory("Laptop",
                "SKU-12345", 10, 2.5, null);
        assertNull(inventory);
    }
}
