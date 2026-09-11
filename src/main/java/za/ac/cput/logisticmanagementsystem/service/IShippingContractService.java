package za.ac.cput.logisticmanagementsystem.service;

import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;

import java.util.List;

public interface IShippingContractService {
    ShippingContract create(ShippingContract shippingContract);
    ShippingContract read(String id);
    List<ShippingContract> getAll();
    ShippingContract update(ShippingContract shippingContract);
    void delete(String id);
}