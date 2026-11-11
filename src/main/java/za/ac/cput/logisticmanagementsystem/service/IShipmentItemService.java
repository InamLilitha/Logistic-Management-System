package za.ac.cput.logisticmanagementsystem.service;
/*
 * IShipmentItemService.java
 * Author: Inam Ngqokomashe
 * 222660155
 * Date: 05 November 2026
 */
import za.ac.cput.logisticmanagementsystem.domain.ShipmentItem;

import java.util.List;

public interface IShipmentItemService {

    ShipmentItem create(ShipmentItem shipmentItem);

    ShipmentItem read(String id);

    List<ShipmentItem> readAll();

    List<ShipmentItem> readByShipmentId(String shipmentId);

    ShipmentItem update(ShipmentItem shipmentItem);

    void delete(String id);
}