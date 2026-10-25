package za.ac.cput.logisticmanagementsystem.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;
import za.ac.cput.logisticmanagementsystem.factory.ShippingContractFactory;
import za.ac.cput.logisticmanagementsystem.service.ShippingContractService;
import za.ac.cput.logisticmanagementsystem.controller.ShippingContractController;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingContractControllerTest {

    @Mock
    private ShippingContractService service;

    @InjectMocks
    private ShippingContractController controller;

    @Test
    void testCreateShippingContract() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                startDate,
                expiryDate
        );

        when(service.create(contract)).thenReturn(contract);

        ShippingContract result = controller.create(contract);

        assertNotNull(result);
        assertEquals("SC-001", result.getContractId());
        assertEquals("CN-2026-001", result.getContractNumber());
        assertEquals(150.00, result.getPricePerKg());
        verify(service).create(contract);
    }

    @Test
    void testCreateShippingContractWithNull() {
        ShippingContract result = controller.create(null);

        assertNull(result);
        verify(service).create(null);
    }

    @Test
    void testReadShippingContract() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                startDate,
                expiryDate
        );

        when(service.read("SC-001")).thenReturn(contract);

        ShippingContract result = controller.read("SC-001");

        assertNotNull(result);
        assertEquals("SC-001", result.getContractId());
        verify(service).read("SC-001");
    }

    @Test
    void testReadShippingContractNotFound() {
        ShippingContract result = controller.read("NON-EXISTENT");

        assertNull(result);
        verify(service).read("NON-EXISTENT");
    }

    @Test
    void testGetAllShippingContracts() {
        when(service.getAll()).thenReturn(List.of());

        List<ShippingContract> result = controller.getAll();

        assertNotNull(result);
        verify(service).getAll();
    }

    @Test
    void testGetAllShippingContractsWithData() {
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

        when(service.getAll()).thenReturn(contracts);

        List<ShippingContract> result = controller.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(service).getAll();
    }

    @Test
    void testUpdateShippingContract() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                175.00,
                startDate,
                expiryDate
        );

        when(service.update(contract)).thenReturn(contract);

        ShippingContract result = controller.update(contract);

        assertNotNull(result);
        assertEquals("SC-001", result.getContractId());
        assertEquals(175.00, result.getPricePerKg());
        verify(service).update(contract);
    }

    @Test
    void testUpdateShippingContractWithNull() {
        ShippingContract result = controller.update(null);

        assertNull(result);
        verify(service).update(null);
    }

    @Test
    void testDeleteShippingContract() {
        controller.delete("SC-001");

        verify(service).delete("SC-001");
    }

    @Test
    void testDeleteShippingContractWithNonExistentId() {
        controller.delete("NON-EXISTENT");

        verify(service).delete("NON-EXISTENT");
    }
}