package za.ac.cput.logisticmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;

public interface ShippingContractRepository extends JpaRepository < ShippingContract, String> {

}
