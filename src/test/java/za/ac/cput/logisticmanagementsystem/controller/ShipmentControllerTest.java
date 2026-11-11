package za.ac.cput.logisticmanagementsystem.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.service.IShipmentService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentControllerTest {

   @Mock
   private IShipmentService service;

   @InjectMocks
   private ShipmentController controller;

   private Shipment createTestShipment() {

      return new Shipment.Builder()
              .shipmentId("SHIP123")
              .weight(450.0)
              .origin("Cape Town")
              .destination("Johannesburg")
              .dispatchDate(LocalDate.now())
              .estimatedDeliveryDate(LocalDate.now().plusDays(12))
              .cargoType("General Cargo")
              .build();
   }

   @Test
   void testCreateShipment() {

      Shipment shipment = createTestShipment();

      when(service.create(shipment)).thenReturn(shipment);

      Shipment result = controller.createShipment(shipment);

      assertNotNull(result);
      assertEquals("SHIP123", result.getShipmentId());
      assertEquals("Cape Town", result.getOrigin());
      assertEquals("Johannesburg", result.getDestination());
      assertEquals(450.0, result.getWeight());

      verify(service).create(shipment);
   }

   @Test
   void testReadShipmentByShipmentId() {

      Shipment shipment = createTestShipment();

      when(service.read("SHIP123")).thenReturn(shipment);

      Shipment result = controller.read("SHIP123");

      assertNotNull(result);
      assertEquals("SHIP123", result.getShipmentId());
      assertEquals("Cape Town", result.getOrigin());
      assertEquals("Johannesburg", result.getDestination());

      verify(service).read("SHIP123");
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
              .build();

      when(service.readAll())
              .thenReturn(List.of(shipment1, shipment2));

      List<Shipment> results = controller.readAll();

      assertNotNull(results);
      assertEquals(2, results.size());
      assertEquals("SHIP123", results.get(0).getShipmentId());
      assertEquals("SHIP124", results.get(1).getShipmentId());

      verify(service).readAll();
   }

   @Test
   void testUpdateShipment() {

      Shipment shipment = createTestShipment();

      when(service.update(shipment)).thenReturn(shipment);

      Shipment result = controller.updateShipment(shipment);

      assertNotNull(result);
      assertEquals("SHIP123", result.getShipmentId());

      verify(service).update(shipment);
   }

   @Test
   void testDeleteShipment() {

      String shipmentId = "SHIP123";

      controller.deleteShipment(shipmentId);

      verify(service, times(1)).delete(shipmentId);
   }
}