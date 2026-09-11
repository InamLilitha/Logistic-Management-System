package za.ac.cput.logisticmanagementsystem.controller;

/**
 * ShipmentTrackingController.java
 *  Class for ShipmentTracking entity
 * Author: Ryan Paledi 230969429
 * Date: 27 July 2026
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.ac.cput.logisticmanagementsystem.domain.ShipmentTracking;
import za.ac.cput.logisticmanagementsystem.service.impl.ShipmentTrackingService;

import java.util.List;

@RestController
@RequestMapping("/api/shipmenttracking")
@CrossOrigin(origins = "http://localhost:3000")

public class ShipmentTrackingController {

    @Autowired
    private ShipmentTrackingService shipmentTrackingService;

    @PostMapping("/create")
    public ResponseEntity<ShipmentTracking> create(@RequestBody ShipmentTracking shipmentTracking) {

        ShipmentTracking created = shipmentTrackingService.create(shipmentTracking);

        if (created == null) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<ShipmentTracking> read(@PathVariable String id) {

        ShipmentTracking shipmentTracking = shipmentTrackingService.read(id);

        if (shipmentTracking == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(shipmentTracking);
    }

    @PutMapping("/update")
    public ResponseEntity<ShipmentTracking> update(@RequestBody ShipmentTracking shipmentTracking) {

        ShipmentTracking updated = shipmentTrackingService.update(shipmentTracking);

        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {

        boolean deleted = shipmentTrackingService.delete(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getall")
    public ResponseEntity<List<ShipmentTracking>> getAll() {

        return ResponseEntity.ok(shipmentTrackingService.getAll());
    }

    @PatchMapping("/update-status/{id}")
    public ResponseEntity<ShipmentTracking> updateStatus(
            @PathVariable String id,
            @RequestParam String shipmentStatus) {

        ShipmentTracking updated =
                shipmentTrackingService.updateShipmentStatus(id, shipmentStatus);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }
}