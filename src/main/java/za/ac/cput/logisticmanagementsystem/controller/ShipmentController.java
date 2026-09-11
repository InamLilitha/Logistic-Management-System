package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.web.bind.annotation.*;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.service.impl.ShipmentService;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "http://localhost:3000")
public class ShipmentController {


    private ShipmentService service;

    public ShipmentController(ShipmentService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return service.create(shipment);
    }

    @GetMapping("/{id}")
    public Shipment read(@PathVariable String id){
        return service.read(id);
    }

    @GetMapping
    public List<Shipment> readAll() {
        return service.readAll();
    }

    @PutMapping
    public Shipment updateShipment(@RequestBody Shipment shipment) {
        return service.update(shipment);
    }

    @DeleteMapping("/{id}")
    public void deleteShipment(@PathVariable String id) {
        service.delete(id);
    }
}
