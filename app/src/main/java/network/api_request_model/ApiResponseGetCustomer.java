package network.api_request_model;

import com.google.gson.annotations.SerializedName;
import java.util.List; // Import List
import model.Customer;

public class ApiResponseGetCustomer {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Data data; // Change the data field to hold a Data object

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    // Define the Data class to represent the nested structure
    public static class Data {
        @SerializedName("Customer")
        private List<Customer> customers; // Change this to a list of customers

        public List<Customer> getCustomer() {
            return customers;
        }
    }

    // You can remove the single Customer field

    // Add any other fields as needed
}
