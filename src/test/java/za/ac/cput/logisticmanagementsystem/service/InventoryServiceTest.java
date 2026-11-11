/*
 * 28/07/2026
 * InventoryServiceTest.java
 * Test class for InventoryService
 */

package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.logisticmanagementsystem.repository.ICompanyRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.factory.InventoryFactory;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private IInventoryRepository repository;

    @Mock
    private ICompanyRepository companyRepository;

    @InjectMocks
    private InventoryService service;

    private Inventory inventory() {
        return InventoryFactory.createInventory("Keyboard", "SKU-98765", 50, 0.5, "company-003");
    }

    @Test
    void createPersistsInventory() {
        Inventory inventory = inventory();
        when(companyRepository.existsById(inventory.getCompanyId())).thenReturn(true);
        when(repository.save(inventory)).thenReturn(inventory);

        Inventory created = service.create(inventory);

        assertNotNull(created);
        assertEquals(inventory.getInventoryId(), created.getInventoryId());
        verify(repository).save(inventory);
    }

    @Test
    void readFindsInventoryById() {
        Inventory inventory = inventory();
        when(repository.findById(inventory.getInventoryId())).thenReturn(Optional.of(inventory));

        Inventory read = service.read(inventory.getInventoryId());

        assertNotNull(read);
        assertEquals("Keyboard", read.getItemName());
    }

    @Test
    void updatePersistsInventory() {
        Inventory inventory = inventory();
        Inventory updatedInventory = new Inventory.Builder()
                .copy(inventory).setQuantityAvailable(60).build();
        when(repository.existsById(inventory.getInventoryId())).thenReturn(true);
        when(companyRepository.existsById(inventory.getCompanyId())).thenReturn(true);
        when(repository.save(updatedInventory)).thenReturn(updatedInventory);

        Inventory updated = service.update(updatedInventory);

        assertNotNull(updated);
        assertEquals(60, updated.getQuantityAvailable());
        verify(repository).save(updatedInventory);
    }

    @Test
    void updateQuantityPersistsUpdatedInventory() {
        Inventory inventory = inventory();
        when(repository.findById(inventory.getInventoryId())).thenReturn(Optional.of(inventory));
        when(repository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventory updated = service.updateQuantity(inventory.getInventoryId(), 75);

        assertNotNull(updated);
        assertEquals(75, updated.getQuantityAvailable());
        verify(repository).save(updated);
    }

    @Test
    void getAllReturnsPersistedInventory() {
        Inventory inventory = inventory();
        when(repository.findAll()).thenReturn(List.of(inventory));

        assertEquals(List.of(inventory), service.getAll());
    }

    @Test
    void deleteRemovesExistingInventory() {
        Inventory inventory = inventory();
        when(repository.existsById(inventory.getInventoryId())).thenReturn(true);

        boolean deleted = service.delete(inventory.getInventoryId());

        assertTrue(deleted);
        verify(repository).deleteById(inventory.getInventoryId());
    }

    @Test
    void testCreateWithNullInventory() {
        Inventory created = service.create(null);

        assertNull(created);
        verifyNoInteractions(repository);
        verifyNoInteractions(companyRepository);
    }

    @Test
    void testUpdateQuantityWithNegative() {
        Inventory inventory = inventory();
        when(repository.findById(inventory.getInventoryId())).thenReturn(Optional.of(inventory));

        Inventory updated = service.updateQuantity(inventory.getInventoryId(), -5);

        assertNull(updated);
        verify(repository, never()).save(any(Inventory.class));
    }

    @Test
    void createRejectsInventoryWithUnknownCompany() {
        Inventory inventory = inventory();
        when(companyRepository.existsById(inventory.getCompanyId())).thenReturn(false);

        assertNull(service.create(inventory));
        verify(repository, never()).save(any(Inventory.class));
    }

    @Test
    void updateRejectsInventoryThatDoesNotExist() {
        Inventory inventory = inventory();
        when(repository.existsById(inventory.getInventoryId())).thenReturn(false);

        assertNull(service.update(inventory));
        verifyNoInteractions(companyRepository);
    }
}
