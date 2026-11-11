package za.ac.cput.logisticmanagementsystem.controller;
/*
 * ShipmentItemController.java
 * Author: Inam Ngqokomashe
 * 222660155
 * Date: 06 November 2026
 */
import org.springframework.web.bind.annotation.*;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentItem;
import za.ac.cput.logisticmanagementsystem.service.impl.ShipmentItemService;

import java.util.List;

@RestController
@RequestMapping("/api/shipment-items")
@CrossOrigin(origins = "http://localhost:3000")
public class ShipmentItemController {

    private final ShipmentItemService service;

    public ShipmentItemController(ShipmentItemService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ShipmentItem create(
            @RequestBody ShipmentItem shipmentItem) {

        return service.create(shipmentItem);
    }

    @GetMapping("/{id}")
    public ShipmentItem read(@PathVariable String id) {
        return service.read(id);
    }

    @GetMapping
    public List<ShipmentItem> readAll() {
        return service.readAll();
    }

    @GetMapping("/shipment/{shipmentId}")
    public List<ShipmentItem> readByShipmentId(
            @PathVariable String shipmentId) {

        return service.readByShipmentId(shipmentId);
    }

    @PutMapping
    public ShipmentItem update(
            @RequestBody ShipmentItem shipmentItem) {

        return service.update(shipmentItem);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}