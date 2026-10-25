package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;
import za.ac.cput.logisticmanagementsystem.factory.ShippingContractFactory;
import za.ac.cput.logisticmanagementsystem.repository.ShippingContractRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingContractServiceTest {

    @Mock
    private ShippingContractRepository repository;

    @InjectMocks
    private ShippingContractService service;

    @Test
    void testCreateShippingContract() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                startDate,
                expiryDate
        );

        when(repository.save(any(ShippingContract.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ShippingContract result = service.create(contract);

        // Assert
        assertNotNull(result);
        assertEquals("SC-001", result.getContractId());
        assertEquals("CN-2026-001", result.getContractNumber());
        assertEquals(150.00, result.getPricePerKg());
        assertEquals(startDate, result.getContractStartDate());
        assertEquals(expiryDate, result.getContractExpiryDate());
        verify(repository).save(any(ShippingContract.class));
    }

    @Test
    void testCreateShippingContractWithNull() {
        // Act
        ShippingContract result = service.create(null);

        // Assert
        assertNull(result);
        verify(repository).save(null);
    }

    @Test
    void testReadShippingContract() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                startDate,
                expiryDate
        );

        when(repository.findById("SC-001")).thenReturn(Optional.of(contract));

        // Act
        ShippingContract result = service.read("SC-001");

        // Assert
        assertNotNull(result);
        assertEquals("SC-001", result.getContractId());
        assertEquals("CN-2026-001", result.getContractNumber());
        assertEquals(150.00, result.getPricePerKg());
        verify(repository).findById("SC-001");
    }

    @Test
    void testReadShippingContractNotFound() {
        // Arrange
        when(repository.findById("NON-EXISTENT")).thenReturn(Optional.empty());

        // Act
        ShippingContract result = service.read("NON-EXISTENT");

        // Assert
        assertNull(result);
        verify(repository).findById("NON-EXISTENT");
    }

    @Test
    void testGetAllShippingContracts() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract1 = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                startDate,
                expiryDate
        );

        ShippingContract contract2 = ShippingContractFactory.createShippingContract(
                "SC-002",
                "CN-2026-002",
                200.00,
                startDate,
                expiryDate
        );

        List<ShippingContract> contracts = List.of(contract1, contract2);

        when(repository.findAll()).thenReturn(contracts);

        // Act
        List<ShippingContract> result = service.getAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("SC-001", result.get(0).getContractId());
        assertEquals("SC-002", result.get(1).getContractId());
        verify(repository).findAll();
    }

    @Test
    void testUpdateShippingContract() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                175.00, // Updated price
                startDate,
                expiryDate
        );

        when(repository.save(any(ShippingContract.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ShippingContract result = service.update(contract);

        // Assert
        assertNotNull(result);
        assertEquals("SC-001", result.getContractId());
        assertEquals(175.00, result.getPricePerKg());
        verify(repository).save(any(ShippingContract.class));
    }

    @Test
    void testUpdateShippingContractWithNull() {
        // Act
        ShippingContract result = service.update(null);

        // Assert
        assertNull(result);
        verify(repository).save(null);
    }

    @Test
    void testDeleteShippingContract() {
        // Act
        service.delete("SC-001");

        // Assert
        verify(repository).deleteById("SC-001");
    }

    @Test
    void testDeleteShippingContractWithNonExistentId() {
        // Act
        service.delete("NON-EXISTENT");

        // Assert
        verify(repository).deleteById("NON-EXISTENT");
    }

    @Test
    void testCreateShippingContractGeneratesValidContract() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate expiryDate = startDate.plusYears(1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-003",
                "CN-2026-003",
                300.00,
                startDate,
                expiryDate
        );

        when(repository.save(any(ShippingContract.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ShippingContract result = service.create(contract);

        // Assert
        assertNotNull(result);
        assertTrue(result.isActive());
        assertFalse(result.isExpired());
        assertTrue(result.isContractValid());
        verify(repository).save(any(ShippingContract.class));
    }
}