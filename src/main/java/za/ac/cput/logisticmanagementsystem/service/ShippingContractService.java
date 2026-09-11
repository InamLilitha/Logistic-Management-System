package za.ac.cput.logisticmanagementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;
import za.ac.cput.logisticmanagementsystem.repository.ShippingContractRepository;

import java.util.List;

@Service
public class ShippingContractService implements IShippingContractService {

    @Autowired
    private ShippingContractRepository repository;

    @Override
    public ShippingContract create(ShippingContract shippingContract) {
        if (shippingContract == null) return null;
        return repository.save(shippingContract);
    }

    @Override
    public ShippingContract read(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<ShippingContract> getAll() {
        return repository.findAll();
    }

    @Override
    public ShippingContract update(ShippingContract shippingContract) {
        if (shippingContract == null) return null;
        return repository.save(shippingContract);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}