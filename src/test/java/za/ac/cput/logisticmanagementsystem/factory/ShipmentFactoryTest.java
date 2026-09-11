package za.ac.cput.logisticmanagementsystem.factory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;

import java.time.LocalDate;

/**
 * ShipmentFactoryTest.java
 * Author: Inam Ngqokomashe
 * 222660155
 */

public class ShipmentFactoryTest {

    @Test
    public void testCreateValidShipment() {
        Shipment shipment = ShipmentFactory.createShipment(
                450.0,
                "Cape Town", "Johannesburg",
                LocalDate.now(),
                LocalDate.now().plusDays(3)
        );

        assertNotNull(shipment);
        assertNotNull(shipment.getShipmentId());
        assertTrue(shipment.getShipmentId().startsWith("SHIP-"));
        assertEquals(450.0, shipment.getWeight());
        assertEquals("Cape Town", shipment.getOrigin());
        assertEquals("Johannesburg", shipment.getDestination());
        assertEquals(LocalDate.now(), shipment.getDispatchDate());
        assertEquals(LocalDate.now().plusDays(3), shipment.getEstimatedDeliveryDate());

    }


   @Test
   public void testCreateShipmentWithNegativeWeight(){
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ShipmentFactory.createShipment(
                -45.0,
                "Cape Town",
                "Johannesburg",
                LocalDate.now(),
                LocalDate.now().plusDays(3))
        );

        assertEquals("Weight has to be greater than zero", exception.getMessage());
   }
   @Test
   public void testCreateInvalidOrigin(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        ShipmentFactory.createShipment(
                450.0,
                "",
                "Johannesburg",
                LocalDate.now(),
                LocalDate.now().plusDays(3))
        );

        assertEquals("Origin has to be specified", exception.getMessage());
   }

   @Test
    public void testCreateShipment_InvalidDestination(){
       IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        ShipmentFactory.createShipment(
                450.0,
                "Cape Town",
                "",
                LocalDate.now(),
                LocalDate.now().plusDays(3))
        );

        assertEquals("Destination has to be specified", exception.getMessage());
   }

   @Test
   public void testCreateShipment_SameOriginAndDestination(){
       IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        ShipmentFactory.createShipment(
                450.0,
                "Cape Town",
                "Cape Town",
                LocalDate.now(),
                LocalDate.now().plusDays(3))
        );

        assertEquals("Origin and destination cannot be the same", exception.getMessage());
   }

   @Test
   public void testCreateShipment_InvalidDispatchDate(){
       IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        ShipmentFactory.createShipment(
                450.0,
                "Cape Town",
                "Johannesburg",
                null,
                LocalDate.now().plusDays(3))
        );

        assertEquals("Dispatch date is required", exception.getMessage());
   }

   @Test
   public void testCreateShipment_InvalidEstimatedDeliveryDate(){
       IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
       ShipmentFactory.createShipment(
                450.0,
                "Cape Town",
                "Johannesburg",
                LocalDate.now(),
                null)

        );

        assertEquals("Estimated delivery date is required", exception.getMessage());
   }

   @Test
    public void testCreateShipment_DeliveryBeforeDispatch(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ShipmentFactory.createShipment(
                        450.0,
                        "Cape Town",
                        "Johannesburg",
                        LocalDate.now(),
                        LocalDate.now().minusDays(1))
        );
        assertEquals("Estimated delivery date cannot be before dispatch date", exception.getMessage());
   }
}//
