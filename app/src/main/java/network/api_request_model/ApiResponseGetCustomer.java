package network.api_request_model;

import com.google.gson.annotations.SerializedName;
import java.util.List; // Import List
import model.Customer;

public class ApiResponseGetCustomer {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Data data; // Data object that holds customer information

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Define the Data class to represent the nested structure
    public static class Data {
        @SerializedName("Customer")
        private List<Customer> customers; // List of Customer objects

        public List<Customer> getCustomers() {
            return customers;
        }

        public void setCustomers(List<Customer> customers) {
            this.customers = customers;
        }
    }

    // Removed the single Customer field as the new structure encapsulates a list of Customers
}
