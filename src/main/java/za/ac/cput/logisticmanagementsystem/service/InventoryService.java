/*
 * 28/07/2026
 * InventoryService.java
 * Service implementation for Inventory business logic
 */

package za.ac.cput.logisticmanagementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.repository.ICompanyRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;

import java.util.List;

@Service
@Transactional
public class InventoryService implements IInventoryService {

    private final IInventoryRepository repository;
    private final ICompanyRepository companyRepository;

    @Autowired
    public InventoryService(IInventoryRepository repository, ICompanyRepository companyRepository) {
        this.repository = repository;
        this.companyRepository = companyRepository;
    }

    @Override
    public Inventory create(Inventory inventory) {
        if (!isValid(inventory, false)) {
            return null;
        }
        return repository.save(inventory);
    }

    @Override
    public Inventory read(String inventoryId) {
        return repository.findById(inventoryId).orElse(null);
    }

    @Override
    public Inventory update(Inventory inventory) {
        if (inventory == null || inventory.getInventoryId() == null
                || !repository.existsById(inventory.getInventoryId())
                || !isValid(inventory, true)) {
            return null;
        }
        return repository.save(inventory);
    }

    @Override
    public boolean delete(String inventoryId) {
        if (!repository.existsById(inventoryId)) {
            return false;
        }
        repository.deleteById(inventoryId);
        return true;
    }

    @Override
    public List<Inventory> getAll() {
        return repository.findAll();
    }

    @Override
    public Inventory updateQuantity(String inventoryId, int newQuantity) {
        Inventory inventory = read(inventoryId);
        if (inventory != null && newQuantity >= 0) {
            Inventory updated = new Inventory.Builder()
                    .copy(inventory)
                    .setQuantityAvailable(newQuantity)
                    .build();
            return repository.save(updated);
        }
        return null;
    }

    private boolean isValid(Inventory inventory, boolean allowZeroQuantity) {
        return inventory != null
                && inventory.getItemName() != null && !inventory.getItemName().isBlank()
                && inventory.getSku() != null && !inventory.getSku().isBlank()
                && inventory.getQuantityAvailable() >= (allowZeroQuantity ? 0 : 1)
                && Double.isFinite(inventory.getUnitWeight()) && inventory.getUnitWeight() > 0
                && inventory.getCompanyId() != null
                && companyRepository.existsById(inventory.getCompanyId());
    }
}
