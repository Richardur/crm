package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.List;

import model.Customer;

public class CustomerUpdateRequest {
    @SerializedName("userId")
    private String userId;

    @SerializedName("apiKey")
    private String apiKey;

    @SerializedName("action")
    private String action;

    @SerializedName("languageCode")
    private String languageCode;

    @SerializedName("customerID")
    private String customerId;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerCode")
    private String customerCode;

    @SerializedName("customerVAT")
    private String customerVAT;

    @SerializedName("customerType")
    private String customerType;

    @SerializedName("customerActive")
    private String customerActive;

    @SerializedName("customerAdressCity")
    private String customerAdressCity;

    @SerializedName("customerAdressStreet")
    private String customerAdressStreet;

    @SerializedName("customerAdressHouse")
    private String customerAdressHouse;

    @SerializedName("customerAdressPostIndex")
    private String customerAdressPostIndex;

    @SerializedName("customerWWW")
    private String customerWWW;

    @SerializedName("CustomerContactPersons")
    private String customerContactPersons; // JSON string of contact persons

    // Getter and setter methods for each field
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerActive() {
        return customerActive;
    }

    public void setCustomerActive(String customerActive) {
        this.customerActive = customerActive;
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

    public String getCustomerAdressPostIndex() {
        return customerAdressPostIndex;
    }

    public void setCustomerAdressPostIndex(String customerAdressPostIndex) {
        this.customerAdressPostIndex = customerAdressPostIndex;
    }

    public String getCustomerWWW() {
        return customerWWW;
    }

    public void setCustomerWWW(String customerWWW) {
        this.customerWWW = customerWWW;
    }

    public String getCustomerContactPersons() {
        return customerContactPersons;
    }

    public void setCustomerContactPersons(List<Customer.CustomerContactPerson> customerContactPersonsList) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        this.customerContactPersons = gson.toJson(customerContactPersonsList);
    }

    public Object getCustomer() {
return null;

    }
}
