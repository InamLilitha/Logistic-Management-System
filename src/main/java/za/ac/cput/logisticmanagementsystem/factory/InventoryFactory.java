/*
 * 28/07/2026
 * InventoryFactory.java
 * Factory for creating Inventory objects with validation
 */

package za.ac.cput.logisticmanagementsystem.factory;

import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.util.Helper;

import java.util.UUID;

public class InventoryFactory {

    public static Inventory createInventory(String itemName,
                                            String sku,
                                            int quantity,
                                            double unitWeight,
                                            String companyId){

        if (!Helper.isNullOrEmpty(itemName) &&
            !Helper.isNullOrEmpty(sku) &&
            quantity > 0 &&
            unitWeight > 0 &&
            !Helper.isNullOrEmpty(companyId)) {

            return new Inventory.Builder()
                    .setInventoryId(UUID.randomUUID().toString())
                    .setItemName(itemName)
                    .setSku(sku)
                    .setQuantityAvailable(quantity)
                    .setUnitWeight(unitWeight)
                    .setCompanyId(companyId)
                    .build();
        }
        return null;
    }
}