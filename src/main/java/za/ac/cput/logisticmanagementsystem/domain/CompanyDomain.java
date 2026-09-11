/* 27/07/2026
 *CompanyDomain.java
 *CompanyDomain model class in domain folder
 *Maghdie Petersen 230600204
 *  */

package za.ac.cput.logisticmanagementsystem.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class CompanyDomain implements Serializable{

    @Id
    private String companyId;
    private String companyName;
    private String taxId;
    private String phoneNumber;
    private String emailAddress;
    private boolean isActive;

    private CompanyDomain() {}

    private CompanyDomain(Builder builder) {
        this.companyId=builder.companyId;
        this.companyName=builder.companyName;
        this.taxId=builder.taxId;
        this.phoneNumber=builder.phoneNumber;
        this.emailAddress=builder.emailAddress;
        this.isActive=builder.isActive;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDomain companyDomain = (CompanyDomain) o;
        return Objects.equals(companyId, companyDomain.companyId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(companyId);
    }

    @Override
    public String toString() {
        return "CompanyDomain{" +
                "companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", taxId='" + taxId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    public static class Builder{
        private String companyId;
        private String companyName;
        private String taxId;
        private String phoneNumber;
        private String emailAddress;
        private boolean isActive;

        public Builder setCompanyId(String companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder setTaxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setIsActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder copy(CompanyDomain companyDomain){
            this.companyId=companyDomain.companyId;
            this.companyName=companyDomain.companyName;
            this.taxId=companyDomain.taxId;
            this.phoneNumber=companyDomain.phoneNumber;
            this.emailAddress=companyDomain.emailAddress;
            this.isActive=companyDomain.isActive;
            return this;
        }

        public CompanyDomain build(){
            return new CompanyDomain(this);
        }

    }
}
