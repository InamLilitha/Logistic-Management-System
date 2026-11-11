/*last Time worked on Wednesday 11/11/2026 04:10
 *CompanyFactory.java
 *CompanyFactory model class in factory folder
 *Maghdie Petersen 230600204
 *  */
package za.ac.cput.logisticmanagementsystem.factory;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import za.ac.cput.logisticmanagementsystem.util.Helper;

public class CompanyFactory {

    public static CompanyDomain createCompanyDomain(String companyName, String companyType, String taxId, String phoneNumber, String emailAddress){
        if (Helper.isNullOrEmpty(companyName) || Helper.isNullOrEmpty(taxId) || Helper.isNullOrEmpty(companyType)){
            return null;
        }

        if (!Helper.isEmailValid(emailAddress) || !Helper.isValidPhoneNumber(phoneNumber)){
            return null;
        }

        String companyId = Helper.generateId();

        return new CompanyDomain.Builder()
                .setCompanyId(companyId)
                .setCompanyName(companyName)
                .setCompanyType(companyType)
                .setTaxId(taxId)
                .setPhoneNumber(phoneNumber)
                .setEmailAddress(emailAddress)
                .setIsActive(true)
                .build();
    }
}
