package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Customer {
    @SerializedName("customerID")
    private String customerID;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerShortName")
    private String customerShortName;

    @SerializedName("customerCode")
    private String customerCode;

    @SerializedName("customerVAT")
    private String customerVat;

    @SerializedName("customerType")
    private String customerType;

    @SerializedName("customerActivityDescription")
    private String customerActivityDescription;

    @SerializedName("customerActive")
    private String customerActive;

    @SerializedName("customerCountryID")
    private String customerCountryID;

    @SerializedName("customerCountryCode")
    private String customerCountryCode;

    @SerializedName("customerCountryES")
    private String customerCountryES;

    @SerializedName("customerCountryName")
    private String customerCountryName;

    @SerializedName("customerRegionID")
    private String customerRegionID;

    @SerializedName("customerRegionName")
    private String customerRegionName;

    @SerializedName("customerRegionCountryID")
    private String customerRegionCountryID;

    @SerializedName("customerRegionCountryName")
    private String customerRegionCountryName;

    @SerializedName("customerAdressPostIndex")
    private String customerAdressPostIndex;

    @SerializedName("customerAdressCity")
    private String customerAdressCity;

    @SerializedName("customerAdressStreet")
    private String customerAdressStreet;

    @SerializedName("customerAdressHouse")
    private String customerAdressHouse;

    @SerializedName("customerContactPhone")
    private String customerContactPhone;

    @SerializedName("customerContactMail")
    private String customerContactMail;

    @SerializedName("customerWWW")
    private String customerWWW;

    @SerializedName("customerManagerID")
    private String customerManagerID;

    @SerializedName("customerManagerName")
    private String customerManagerName;

    @SerializedName("customerMediatorID")
    private String customerMediatorID;

    @SerializedName("customerMediatorName")
    private String customerMediatorName;

    @SerializedName("customerProjectManagerID")
    private String customerProjectManagerID;

    @SerializedName("customerProjectManagerName")
    private String customerProjectManagerName;

    @SerializedName("customerPriceType")
    private String customerPriceType;

    @SerializedName("customerDiscount")
    private String customerDiscount;

    @SerializedName("customerBankInfo")
    private String customerBankInfo;

    @SerializedName("customerImportanceDegree")
    private String customerImportanceDegree;

    @SerializedName("customerCompanyBranchQuantity")
    private String customerCompanyBranchQuantity;

    @SerializedName("customerCompanyEmployeesQuantity")
    private String customerCompanyEmployeesQuantity;

    @SerializedName("customerCompanyTurnoverFrom")
    private String customerCompanyTurnoverFrom;

    @SerializedName("customerCompanyTurnoverFor")
    private String customerCompanyTurnoverFor;

    @SerializedName("customerMarketNote")
    private String customerMarketNote;

    @SerializedName("customerLoginUser")
    private String customerLoginUser;

    @SerializedName("customerLoginPassword")
    private String customerLoginPassword;

    @SerializedName("customerCreateDate")
    private String customerCreateDate;

    @SerializedName("CustomerContactPerson") // Correct field name as per the JSON response
    private List<CustomerContactPerson> customerContactPersons;

    @SerializedName("CustomerInfoForSales")
    private List<CustomerInfoForSales> customerInfoForSales;


    // Getter and setter methods for each field
    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerShortName() {
        return customerShortName;
    }

    public void setCustomerShortName(String customerShortName) {
        this.customerShortName = customerShortName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerVat() {
        return customerVat;
    }

    public void setCustomerVat(String customerVat) {
        this.customerVat = customerVat;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerActivityDescription() {
        return customerActivityDescription;
    }

    public void setCustomerActivityDescription(String customerActivityDescription) {
        this.customerActivityDescription = customerActivityDescription;
    }

    public String getCustomerActive() {
        return customerActive;
    }

    public void setCustomerActive(String customerActive) {
        this.customerActive = customerActive;
    }

    public String getCustomerCountryID() {
        return customerCountryID;
    }

    public void setCustomerCountryID(String customerCountryID) {
        this.customerCountryID = customerCountryID;
    }

    public String getCustomerCountryCode() {
        return customerCountryCode;
    }

    public void setCustomerCountryCode(String customerCountryCode) {
        this.customerCountryCode = customerCountryCode;
    }

    public String getCustomerCountryES() {
        return customerCountryES;
    }

    public void setCustomerCountryES(String customerCountryES) {
        this.customerCountryES = customerCountryES;
    }

    public String getCustomerCountryName() {
        return customerCountryName;
    }

    public void setCustomerCountryName(String customerCountryName) {
        this.customerCountryName = customerCountryName;
    }

    public String getCustomerRegionID() {
        return customerRegionID;
    }

    public void setCustomerRegionID(String customerRegionID) {
        this.customerRegionID = customerRegionID;
    }

    public String getCustomerRegionName() {
        return customerRegionName;
    }

    public void setCustomerRegionName(String customerRegionName) {
        this.customerRegionName = customerRegionName;
    }

    public String getCustomerRegionCountryID() {
        return customerRegionCountryID;
    }

    public void setCustomerRegionCountryID(String customerRegionCountryID) {
        this.customerRegionCountryID = customerRegionCountryID;
    }

    public String getCustomerRegionCountryName() {
        return customerRegionCountryName;
    }

    public void setCustomerRegionCountryName(String customerRegionCountryName) {
        this.customerRegionCountryName = customerRegionCountryName;
    }

    public String getCustomerAdressPostIndex() {
        return customerAdressPostIndex;
    }

    public void setCustomerAdressPostIndex(String customerAdressPostIndex) {
        this.customerAdressPostIndex = customerAdressPostIndex;
    }

    public String getCustomerAdressCity() {
        return customerAdressCity;
    }

    public void setCustomerAdressCity(String customerAdressCity) {
        this.customerAdressCity = customerAdressCity;
    }

    public String getCustomerAdressStreet() {
        return customerAdressStreet;
    }

    public void setCustomerAdressStreet(String customerAdressStreet) {
        this.customerAdressStreet = customerAdressStreet;
    }

    public String getCustomerAdressHouse() {
        return customerAdressHouse;
    }

    public void setCustomerAdressHouse(String customerAdressHouse) {
        this.customerAdressHouse = customerAdressHouse;
    }

    public String getCustomerContactPhone() {
        return customerContactPhone;
    }

    public void setCustomerContactPhone(String customerContactPhone) {
        this.customerContactPhone = customerContactPhone;
    }

    public String getCustomerContactMail() {
        return customerContactMail;
    }

    public void setCustomerContactMail(String customerContactMail) {
        this.customerContactMail = customerContactMail;
    }

    public String getCustomerWWW() {
        return customerWWW;
    }

    public void setCustomerWWW(String customerWWW) {
        this.customerWWW = customerWWW;
    }

    public String getCustomerManagerID() {
        return customerManagerID;
    }

    public void setCustomerManagerID(String customerManagerID) {
        this.customerManagerID = customerManagerID;
    }

    public String getCustomerManagerName() {
        return customerManagerName;
    }

    public void setCustomerManagerName(String customerManagerName) {
        this.customerManagerName = customerManagerName;
    }

    public String getCustomerMediatorID() {
        return customerMediatorID;
    }

    public void setCustomerMediatorID(String customerMediatorID) {
        this.customerMediatorID = customerMediatorID;
    }

    public String getCustomerMediatorName() {
        return customerMediatorName;
    }

    public void setCustomerMediatorName(String customerMediatorName) {
        this.customerMediatorName = customerMediatorName;
    }

    public String getCustomerProjectManagerID() {
        return customerProjectManagerID;
    }

    public void setCustomerProjectManagerID(String customerProjectManagerID) {
        this.customerProjectManagerID = customerProjectManagerID;
    }

    public String getCustomerProjectManagerName() {
        return customerProjectManagerName;
    }

    public void setCustomerProjectManagerName(String customerProjectManagerName) {
        this.customerProjectManagerName = customerProjectManagerName;
    }

    public String getCustomerPriceType() {
        return customerPriceType;
    }

    public void setCustomerPriceType(String customerPriceType) {
        this.customerPriceType = customerPriceType;
    }

    public String getCustomerDiscount() {
        return customerDiscount;
    }

    public void setCustomerDiscount(String customerDiscount) {
        this.customerDiscount = customerDiscount;
    }

    public String getCustomerBankInfo() {
        return customerBankInfo;
    }

    public void setCustomerBankInfo(String customerBankInfo) {
        this.customerBankInfo = customerBankInfo;
    }

    public String getCustomerImportanceDegree() {
        return customerImportanceDegree;
    }

    public void setCustomerImportanceDegree(String customerImportanceDegree) {
        this.customerImportanceDegree = customerImportanceDegree;
    }

    public String getCustomerCompanyBranchQuantity() {
        return customerCompanyBranchQuantity;
    }

    public void setCustomerCompanyBranchQuantity(String customerCompanyBranchQuantity) {
        this.customerCompanyBranchQuantity = customerCompanyBranchQuantity;
    }

    public String getCustomerCompanyEmployeesQuantity() {
        return customerCompanyEmployeesQuantity;
    }

    public void setCustomerCompanyEmployeesQuantity(String customerCompanyEmployeesQuantity) {
        this.customerCompanyEmployeesQuantity = customerCompanyEmployeesQuantity;
    }

    public String getCustomerCompanyTurnoverFrom() {
        return customerCompanyTurnoverFrom;
    }

    public void setCustomerCompanyTurnoverFrom(String customerCompanyTurnoverFrom) {
        this.customerCompanyTurnoverFrom = customerCompanyTurnoverFrom;
    }

    public String getCustomerCompanyTurnoverFor() {
        return customerCompanyTurnoverFor;
    }

    public void setCustomerCompanyTurnoverFor(String customerCompanyTurnoverFor) {
        this.customerCompanyTurnoverFor = customerCompanyTurnoverFor;
    }

    public String getCustomerMarketNote() {
        return customerMarketNote;
    }

    public void setCustomerMarketNote(String customerMarketNote) {
        this.customerMarketNote = customerMarketNote;
    }

    public String getCustomerLoginUser() {
        return customerLoginUser;
    }

    public void setCustomerLoginUser(String customerLoginUser) {
        this.customerLoginUser = customerLoginUser;
    }

    public String getCustomerLoginPassword() {
        return customerLoginPassword;
    }

    public void setCustomerLoginPassword(String customerLoginPassword) {
        this.customerLoginPassword = customerLoginPassword;
    }

    public String getCustomerCreateDate() {
        return customerCreateDate;
    }

    public void setCustomerCreateDate(String customerCreateDate) {
        this.customerCreateDate = customerCreateDate;
    }

    public List<CustomerContactPerson> getCustomerContactPersons() {
        return customerContactPersons;
    }

    public void setCustomerContactPersons(List<CustomerContactPerson> customerContactPersons) {
        this.customerContactPersons = customerContactPersons;
    }

    public List<CustomerInfoForSales> getCustomerInfoForSales() {
        return customerInfoForSales;
    }

    public void setCustomerInfoForSales(List<CustomerInfoForSales> customerInfoForSales) {
        this.customerInfoForSales = customerInfoForSales;
    }

    public static class CustomerContactPerson {

        public boolean isRepresentative() {
            return contactPersonType != null && (contactPersonType.charAt(2) == '1');
        }

        @SerializedName("contactPersonType")
        private String contactPersonType;

        @SerializedName("contactPersonName")
        private String contactPersonName;

        @SerializedName("contactPersonSurname")
        private String contactPersonSurname;

        @SerializedName("contactPersonResponsibilities")
        private String contactPersonResponsibilities;

        @SerializedName("employeResponsibilitiesID")
        private String employeResponsibilitiesID;

        @SerializedName("contactPersonPhone")
        private String contactPersonPhone;

        @SerializedName("contactPersonMobPhone")
        private String contactPersonMobPhone;

        @SerializedName("contactPersonMail")
        private String contactPersonMail;

        @SerializedName("contactPersonBornDate")
        private String contactPersonBornDate;

        @SerializedName("contactPersonLoginUser")
        private String contactPersonLoginUser;

        @SerializedName("contactPersonLoginPassword")
        private String contactPersonLoginPassword;

        @SerializedName("contactPersonPriority")
        private String contactPersonPriority;

        // Getter and setter methods for each field
        public String getContactPersonType() {
            return contactPersonType;
        }

        public void setContactPersonType(String contactPersonType) {
            this.contactPersonType = contactPersonType;
        }

        public String getContactPersonName() {
            return contactPersonName;
        }

        public void setContactPersonName(String contactPersonName) {
            this.contactPersonName = contactPersonName;
        }

        public String getContactPersonSurname() {
            return contactPersonSurname;
        }

        public void setContactPersonSurname(String contactPersonSurname) {
            this.contactPersonSurname = contactPersonSurname;
        }

        public String getContactPersonResponsibilities() {
            return contactPersonResponsibilities;
        }

        public void setContactPersonResponsibilities(String contactPersonResponsibilities) {
            this.contactPersonResponsibilities = contactPersonResponsibilities;
        }

        public String getEmployeResponsibilitiesID() {
            return employeResponsibilitiesID;
        }

        public void setEmployeResponsibilitiesID(String employeResponsibilitiesID) {
            this.employeResponsibilitiesID = employeResponsibilitiesID;
        }

        public String getContactPersonPhone() {
            return contactPersonPhone;
        }

        public void setContactPersonPhone(String contactPersonPhone) {
            this.contactPersonPhone = contactPersonPhone;
        }

        public String getContactPersonMobPhone() {
            return contactPersonMobPhone;
        }

        public void setContactPersonMobPhone(String contactPersonMobPhone) {
            this.contactPersonMobPhone = contactPersonMobPhone;
        }

        public String getContactPersonMail() {
            return contactPersonMail;
        }

        public void setContactPersonMail(String contactPersonMail) {
            this.contactPersonMail = contactPersonMail;
        }

        public String getContactPersonBornDate() {
            return contactPersonBornDate;
        }

        public void setContactPersonBornDate(String contactPersonBornDate) {
            this.contactPersonBornDate = contactPersonBornDate;
        }

        public String getContactPersonLoginUser() {
            return contactPersonLoginUser;
        }

        public void setContactPersonLoginUser(String contactPersonLoginUser) {
            this.contactPersonLoginUser = contactPersonLoginUser;
        }

        public String getContactPersonLoginPassword() {
            return contactPersonLoginPassword;
        }

        public void setContactPersonLoginPassword(String contactPersonLoginPassword) {
            this.contactPersonLoginPassword = contactPersonLoginPassword;
        }

        public String getContactPersonPriority() {
            return contactPersonPriority;
        }

        public void setContactPersonPriority(String contactPersonPriority) {
            this.contactPersonPriority = contactPersonPriority;
        }
    }

    public static class CustomerInfoForSales {
        @SerializedName("customerResolution")
        private String customerResolution;

        @SerializedName("customerDeliveryCondition")
        private String customerDeliveryCondition;

        @SerializedName("customerDeliveryMethod")
        private String customerDeliveryMethod;

        @SerializedName("customerPaymentCondition")
        private String customerPaymentCondition;

        @SerializedName("customerCreditingCreditLimit")
        private String customerCreditingCreditLimit;

        @SerializedName("customerCreditingCreditDayTerm")
        private String customerCreditingCreditDayTerm;

        @SerializedName("customerCreditingReliability")
        private String customerCreditingReliability;

        @SerializedName("customerCreditingContractNo")
        private String customerCreditingContractNo;

        @SerializedName("customerCreditingContractData")
        private String customerCreditingContractData;

        @SerializedName("customerDebtSum")
        private String customerDebtSum;

        @SerializedName("customerOverdueDebtSuma")
        private String customerOverdueDebtSuma;

        @SerializedName("customerCreditContractBankID")
        private String customerCreditContractBankID;

        @SerializedName("customerCreditContractBankName")
        private String customerCreditContractBankName;

        @SerializedName("customerCreditContractBankAccountNo")
        private String customerCreditContractBankAccountNo;

        @SerializedName("customerCreditContractCorrespBankID")
        private String customerCreditContractCorrespBankID;

        @SerializedName("customerCreditContractCorrespBankName")
        private String customerCreditContractCorrespBankName;

        @SerializedName("customerCreditContractCorrespBankAccountNo")
        private String customerCreditContractCorrespBankAccountNo;

        @SerializedName("customerCreditContractSWIFTCode")
        private String customerCreditContractSWIFTCode;

        @SerializedName("customerCreditContractCustomerCode")
        private String customerCreditContractCustomerCode;

        @SerializedName("customerCreditContractCustomerVATCode")
        private String customerCreditContractCustomerVATCode;

        @SerializedName("customerCreditContractNo")
        private String customerCreditContractNo;

        @SerializedName("customerCreditContractData")
        private String customerCreditContractData;

        @SerializedName("customerCreditContractPaymentDayTerm")
        private String customerCreditContractPaymentDayTerm;

        @SerializedName("customerCreditContractPercOfAdvance")
        private String customerCreditContractPercOfAdvance;

        @SerializedName("customerCreditContractInfo")
        private String customerCreditContractInfo;

        // Getter and setter methods for each field
        public String getCustomerResolution() {
            return customerResolution;
        }

        public void setCustomerResolution(String customerResolution) {
            this.customerResolution = customerResolution;
        }

        public String getCustomerDeliveryCondition() {
            return customerDeliveryCondition;
        }

        public void setCustomerDeliveryCondition(String customerDeliveryCondition) {
            this.customerDeliveryCondition = customerDeliveryCondition;
        }

        public String getCustomerDeliveryMethod() {
            return customerDeliveryMethod;
        }

        public void setCustomerDeliveryMethod(String customerDeliveryMethod) {
            this.customerDeliveryMethod = customerDeliveryMethod;
        }

        public String getCustomerPaymentCondition() {
            return customerPaymentCondition;
        }

        public void setCustomerPaymentCondition(String customerPaymentCondition) {
            this.customerPaymentCondition = customerPaymentCondition;
        }

        public String getCustomerCreditingCreditLimit() {
            return customerCreditingCreditLimit;
        }

        public void setCustomerCreditingCreditLimit(String customerCreditingCreditLimit) {
            this.customerCreditingCreditLimit = customerCreditingCreditLimit;
        }

        public String getCustomerCreditingCreditDayTerm() {
            return customerCreditingCreditDayTerm;
        }

        public void setCustomerCreditingCreditDayTerm(String customerCreditingCreditDayTerm) {
            this.customerCreditingCreditDayTerm = customerCreditingCreditDayTerm;
        }

        public String getCustomerCreditingReliability() {
            return customerCreditingReliability;
        }

        public void setCustomerCreditingReliability(String customerCreditingReliability) {
            this.customerCreditingReliability = customerCreditingReliability;
        }

        public String getCustomerCreditingContractNo() {
            return customerCreditingContractNo;
        }

        public void setCustomerCreditingContractNo(String customerCreditingContractNo) {
            this.customerCreditingContractNo = customerCreditingContractNo;
        }

        public String getCustomerCreditingContractData() {
            return customerCreditingContractData;
        }

        public void setCustomerCreditingContractData(String customerCreditingContractData) {
            this.customerCreditingContractData = customerCreditingContractData;
        }

        public String getCustomerDebtSum() {
            return customerDebtSum;
        }

        public void setCustomerDebtSum(String customerDebtSum) {
            this.customerDebtSum = customerDebtSum;
        }

        public String getCustomerOverdueDebtSuma() {
            return customerOverdueDebtSuma;
        }

        public void setCustomerOverdueDebtSuma(String customerOverdueDebtSuma) {
            this.customerOverdueDebtSuma = customerOverdueDebtSuma;
        }

        public String getCustomerCreditContractBankID() {
            return customerCreditContractBankID;
        }

        public void setCustomerCreditContractBankID(String customerCreditContractBankID) {
            this.customerCreditContractBankID = customerCreditContractBankID;
        }

        public String getCustomerCreditContractBankName() {
            return customerCreditContractBankName;
        }

        public void setCustomerCreditContractBankName(String customerCreditContractBankName) {
            this.customerCreditContractBankName = customerCreditContractBankName;
        }

        public String getCustomerCreditContractBankAccountNo() {
            return customerCreditContractBankAccountNo;
        }

        public void setCustomerCreditContractBankAccountNo(String customerCreditContractBankAccountNo) {
            this.customerCreditContractBankAccountNo = customerCreditContractBankAccountNo;
        }

        public String getCustomerCreditContractCorrespBankID() {
            return customerCreditContractCorrespBankID;
        }

        public void setCustomerCreditContractCorrespBankID(String customerCreditContractCorrespBankID) {
            this.customerCreditContractCorrespBankID = customerCreditContractCorrespBankID;
        }

        public String getCustomerCreditContractCorrespBankName() {
            return customerCreditContractCorrespBankName;
        }

        public void setCustomerCreditContractCorrespBankName(String customerCreditContractCorrespBankName) {
            this.customerCreditContractCorrespBankName = customerCreditContractCorrespBankName;
        }

        public String getCustomerCreditContractCorrespBankAccountNo() {
            return customerCreditContractCorrespBankAccountNo;
        }

        public void setCustomerCreditContractCorrespBankAccountNo(String customerCreditContractCorrespBankAccountNo
        ) {
            this.customerCreditContractCorrespBankAccountNo = customerCreditContractCorrespBankAccountNo;
        }

        public String getCustomerCreditContractSWIFTCode() {
            return customerCreditContractSWIFTCode;
        }

        public void setCustomerCreditContractSWIFTCode(String customerCreditContractSWIFTCode) {
            this.customerCreditContractSWIFTCode = customerCreditContractSWIFTCode;
        }

        public String getCustomerCreditContractCustomerCode() {
            return customerCreditContractCustomerCode;
        }

        public void setCustomerCreditContractCustomerCode(String customerCreditContractCustomerCode) {
            this.customerCreditContractCustomerCode = customerCreditContractCustomerCode;
        }

        public String getCustomerCreditContractCustomerVATCode() {
            return customerCreditContractCustomerVATCode;
        }

        public void setCustomerCreditContractCustomerVATCode(String customerCreditContractCustomerVATCode) {
            this.customerCreditContractCustomerVATCode = customerCreditContractCustomerVATCode;
        }

        public String getCustomerCreditContractNo() {
            return customerCreditContractNo;
        }

        public void setCustomerCreditContractNo(String customerCreditContractNo) {
            this.customerCreditContractNo = customerCreditContractNo;
        }

        public String getCustomerCreditContractData() {
            return customerCreditContractData;
        }

        public void setCustomerCreditContractData(String customerCreditContractData) {
            this.customerCreditContractData = customerCreditContractData;
        }

        public String getCustomerCreditContractPaymentDayTerm() {
            return customerCreditContractPaymentDayTerm;
        }

        public void setCustomerCreditContractPaymentDayTerm(String customerCreditContractPaymentDayTerm) {
            this.customerCreditContractPaymentDayTerm = customerCreditContractPaymentDayTerm;
        }

        public String getCustomerCreditContractPercOfAdvance() {
            return customerCreditContractPercOfAdvance;
        }

        public void setCustomerCreditContractPercOfAdvance(String customerCreditContractPercOfAdvance) {
            this.customerCreditContractPercOfAdvance = customerCreditContractPercOfAdvance;
        }

        public String getCustomerCreditContractInfo() {
            return customerCreditContractInfo;
        }

        public void setCustomerCreditContractInfo(String customerCreditContractInfo) {
            this.customerCreditContractInfo = customerCreditContractInfo;
        }
    }
}
