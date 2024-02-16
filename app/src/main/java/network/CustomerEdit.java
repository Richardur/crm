package network;

import com.google.gson.annotations.SerializedName;

public class CustomerEdit {
    @SerializedName("customerID")
    private int customerID;

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
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

    public String getCustomerVAT() {
        return customerVAT;
    }

    public void setCustomerVAT(String customerVAT) {
        this.customerVAT = customerVAT;
    }

    public int getCustomerType() {
        return customerType;
    }

    public void setCustomerType(int customerType) {
        this.customerType = customerType;
    }

    public String getCustomerActivityDescription() {
        return customerActivityDescription;
    }

    public void setCustomerActivityDescription(String customerActivityDescription) {
        this.customerActivityDescription = customerActivityDescription;
    }

    public int getCustomerActive() {
        return customerActive;
    }

    public void setCustomerActive(int customerActive) {
        this.customerActive = customerActive;
    }

    public String getCustomerCountryCode() {
        return customerCountryCode;
    }

    public void setCustomerCountryCode(String customerCountryCode) {
        this.customerCountryCode = customerCountryCode;
    }

    public String getCustomerCountryName() {
        return customerCountryName;
    }

    public void setCustomerCountryName(String customerCountryName) {
        this.customerCountryName = customerCountryName;
    }

    public int getCustomerRegionID() {
        return customerRegionID;
    }

    public void setCustomerRegionID(int customerRegionID) {
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

    public int getCustomerManagerID() {
        return customerManagerID;
    }

    public void setCustomerManagerID(int customerManagerID) {
        this.customerManagerID = customerManagerID;
    }

    public String getCustomerManagerName() {
        return customerManagerName;
    }

    public void setCustomerManagerName(String customerManagerName) {
        this.customerManagerName = customerManagerName;
    }

    public int getCustomerMediatorID() {
        return customerMediatorID;
    }

    public void setCustomerMediatorID(int customerMediatorID) {
        this.customerMediatorID = customerMediatorID;
    }

    public String getCustomerMediatorName() {
        return customerMediatorName;
    }

    public void setCustomerMediatorName(String customerMediatorName) {
        this.customerMediatorName = customerMediatorName;
    }

    public int getCustomerProjectManagerID() {
        return customerProjectManagerID;
    }

    public void setCustomerProjectManagerID(int customerProjectManagerID) {
        this.customerProjectManagerID = customerProjectManagerID;
    }

    public String getCustomerProjectManagerName() {
        return customerProjectManagerName;
    }

    public void setCustomerProjectManagerName(String customerProjectManagerName) {
        this.customerProjectManagerName = customerProjectManagerName;
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

    public CustomerEditContactPersonList getContactPersonList() {
        return contactPersonList;
    }

    public void setContactPersonList(CustomerEditContactPersonList contactPersonList) {
        this.contactPersonList = contactPersonList;
    }

    public CustomerEditCharSetList getCharSetList() {
        return charSetList;
    }

    public void setCharSetList(CustomerEditCharSetList charSetList) {
        this.charSetList = charSetList;
    }

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerShortName")
    private String customerShortName;

    @SerializedName("customerCode")
    private String customerCode;

    @SerializedName("customerVAT")
    private String customerVAT;

    @SerializedName("customerType")
    private int customerType;

    @SerializedName("customerActivityDescription")
    private String customerActivityDescription;

    @SerializedName("customerActive")
    private int customerActive;

    @SerializedName("customerCountryCode")
    private String customerCountryCode;

    @SerializedName("customerCountryName")
    private String customerCountryName;

    @SerializedName("customerRegionID")
    private int customerRegionID;

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
    private int customerManagerID;

    @SerializedName("customerManagerName")
    private String customerManagerName;

    @SerializedName("customerMediatorID")
    private int customerMediatorID;

    @SerializedName("customerMediatorName")
    private String customerMediatorName;

    @SerializedName("customerProjectManagerID")
    private int customerProjectManagerID;

    @SerializedName("customerProjectManagerName")
    private String customerProjectManagerName;

    @SerializedName("customerLoginUser")
    private String customerLoginUser;

    @SerializedName("customerLoginPassword")
    private String customerLoginPassword;

    @SerializedName("CustomerEditContactPersonList")
    private CustomerEditContactPersonList contactPersonList;

    @SerializedName("CustomerEditCharSetList")
    private CustomerEditCharSetList charSetList;

    public CustomerEdit(/* constructor parameters */) {
        // Initialize fields based on constructor parameters
    }

    // Getters and setters for all fields

    public static class CustomerEditContactPersonList {
        // Define the structure for CustomerEditContactPersonList
        // You can add fields and methods as needed
    }

    public static class CustomerEditCharSetList {
        // Define the structure for CustomerEditCharSetList
        // You can add fields and methods as needed
    }
}
