package za.ac.cput.logisticmanagementsystem.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.service.impl.ShipmentService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ShipmentControllerTest {

    @Mock
    private ShipmentService service;

    @InjectMocks
    private ShipmentController controller;

   @Test
    void testCreateShipment() {
       Shipment shipment = new Shipment.Builder()
               .shipmentId("SHIP123")
               .weight(450.0)
               .origin("Cape Town")
               .destination("Johannesburg")
               .dispatchDate(LocalDate.now())
               .estimatedDeliveryDate(LocalDate.now().plusDays(3))
               .build();

       when(service.create(shipment)).thenReturn(shipment);
       Shipment result = controller.createShipment(shipment);

       assertNotNull(result);
       assertEquals("SHIP123", result.getShipmentId());
       assertEquals("Cape Town", result.getOrigin());
       assertEquals("Johannesburg", result.getDestination());
       assertEquals(LocalDate.now(), result.getDispatchDate());
       assertEquals(LocalDate.now().plusDays(3), result.getEstimatedDeliveryDate());

       verify(service).create(shipment);
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

       when(service.read("SHIP123")).thenReturn(shipment);
       Shipment result = controller.read("SHIP123");

       assertNotNull(result);
       assertEquals("SHIP123", result.getShipmentId());
       assertEquals("Cape Town", result.getOrigin());
       assertEquals("Johannesburg", result.getDestination());
       assertEquals(LocalDate.now(), result.getDispatchDate());
       assertEquals(LocalDate.now().plusDays(3), result.getEstimatedDeliveryDate());

       verify(service).read("SHIP123");
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

       when(service.readAll()).thenReturn(List.of(shipment1, shipment2));
       List<Shipment> results = controller.readAll();

       assertNotNull(results);
       verify(service).readAll();
   }

   @Test
    void testUpdateShipment(){

       Shipment shipment = new Shipment.Builder()
               .shipmentId("SHIP123")
               .weight(450.0)
               .origin("Cape Town")
               .destination("Johannesburg")
               .dispatchDate(LocalDate.now())
               .estimatedDeliveryDate(LocalDate.now().plusDays(3))
               .build();

       when(service.update(shipment)).thenReturn(shipment);
       Shipment result = controller.updateShipment(shipment);
       assertNotNull(result);
       assertEquals("SHIP123", result.getShipmentId());
   }

   @Test
    void testDeleteShipment() {
       String ShipmentId = "SHIP123";
       controller.deleteShipment(ShipmentId);
       verify(service).delete(ShipmentId);
   }

}


