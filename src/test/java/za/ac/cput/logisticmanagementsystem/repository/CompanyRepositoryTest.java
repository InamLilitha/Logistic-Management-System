/*
 * 28/07/2026
 *CompanyRepositoryTest.java
 *CompanyRepositoryTest model class in repository folder inside the test folder
 *Maghdie Petersen 230600204
 *  */
package za.ac.cput.logisticmanagementsystem.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import static org.junit.jupiter.api.Assertions.*;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import za.ac.cput.logisticmanagementsystem.factory.CompanyFactory;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompanyRepositoryTest {
    private static final CompanyRepository repository = CompanyRepository.getRepository();

    private static final  CompanyDomain companyDomain = CompanyFactory.createCompanyDomain("Devland",
            "TAX-25789", "0213337890","Devland@gmail.co.za");

    @Test
    @Order(1)
    void a_create(){
        CompanyDomain created = repository.create(companyDomain);
        assertNotNull(created);
        assertEquals(companyDomain.getCompanyId(), created.getCompanyId());
        System.out.println("Created " + created);
    }

    @Test
    @Order(2)
    void b_read(){
        CompanyDomain read = repository.read(companyDomain.getCompanyId());
        assertNotNull(read);
        assertEquals("Devland", read.getCompanyName());
        System.out.println("Read " + read);
    }

    @Test
    @Order(3)
    void c_update(){
        CompanyDomain updatedCompany = new CompanyDomain.Builder()
                .copy(companyDomain).setCompanyName("Devland Hyper").build();

        CompanyDomain updated = repository.update(updatedCompany);
        assertNotNull(updated);
        assertEquals("Devland Hyper", updated.getCompanyName());
        System.out.println("Updated " + updated);
    }

    @Test
    @Order(4)
    void d_getAll(){
       assertTrue(repository.getAll().size() > 0);
       System.out.println("All companies: " + repository.getAll());
    }

    @Test
    @Order(5)
    void e_delete(){
        boolean deleted = repository.delete(companyDomain.getCompanyId());
        assertTrue(deleted);
        assertNull(repository.read(companyDomain.getCompanyId()));
        System.out.println("Deleted Successfully" + companyDomain.getCompanyId());
    }
}