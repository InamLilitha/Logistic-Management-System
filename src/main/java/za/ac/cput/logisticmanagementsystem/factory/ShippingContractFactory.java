package za.ac.cput.logisticmanagementsystem.factory;

import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;

import java.time.LocalDate;

public class ShippingContractFactory {

    public static ShippingContract createShippingContract(String contractId, String contractNumber, Double pricePerKg, LocalDate contractStartDate, LocalDate contractExpiryDate){
        return new ShippingContract.Builder()
                .setContractId(contractId)
                .setContractNumber(contractNumber)
                .setPricePerKgString(pricePerKg)
                .setContractStartDate(contractStartDate)
                .setContractExpiryDate(contractExpiryDate)
                .build();
    }
}
