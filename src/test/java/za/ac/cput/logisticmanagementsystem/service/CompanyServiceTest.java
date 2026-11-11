package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import za.ac.cput.logisticmanagementsystem.repository.ICompanyRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;
import za.ac.cput.logisticmanagementsystem.service.impl.CompanyService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
    @Mock
    private ICompanyRepository repository;

    @Mock
    private IInventoryRepository inventoryRepository;

    @InjectMocks
    private CompanyService service;

    private CompanyDomain company(String id, boolean active) {
        return new CompanyDomain.Builder()
                .setCompanyId(id)
                .setCompanyName("Checkers")
                .setTaxId("TAX-45671")
                .setPhoneNumber("0210001234")
                .setEmailAddress("checkers@gmail.co.za")
                .setIsActive(active)
                .build();
    }

    @Test
    void createPersistsValidatedCompanyWithGeneratedId() {
        CompanyDomain request = company(null, true);
        when(repository.save(any(CompanyDomain.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyDomain created = service.create(request);

        assertNotNull(created);
        assertNotNull(created.getCompanyId());
        assertEquals(request.getCompanyName(), created.getCompanyName());
        assertTrue(created.isActive());
        verify(repository).save(any(CompanyDomain.class));
    }

    @Test
    void readLoadsCompanyFromRepository() {
        CompanyDomain company = company("company-1", true);
        when(repository.findById(company.getCompanyId())).thenReturn(Optional.of(company));

        assertEquals(company, service.read(company.getCompanyId()));
    }

    @Test
    void updateOnlyChangesAnExistingCompany() {
        CompanyDomain company = company("company-1", false);
        when(repository.existsById(company.getCompanyId())).thenReturn(true);
        when(repository.save(any(CompanyDomain.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyDomain updated = service.update(company);

        assertNotNull(updated);
        assertEquals(company.getCompanyId(), updated.getCompanyId());
        assertFalse(updated.isActive());
    }

    @Test
    void deletePreventsRemovingACompanyWithInventory() {
        when(repository.existsById("company-1")).thenReturn(true);
        when(inventoryRepository.existsByCompanyId("company-1")).thenReturn(true);

        assertFalse(service.delete("company-1"));
        verify(repository, never()).deleteById(anyString());
    }

    @Test
    void getAllReturnsPersistedCompanies() {
        List<CompanyDomain> companies = List.of(company("company-1", true));
        when(repository.findAll()).thenReturn(companies);

        assertTrue(service.getAll().containsAll(companies));
    }

    @Test
    void activationIsPersisted() {
        CompanyDomain company = company("company-1", false);
        when(repository.findById(company.getCompanyId())).thenReturn(Optional.of(company));
        when(repository.save(any(CompanyDomain.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyDomain activated = service.activateCompanyDomain(company.getCompanyId());

        assertNotNull(activated);
        assertTrue(activated.isActive());
    }
}
