package za.ac.cput.logisticmanagementsystem.service;

import za.ac.cput.logisticmanagementsystem.domain.Shipment;

import java.util.List;

public interface IShipmentService {

    Shipment create(Shipment shipment);
    Shipment read(String id);
    List<Shipment> readAll();
    Shipment update(Shipment shipment);
    void delete(String id);
}
