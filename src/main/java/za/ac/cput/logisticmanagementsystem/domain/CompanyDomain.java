/* last Time worked on Wednesday 11/11/2026 04:03
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
    private String companyType;
    private String taxId;
    private String phoneNumber;
    private String emailAddress;
    private boolean isActive;

    protected CompanyDomain() {}

    private CompanyDomain(Builder builder) {
        this.companyId=builder.companyId;
        this.companyName=builder.companyName;
        this.companyType=builder.companyType;
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

    public String getCompanyType() {
        return companyType;
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
                ", companyType='" + companyType + '\'' +
                ", taxId='" + taxId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    public static class Builder{
        private String companyId;
        private String companyName;
        private String companyType;
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

        public Builder setCompanyType(String companyType) {
            this.companyType = companyType;
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
            this.companyType=companyDomain.companyType;
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
