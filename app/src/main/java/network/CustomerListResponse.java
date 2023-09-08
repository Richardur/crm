package network;

import java.util.List;

import model.Customer;

public class CustomerListResponse {
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CustomerListData getData() {
        return data;
    }

    public void setData(CustomerListData data) {
        this.data = data;
    }

    private CustomerListData data;


    public class CustomerListData {
        private List<model.Customer> Customer;
        public List<model.Customer> getCustomers() {
            return Customer;
        }

        public void setCustomer(List<model.Customer> customer) {
            Customer = customer;
        }



        // Getters and setters for Customer
    }
}



