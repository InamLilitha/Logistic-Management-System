package za.ac.cput.logisticmanagementsystem.repository;
/*
 * IShipmentItemRepository.java
 * Author: Inam Ngqokomashe
 * 222660155
 * Date: 05 November 2026
 */
import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentItem;

import java.util.List;

public interface IShipmentItemRepository extends JpaRepository<ShipmentItem, String> {
    List<ShipmentItem> findByShipment_ShipmentId(String shipmentId);
}
