package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentItem;
import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;

import za.ac.cput.logisticmanagementsystem.repository.ICompanyRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.repository.StaffProfileRepository;

import za.ac.cput.logisticmanagementsystem.service.impl.ShipmentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentServiceTest {

    @Mock
    private IShipmentRepository repository;

    @Mock
    private ICompanyRepository companyRepository;

    @Mock
    private IInventoryRepository inventoryRepository;

    @Mock
    private StaffProfileRepository staffRepository;

    @InjectMocks
    private ShipmentService service;


    private StaffProfile createTestStaff() {
        StaffProfile staff = mock(StaffProfile.class);
        when(staff.getId()).thenReturn(1L);
        return staff;
    }


    private CompanyDomain createTestCompany() {
        CompanyDomain company = mock(CompanyDomain.class);
        when(company.getCompanyId()).thenReturn("COMP-1");
        return company;
    }


    private Inventory createTestInventory(int quantity) {

        return new Inventory.Builder()
                .setInventoryId("INV-1")
                .setItemName("Packing boxes")
                .setSku("BOX-01")
                .setQuantityAvailable(quantity)
                .setUnitWeight(1.5)
                .setCompanyId("COMP-1")
                .build();
    }


    private ShipmentItem createTestShipmentItem(Inventory inventory, int quantity) {

        ShipmentItem item = mock(ShipmentItem.class);

        when(item.getInventory()).thenReturn(inventory);
        when(item.getQuantityShipped()).thenReturn(quantity);

        return item;
    }


    private Shipment createTestShipment() {

        StaffProfile staff = createTestStaff();
        CompanyDomain company = createTestCompany();

        Inventory inventory = createTestInventory(10);
        ShipmentItem item = createTestShipmentItem(inventory, 2);

        return new Shipment.Builder()
                .shipmentId("SHIP123")
                .weight(450.0)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(12))
                .cargoType("General Cargo")
                .company(company)
                .createdByStaff(staff)
                .shipmentItems(List.of(item))
                .build();
    }


    @Test
    void testCreateShipment() {

        Shipment request = createTestShipment();

        CompanyDomain company = request.getCompany();
        StaffProfile staff = request.getCreatedByStaff();

        Inventory inventory = createTestInventory(10);
        ShipmentItem item = createTestShipmentItem(inventory, 2);

        request.setShipmentItems(List.of(item));

        when(companyRepository.findById("COMP-1"))
                .thenReturn(Optional.of(company));

        when(staffRepository.findById(1L))
                .thenReturn(Optional.of(staff));

        when(inventoryRepository.findById("INV-1"))
                .thenReturn(Optional.of(inventory));

        when(repository.save(any(Shipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Shipment result = service.create(request);

        assertNotNull(result);
        assertNotNull(result.getShipmentId());

        assertEquals(450.0, result.getWeight());
        assertEquals("Cape Town", result.getOrigin());
        assertEquals("Johannesburg", result.getDestination());
        assertEquals(LocalDate.now(), result.getDispatchDate());
        assertEquals("General Cargo", result.getCargoType());

        assertNotNull(result.getShipmentItems());
        assertEquals(1, result.getShipmentItems().size());

        verify(repository, times(1))
                .save(any(Shipment.class));
    }


    @Test
    void testCreateShipmentNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(null)
        );

        verify(repository, never())
                .save(any());
    }


    @Test
    void testCreateShipmentInvalidStaff() {

        Shipment request = createTestShipment();

        CompanyDomain company = request.getCompany();

        when(companyRepository.findById("COMP-1"))
                .thenReturn(Optional.of(company));

        when(staffRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(request)
        );

        verify(repository, never())
                .save(any(Shipment.class));
    }


    @Test
    void testCreateShipmentDeductsInventoryForItems() {

        CompanyDomain company = createTestCompany();
        StaffProfile staff = createTestStaff();

        Inventory inventory = createTestInventory(10);

        ShipmentItem item = createTestShipmentItem(inventory, 4);

        Shipment request = new Shipment.Builder()
                .weight(10.0)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(LocalDate.now())
                .cargoType("General Cargo")
                .company(company)
                .createdByStaff(staff)
                .shipmentItems(List.of(item))
                .build();

        when(companyRepository.findById("COMP-1"))
                .thenReturn(Optional.of(company));

        when(staffRepository.findById(1L))
                .thenReturn(Optional.of(staff));


        when(inventoryRepository.findById("INV-1"))
                .thenReturn(Optional.of(inventory));

        when(repository.save(any(Shipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Shipment result = service.create(request);

        assertNotNull(result);
        assertNotNull(result.getShipmentItems());
        assertEquals(1, result.getShipmentItems().size());

        ShipmentItem savedItem = result.getShipmentItems().get(0);

        assertEquals(4, savedItem.getQuantityShipped());
        assertEquals(
                "BOX-01",
                savedItem.getInventory().getSku()
        );

        verify(inventoryRepository)
                .save(argThat(updated ->
                        updated.getQuantityAvailable() == 6
                ));
    }


    @Test
    void testCreateShipmentRejectsQuantityAboveAvailableStock() {

        CompanyDomain company = createTestCompany();
        StaffProfile staff = createTestStaff();

        Inventory inventory = createTestInventory(2);

        ShipmentItem item = createTestShipmentItem(inventory, 3);

        Shipment request = new Shipment.Builder()
                .weight(10.0)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(LocalDate.now())
                .cargoType("General Cargo")
                .company(company)
                .createdByStaff(staff)
                .shipmentItems(List.of(item))
                .build();

        when(companyRepository.findById("COMP-1"))
                .thenReturn(Optional.of(company));

        when(staffRepository.findById(1L))
                .thenReturn(Optional.of(staff));

        when(inventoryRepository.findById("INV-1"))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(request)
        );

        verify(repository, never())
                .save(any(Shipment.class));

        verify(inventoryRepository, never())
                .save(any(Inventory.class));
    }


    @Test
    void testReadShipmentByShipmentId() {

        Shipment shipment = createTestShipment();

        when(repository.findById("SHIP123"))
                .thenReturn(Optional.of(shipment));

        Shipment result = service.read("SHIP123");

        assertNotNull(result);
        assertEquals("SHIP123", result.getShipmentId());
        assertEquals(450.0, result.getWeight());
        assertEquals("Cape Town", result.getOrigin());
        assertEquals("Johannesburg", result.getDestination());
    }


    @Test
    void testReadShipmentNotFound() {

        when(repository.findById("SHIP999"))
                .thenReturn(Optional.empty());

        Shipment result = service.read("SHIP999");

        assertNull(result);
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
                .company(createTestCompany())
                .createdByStaff(shipment1.getCreatedByStaff())
                .shipmentItems(shipment1.getShipmentItems())
                .build();

        when(repository.findAll())
                .thenReturn(List.of(shipment1, shipment2));

        List<Shipment> results = service.readAll();

        assertNotNull(results);
        assertEquals(2, results.size());

        assertEquals("SHIP123", results.get(0).getShipmentId());
        assertEquals("SHIP124", results.get(1).getShipmentId());
    }


    @Test
    void testUpdateShipment() {

        Shipment existing = createTestShipment();

        when(repository.findById("SHIP123"))
                .thenReturn(Optional.of(existing));

        CompanyDomain company = existing.getCompany();

        when(companyRepository.findById("COMP-1"))
                .thenReturn(Optional.of(company));

        Inventory inventory = createTestInventory(10);

        ShipmentItem newItem = createTestShipmentItem(inventory, 2);

        Shipment request = new Shipment.Builder()
                .shipmentId("SHIP123")
                .weight(600.0)
                .origin("Durban")
                .destination("Cape Town")
                .dispatchDate(LocalDate.now())
                .cargoType("Fragile")
                .company(company)
                .shipmentItems(List.of(newItem))
                .build();

        when(inventoryRepository.findById("INV-1"))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.save(any(Shipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Shipment result = service.update(request);

        assertNotNull(result);

        assertEquals("SHIP123", result.getShipmentId());
        assertEquals(600.0, result.getWeight());
        assertEquals("Durban", result.getOrigin());
        assertEquals("Cape Town", result.getDestination());
        assertEquals("Fragile", result.getCargoType());

        verify(repository, times(1))
                .save(any(Shipment.class));
    }


    @Test
    void testUpdateShipmentNotFound() {

        Shipment shipment = createTestShipment();

        when(repository.findById("SHIP123"))
                .thenReturn(Optional.empty());

        Shipment result = service.update(shipment);

        assertNull(result);

        verify(repository, never())
                .save(any(Shipment.class));
    }


    @Test
    void testUpdateShipmentNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.update(null)
        );
    }


    @Test
    void testDeleteShipment() {

        Shipment shipment = createTestShipment();

        when(repository.findById("SHIP123"))
                .thenReturn(Optional.of(shipment));

        Inventory inventory = createTestInventory(10);

        when(inventoryRepository.findById("INV-1"))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.delete("SHIP123");

        verify(repository, times(1))
                .delete(shipment);
    }


    @Test
    void testDeleteShipmentNullId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.delete(null)
        );

        verify(repository, never())
                .delete(any());
    }
}