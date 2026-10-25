package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;
import za.ac.cput.logisticmanagementsystem.service.ShippingContractService;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-contract")
public class ShippingContractController {

    private final ShippingContractService service;

    public ShippingContractController(ShippingContractService service) {
        this.service = service;
    }

    @PostMapping
    public ShippingContract create(@RequestBody ShippingContract shippingContract) {
        return service.create(shippingContract);
    }

    @GetMapping("/{id}")
    public ShippingContract read(@PathVariable String id) {
        return service.read(id);
    }

    @GetMapping
    public List<ShippingContract> getAll() {
        return service.getAll();
    }

    @PutMapping
    public ShippingContract update(@RequestBody ShippingContract shippingContract) {
        return service.update(shippingContract);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}