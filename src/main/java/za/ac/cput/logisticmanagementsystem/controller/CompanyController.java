/*
 * 28/07/2026
 *CompanyController.java
 *CompanyController model class in controller folder
 *Maghdie Petersen 230600204
 *  */

package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import za.ac.cput.logisticmanagementsystem.service.impl.CompanyService;

import java.util.Set;

@RestController

@RequestMapping("/api/companies")

@CrossOrigin(origins = "http://localhost:3000")

public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<CompanyDomain> create(@RequestBody CompanyDomain companyDomain) {
        CompanyDomain created = companyService.create(companyDomain);
        if (created == null){
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<CompanyDomain> read(@PathVariable String id) {
        CompanyDomain companyDomain = companyService.read(id);
        if (companyDomain == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(companyDomain);
    }


    @PutMapping("/update")
    public ResponseEntity<CompanyDomain> update(@RequestBody CompanyDomain companyDomain) {
        CompanyDomain updated = companyService.update(companyDomain);
        if (updated == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean deleted = companyService.delete(id);
        if (!deleted){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getall")
    public ResponseEntity<Set<CompanyDomain>>getAll() {
        return ResponseEntity.ok(companyService.getAll());
    }

    @PatchMapping("/activate/{id}")
    public ResponseEntity<CompanyDomain> activate(@PathVariable String id) {
        CompanyDomain activated = companyService.deactivateCompanyDomain(id);
        if (activated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(activated);
    }

    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<CompanyDomain> deactivate(@PathVariable String id) {
        CompanyDomain deactivated = companyService.deactivateCompanyDomain(id);
        if (deactivated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(deactivated);
    }
}
