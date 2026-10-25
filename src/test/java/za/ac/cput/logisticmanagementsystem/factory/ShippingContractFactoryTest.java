package za.ac.cput.logisticmanagementsystem.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;

import java.time.LocalDate;

public class ShippingContractFactoryTest {

    @Test
    void testCreateShippingContractSuccess() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                startDate,
                expiryDate
        );

        assertNotNull(contract);
        assertNotNull(contract.getContractId());
        assertEquals("SC-001", contract.getContractId());
        assertEquals("CN-2026-001", contract.getContractNumber());
        assertEquals(150.00, contract.getPricePerKg());
        assertEquals(startDate, contract.getContractStartDate());
        assertEquals(expiryDate, contract.getContractExpiryDate());
    }

    @Test
    void testCreateShippingContractWithInvalidPricePerKg() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ShippingContractFactory.createShippingContract(
                        "SC-001",
                        "CN-2026-001",
                        -150.00,
                        startDate,
                        expiryDate
                )
        );

        assertEquals("Price per kg must be positive", exception.getMessage());
    }

    @Test
    void testCreateShippingContractWithNullContractNumber() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ShippingContractFactory.createShippingContract(
                        "SC-001",
                        null,
                        150.00,
                        startDate,
                        expiryDate
                )
        );

        assertEquals("Contract number cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateShippingContractWithEmptyContractNumber() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ShippingContractFactory.createShippingContract(
                        "SC-001",
                        "",
                        150.00,
                        startDate,
                        expiryDate
                )
        );

        assertEquals("Contract number cannot be empty", exception.getMessage());
    }

    @Test
    void testCreateShippingContractWithNullContractId() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        assertThrows(IllegalArgumentException.class, () ->
                ShippingContractFactory.createShippingContract(
                        null,
                        "CN-2026-001",
                        150.00,
                        startDate,
                        expiryDate
                )
        );
    }

    @Test
    void testCreateShippingContractWithNullStartDate() {
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ShippingContractFactory.createShippingContract(
                        "SC-001",
                        "CN-2026-001",
                        150.00,
                        null,
                        expiryDate
                )
        );

        assertEquals("Start date and expiry date cannot be null", exception.getMessage());
    }

    @Test
    void testCreateShippingContractWithNullExpiryDate() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ShippingContractFactory.createShippingContract(
                        "SC-001",
                        "CN-2026-001",
                        150.00,
                        startDate,
                        null
                )
        );

        assertEquals("Start date and expiry date cannot be null", exception.getMessage());
    }

    @Test
    void testCreateShippingContractWithStartDateAfterExpiryDate() {
        LocalDate startDate = LocalDate.of(2027, 1, 1);
        LocalDate expiryDate = LocalDate.of(2026, 1, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ShippingContractFactory.createShippingContract(
                        "SC-001",
                        "CN-2026-001",
                        150.00,
                        startDate,
                        expiryDate
                )
        );

        assertEquals("Start date cannot be after expiry date", exception.getMessage());
    }

    @Test
    void testCreateShippingContractGeneratesUniqueIds() {
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

        assertNotNull(contract1);
        assertNotNull(contract2);
        assertNotEquals(contract1.getContractId(), contract2.getContractId());
    }

    @Test
    void testCreateShippingContractIsActive() {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusYears(1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                today,
                futureDate
        );

        assertNotNull(contract);
        assertTrue(contract.isActive());
        assertTrue(contract.isContractValid());
        assertFalse(contract.isExpired());
    }

    @Test
    void testCreateShippingContractIsExpired() {
        LocalDate pastDate = LocalDate.now().minusYears(2);
        LocalDate recentPast = LocalDate.now().minusYears(1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                pastDate,
                recentPast
        );

        assertNotNull(contract);
        assertFalse(contract.isActive());
        assertFalse(contract.isContractValid());
        assertTrue(contract.isExpired());
    }

    @Test
    void testCreateShippingContractCalculateTotalCost() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                startDate,
                expiryDate
        );

        double weight = 10.5;
        double expectedTotal = 150.00 * weight;
        double actualTotal = contract.calculateTotalCost(weight);

        assertEquals(expectedTotal, actualTotal, 0.001);
    }

    @Test
    void testCreateShippingContractIsWithinValidPeriod() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                startDate,
                expiryDate
        );

        LocalDate testDate = LocalDate.of(2026, 6, 1);
        assertTrue(contract.isWithinValidPeriod(testDate));
    }

    @Test
    void testCreateShippingContractIsWithinValidPeriodReturnsFalse() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        ShippingContract contract = ShippingContractFactory.createShippingContract(
                "SC-001",
                "CN-2026-001",
                150.00,
                startDate,
                expiryDate
        );

        LocalDate testDate = LocalDate.of(2027, 2, 1);
        assertFalse(contract.isWithinValidPeriod(testDate));
    }
}

