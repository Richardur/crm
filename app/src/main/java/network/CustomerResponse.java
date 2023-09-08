package network;

import java.util.List;

import model.Customer;

public class CustomerResponse {

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CustomerResponse.CustomerData getData() {
        return data;
    }

    public void setData(CustomerResponse.CustomerData data) {
        this.data = data;
    }

    private CustomerResponse.CustomerData data;


    public class CustomerData {
        Customer customer;

        private List<model.Customer> Customer;
        public model.Customer getCustomer() {
            return customer;
        }

        public void setCustomer(List<model.Customer> customer) {
            Customer = customer;
        }



        // Getters and setters for Customer
    }
}
