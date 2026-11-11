package za.ac.cput.logisticmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;

import java.util.Optional;

public interface IInventoryRepository extends JpaRepository<Inventory, String> {
    boolean existsByCompanyId(String companyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select inventory from Inventory inventory where inventory.inventoryId = :inventoryId")
    Optional<Inventory> findByIdForUpdate(@Param("inventoryId") String inventoryId);
}
