package data;

import com.aiva.aivacrm.home.DailyTasks;
import com.aiva.aivacrm.home.TasksTab;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import model.Customer;
import model.Task;
import network.TasksApi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetTasks {

    public static void getTasksData(TasksTab.OnTasksRetrieved callback) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/BD/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        TasksApi taskAPI =retrofit.create(TasksApi.class);
        Call<List<Task>> call = taskAPI.getAllTasks();

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                callback.getResult(response.body());
            }
            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
            }
        });
    }
    public static void getCustomersData(TasksTab.OnCustomersRetrieved callback) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/BD/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        TasksApi taskAPI = retrofit.create(TasksApi.class);
        Call<List<Customer>> call = taskAPI.getAllCustomers();

        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                callback.getResult(response.body());
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
            }
        });
    }
}
