package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;

import za.ac.cput.logisticmanagementsystem.repository.ICompanyRepository;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.repository.ShippingContractRepository;
import za.ac.cput.logisticmanagementsystem.repository.StaffProfileRepository;

import za.ac.cput.logisticmanagementsystem.service.impl.ShipmentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

    @Mock
    private IShipmentRepository repository;

    @Mock
    private ICompanyRepository companyRepository;

    @Mock
    private ShippingContractRepository contractRepository;

    @Mock
    private StaffProfileRepository staffRepository;

    @InjectMocks
    private ShipmentService service;

    private Shipment createTestShipment() {

        StaffProfile staff = mock(StaffProfile.class);
        lenient().when(staff.getId()).thenReturn(1L);

        return new Shipment.Builder()
                .shipmentId("SHIP123")
                .weight(450.0)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(12))
                .cargoType("General Cargo")
                .createdByStaff(staff)
                .build();
    }

    @Test
    void testCreateShipment() {

        Shipment request = createTestShipment();

        when(staffRepository.findById(1L))
                .thenReturn(Optional.of(request.getCreatedByStaff()));

        when(repository.save(any(Shipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Shipment result = service.create(request);

        assertNotNull(result);
        assertNotNull(result.getShipmentId());
        assertEquals(450.0, result.getWeight());
        assertEquals("Cape Town", result.getOrigin());
        assertEquals("Johannesburg", result.getDestination());
        assertEquals(LocalDate.now(), result.getDispatchDate());
        assertEquals("General Cargo", result.getCargoType());

        verify(repository, times(1)).save(any(Shipment.class));
    }

    @Test
    void testCreateShipmentNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(null)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void testCreateShipmentInvalidStaff() {

        Shipment request = createTestShipment();

        when(staffRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(request)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void testReadShipmentByShipmentId() {

        Shipment shipment = createTestShipment();

        when(repository.findById("SHIP123"))
                .thenReturn(Optional.of(shipment));

        Shipment result = service.read("SHIP123");

        assertNotNull(result);
        assertEquals("SHIP123", result.getShipmentId());
        assertEquals(450.0, result.getWeight());
        assertEquals("Cape Town", result.getOrigin());
        assertEquals("Johannesburg", result.getDestination());
    }

    @Test
    void testReadShipmentNotFound() {

        when(repository.findById("SHIP999"))
                .thenReturn(Optional.empty());

        Shipment result = service.read("SHIP999");

        assertNull(result);
    }

    @Test
    void testReadAllShipments() {

        Shipment shipment1 = createTestShipment();

        Shipment shipment2 = new Shipment.Builder()
                .shipmentId("SHIP124")
                .weight(50.0)
                .origin("Durban")
                .destination("Pretoria")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(12))
                .cargoType("Fragile")
                .createdByStaff(shipment1.getCreatedByStaff())
                .build();

        when(repository.findAll())
                .thenReturn(List.of(shipment1, shipment2));

        List<Shipment> results = service.readAll();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("SHIP123", results.get(0).getShipmentId());
        assertEquals("SHIP124", results.get(1).getShipmentId());
    }

    @Test
    void testUpdateShipment() {

        Shipment existing = createTestShipment();

        when(repository.findById("SHIP123"))
                .thenReturn(Optional.of(existing));

        when(repository.save(any(Shipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Shipment request = new Shipment.Builder()
                .shipmentId("SHIP123")
                .weight(600.0)
                .origin("Durban")
                .destination("Cape Town")
                .dispatchDate(LocalDate.now())
                .cargoType("Fragile")
                .build();

        Shipment result = service.update(request);

        assertNotNull(result);
        assertEquals("SHIP123", result.getShipmentId());
        assertEquals(600.0, result.getWeight());
        assertEquals("Durban", result.getOrigin());
        assertEquals("Cape Town", result.getDestination());
        assertEquals("Fragile", result.getCargoType());

        verify(repository, times(1)).save(any(Shipment.class));
    }

    @Test
    void testUpdateShipmentNotFound() {

        Shipment shipment = createTestShipment();

        when(repository.findById("SHIP123"))
                .thenReturn(Optional.empty());

        Shipment result = service.update(shipment);

        assertNull(result);

        verify(repository, never()).save(any());
    }

    @Test
    void testUpdateShipmentNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.update(null)
        );
    }

    @Test
    void testDeleteShipment() {

        String shipmentId = "SHIP123";

        service.delete(shipmentId);

        verify(repository, times(1)).deleteById(shipmentId);
    }

    @Test
    void testDeleteShipmentNullId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.delete(null)
        );

        verify(repository, never()).deleteById(any());
    }
}
