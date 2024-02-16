package network;

import android.util.Log;

import com.google.gson.*;

import java.lang.reflect.Type;

import model.Customer;

public class CustomerDeserializer implements JsonDeserializer<Customer> {
    @Override
    public Customer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Customer customer = new Customer();

        // Deserialize fields from JSON and set them in the Customer object
        customer.setCustomerID(getStringValue(jsonObject, "customerID"));
        customer.setCustomerName(getStringValue(jsonObject, "customerName"));
        customer.setCustomerShortName(getStringValue(jsonObject, "customerShortName"));
        customer.setCustomerCode(getStringValue(jsonObject, "customerCode"));
        customer.setCustomerVat(getStringValue(jsonObject, "customerVat"));
        customer.setCustomerType(getStringValue(jsonObject, "customerType"));
        customer.setCustomerActive(getStringValue(jsonObject, "customerActive"));
        customer.setCustomerCountryID(getStringValue(jsonObject, "customerCountryID"));
        customer.setCustomerCountryCode(getStringValue(jsonObject, "customerCountryCode"));
        customer.setCustomerCountryES(getStringValue(jsonObject, "customerCountryES"));
        customer.setCustomerCountryName(getStringValue(jsonObject, "customerCountryName"));
        customer.setCustomerRegionID(getStringValue(jsonObject, "customerRegionID"));
        customer.setCustomerRegionName(getStringValue(jsonObject, "customerRegionName"));
        customer.setCustomerRegionCountryID(getStringValue(jsonObject, "customerRegionCountryID"));
        customer.setCustomerRegionCountryName(getStringValue(jsonObject, "customerRegionCountryName"));
        customer.setCustomerAdressPostIndex(getStringValue(jsonObject, "customerAdressPostIndex"));
        customer.setCustomerAdressCity(getStringValue(jsonObject, "customerAdressCity"));
        customer.setCustomerAdressStreet(getStringValue(jsonObject, "customerAdressStreet"));
        customer.setCustomerAdressHouse(getStringValue(jsonObject, "customerAdressHouse"));
        customer.setCustomerContactPhone(getStringValue(jsonObject, "customerContactPhone"));
        customer.setCustomerContactMail(getStringValue(jsonObject, "customerContactMail"));
        customer.setCustomerWWW(getStringValue(jsonObject, "customerWWW"));
        customer.setCustomerManagerID(getStringValue(jsonObject, "customerManagerID"));
        customer.setCustomerManagerName(getStringValue(jsonObject, "customerManagerName"));
        customer.setCustomerMediatorID(getStringValue(jsonObject, "customerMediatorID"));
        customer.setCustomerMediatorName(getStringValue(jsonObject, "customerMediatorName"));
        customer.setCustomerProjectManagerID(getStringValue(jsonObject, "customerProjectManagerID"));
        customer.setCustomerProjectManagerName(getStringValue(jsonObject, "customerProjectManagerName"));
        customer.setCustomerPriceType(getStringValue(jsonObject, "customerPriceType"));
        customer.setCustomerDiscount(getStringValue(jsonObject, "customerDiscount"));
        customer.setCustomerBankInfo(getStringValue(jsonObject, "customerBankInfo"));
        customer.setCustomerImportanceDegree(getStringValue(jsonObject, "customerImportanceDegree"));



       // Log.d("CustomerDeserializer", "Customer Name: " + customer.getCustomerName());


        // Add more fields as needed...

        return customer;
    }

    private Integer getIntegerValue(JsonObject jsonObject, String fieldName) {
        JsonElement element = jsonObject.get(fieldName);
        if (element != null && !element.isJsonNull() && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsInt();
        } else {
            return null; // Return null for missing, null, or invalid integer values
        }
    }

    private String getStringValue(JsonObject jsonObject, String fieldName) {
        JsonElement element = jsonObject.get(fieldName);
        if (element != null && !element.isJsonNull()) {
            //Log.d("CustomerDeserializer", "Field: " + fieldName + ", Value: " + element.getAsString());
            return element.getAsString();
        } else {
            return null; // Return null for missing or null values
        }
    }
}
