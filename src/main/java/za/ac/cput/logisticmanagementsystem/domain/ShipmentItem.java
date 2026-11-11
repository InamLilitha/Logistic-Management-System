package za.ac.cput.logisticmanagementsystem.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ShipmentItem {

    @Column(name = "inventory_id", nullable = false)
    private String inventoryId;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    protected ShipmentItem() {
    }

    @JsonCreator
    public ShipmentItem(
            @JsonProperty("inventoryId") String inventoryId,
            @JsonProperty("sku") String sku,
            @JsonProperty("quantity") int quantity
    ) {
        this.inventoryId = inventoryId;
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }
}
