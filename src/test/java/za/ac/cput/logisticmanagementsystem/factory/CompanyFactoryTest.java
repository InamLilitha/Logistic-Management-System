/*
 * 28/07/2026
 *CompanyFactoryTest.java
 *CompanyFactoryTest model class in factory folder inside the test folder
 *Maghdie Petersen 230600204
 *  */
package za.ac.cput.logisticmanagementsystem.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;

public class CompanyFactoryTest {

    @Test
    void testCreateCompanySuccess(){
        CompanyDomain companyDomain = CompanyFactory.createCompanyDomain("Checkers",
                "TAX-45671", "0210001234","checkers@gmail.co.za");
        assertNotNull(companyDomain);
        assertNotNull(companyDomain.getCompanyId());
        assertEquals("Checkers", companyDomain.getCompanyName());
        assertTrue(companyDomain.isActive());
    }

    @Test
    void testCreateCompanyWithInvalidEmail(){
        CompanyDomain companyDomain = CompanyFactory.createCompanyDomain("Checkers",
                "TAX-45671", "0210001234","wrong-email-format");
        assertNull(companyDomain);
    }

    @Test
    void testCreateCompanyWithInvalidPhoneNumber(){
        CompanyDomain companyDomain = CompanyFactory.createCompanyDomain("Checkers",
                "TAX-45671", "1234","checkers@gmail.co.za");
        assertNull(companyDomain);
    }

}
