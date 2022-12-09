package network;

import java.util.List;

import model.Address;
import model.Customer;
import model.Task;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface TasksApi {


    @GET("tasks.php")
    Call<List<Task>> getAllTasks();
    @GET("customers.php")
    Call<List<Customer>> getAllCustomers();
    @GET("addresses.php")
    Call<List<Address>> getAllAddresses();
    @PUT("tasks.php")
    Call<Task> updateTask(@Body Task task);
}
