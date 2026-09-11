package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.service.impl.ShipmentService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

    @Mock
    private IShipmentRepository repository;

    @InjectMocks
    private ShipmentService service;


    @Test
    void testCreateShipment() {
        Shipment request = new Shipment.Builder()
                .weight(450.0)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(java.time.LocalDate.now())
                .estimatedDeliveryDate(java.time.LocalDate.now().plusDays(3))
                .build();

        Shipment saved = new Shipment.Builder()
                .shipmentId("SHIP123")
                .weight(450.0)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(3))
                .build();

        when(repository.save(any(Shipment.class))).thenReturn(saved);
        Shipment result = service.create(request);

        assertNotNull(result);
        assertEquals("SHIP123", result.getShipmentId());
        assertEquals(450.0, result.getWeight());
        assertEquals("Cape Town", result.getOrigin());
        assertEquals("Johannesburg", result.getDestination());
        assertEquals(LocalDate.now(), result.getDispatchDate());
        assertEquals(LocalDate.now().plusDays(3), result.getEstimatedDeliveryDate());
    }

    @Test
    void testReadShipmentByShipmentId() {
        Shipment shipment = new Shipment.Builder()
                .shipmentId("SHIP123")
                .weight(450.0)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(3))
                .build();

        when(repository.findById("SHIP123")).thenReturn(java.util.Optional.of(shipment));
        Shipment result = (Shipment) service.read("SHIP123");

        assertNotNull(result);
        assertEquals("SHIP123", result.getShipmentId());
        assertEquals(450.0, result.getWeight());
        assertEquals("Cape Town", result.getOrigin());
        assertEquals("Johannesburg", result.getDestination());
        assertEquals(LocalDate.now(), result.getDispatchDate());
        assertEquals(LocalDate.now().plusDays(3), result.getEstimatedDeliveryDate());
    }

    @Test
    void testReadAllShipments() {
        Shipment shipment1 = new Shipment.Builder()
                .shipmentId("SHIP123")
                .weight(450.0)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(3))
                .build();

        Shipment shipment2 = new Shipment.Builder()
                .shipmentId("SHIP124")
                .weight(50.0)
                .origin("Durban")
                .destination("Pretoria")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(2))
                .build();

        when(repository.findAll()).thenReturn(List.of(shipment1, shipment2));
        List<Shipment> results = service.readAll();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("SHIP123", results.get(0).getShipmentId());
        assertEquals("SHIP124", results.get(1).getShipmentId());
    }

    @Test
    void testUpdateShipmentNotFound() {
        Shipment shipment = new Shipment.Builder()
                .shipmentId("SHIP123")
                .weight(450.0)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(3))
                .build();

        when(repository.existsById("SHIP123")).thenReturn(false);

        Shipment result = service.update(shipment);
        assertNull(result);
    }

    @Test
    void testDeleteShipment() {
        String shipmentId = "SHIP123";
        service.delete(shipmentId);
        verify(repository, times(1)).deleteById(shipmentId);
    }

}
