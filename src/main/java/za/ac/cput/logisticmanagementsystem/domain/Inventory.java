/*
 * 28/07/2026
 * Inventory.java
 * Inventory domain model class
 */

package za.ac.cput.logisticmanagementsystem.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.FetchType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @Column(nullable = false, updatable = false)
    private String inventoryId;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private int quantityAvailable;

    @Column(nullable = false)
    private double unitWeight;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "company_id", insertable = false, updatable = false)
    private CompanyDomain company;

    protected Inventory(){}

    public Inventory(Builder builder){
        this.inventoryId = builder.inventoryId;
        this.itemName = builder.itemName;
        this.sku = builder.sku;
        this.quantityAvailable = builder.quantityAvailable;
        this.unitWeight = builder.unitWeight;
        this.companyId = builder.companyId;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public double getUnitWeight() {
        return unitWeight;
    }

    public String getCompanyId() {
        return companyId;
    }

    public static class Builder{

        private String inventoryId;
        private String itemName;
        private String sku;
        private int quantityAvailable;
        private double unitWeight;
        private String companyId;

        public Builder setInventoryId(String inventoryId){
            this.inventoryId = inventoryId;
            return this;
        }

        public Builder setItemName(String itemName){
            this.itemName = itemName;
            return this;
        }

        public Builder setSku(String sku){
            this.sku = sku;
            return this;
        }

        public Builder setQuantityAvailable(int quantityAvailable){
            this.quantityAvailable = quantityAvailable;
            return this;
        }

        public Builder setUnitWeight(double unitWeight){
            this.unitWeight = unitWeight;
            return this;
        }

        public Builder setCompanyId(String companyId){
            this.companyId = companyId;
            return this;
        }

        public Builder copy(Inventory inventory) {
            this.inventoryId = inventory.inventoryId;
            this.itemName = inventory.itemName;
            this.sku = inventory.sku;
            this.quantityAvailable = inventory.quantityAvailable;
            this.unitWeight = inventory.unitWeight;
            this.companyId = inventory.companyId;
            return this;
        }

        public Inventory build(){
            return new Inventory(this);
        }
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryId='" + inventoryId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", sku='" + sku + '\'' +
                ", quantityAvailable=" + quantityAvailable +
                ", unitWeight=" + unitWeight +
                ", companyId='" + companyId + '\'' +
                '}';
    }
}
