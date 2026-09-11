package za.ac.cput.logisticmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;

import java.util.List;

/**
 * IShipmentRepository.java
 * Author: Inam Ngqokomashe
 * 222660155
 */

public interface IShipmentRepository  extends JpaRepository<Shipment, String> {


}
