package data;

import android.util.Log;

import com.aiva.aivacrm.home.TaskInfo;
import com.aiva.aivacrm.home.TasksTab;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import model.Address;
import model.Customer;
import model.Task;
import network.ApiGetRequest;
import network.ApiService;
import network.AuthResponse;
import network.CustomerListResponse;
import network.CustomerResponse;
import network.TasksApi;
import network.api_request_model.ApiResponse;
import network.api_request_model.ManagerWorkInPlan;
import network.api_request_model.ManagerWorkInPlanDeserializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetTasks {
    static String apiKey3;
 /*   public static void getTasksData(TasksTab.OnTasksRetrieved callback) {
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
    */

   public static void getCustomer(String apiKey3, TaskInfo.OnCustomerRetrieved callback) {
       HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
       interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
       OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

       Gson gson = new GsonBuilder()
               .setDateFormat("yyyy-MM-dd HH:mm:ss")
               .registerTypeAdapter(ManagerWorkInPlan.class, new ManagerWorkInPlanDeserializer())
               .create();

       Retrofit retrofit = new Retrofit.Builder()
               .baseUrl("https://gamyba.online/api-aiva/v1/")
               .addConverterFactory(GsonConverterFactory.create(gson))
               .client(client)
               .build();

       ApiService apiService = retrofit.create(ApiService.class);

       String userId = "24";
       try {
           // Create a JSON array with a single JSON object for the "where" parameter
           JSONArray whereArray = new JSONArray();
           JSONObject whereObject = new JSONObject();
           try {
               whereObject.put("column", "OrderNo");
           } catch (JSONException e) {
               e.printStackTrace();
           }
           try {
               whereObject.put("operator", "=");
           } catch (JSONException e) {
               e.printStackTrace();
           }
           try {
               whereObject.put("value", "0307Z00430");
           } catch (JSONException e) {
               e.printStackTrace();
           }
           whereArray.put(whereObject);

           // Convert the JSON array to a string
           String whereJsonString = whereArray.toString();

           // Use the whereJsonString in your API call
           Call<Customer> call = apiService.getCustomer(userId, apiKey3, "*", "", "select", whereJsonString, "100", "");




        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                callback.getResult(response.body());
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
            }
        });
    } catch (Exception e) {
           e.printStackTrace();
       }}

       /*public static void getAddressesData(TaskInfo.onAddressesRetrieved callback) {
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
        Call<List<Address>> call = taskAPI.getAllAddresses();

        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                callback.getResult(response.body());
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
            }
        });
    }
*/
    public static void connectApi(String login, String password, TasksTab.OnApiKeyRetrieved callback) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gamyba.online/api-aiva/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<AuthResponse> authCall = apiService.authenticate(login, password);
        authCall.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                // Handle the authentication response
                if (response.isSuccessful()) {
                    AuthResponse authResponse = response.body();
                    String userId = authResponse.getUserId();
                    String apiKey = authResponse.getApiKey();
                    System.out.println("User ID: " + userId);
                    System.out.println("API Key: " + apiKey);
                    String apiKey2 = generateApiKey(userId, "ricardas", apiKey);
                    System.out.println("API Key2: " + apiKey2);
                    apiKey3 = apiKey2;

                    callback.onApiKeyReceived(apiKey2);

                    Log.d("Callback", "API Key Received Callback Invoked");

                } else {
                    // Put into terminal the error message
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {

            }
        });

    }
    public static void getWorkPlan(String apiKey3, TasksTab.OnTasksRetrieved callback ) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(ManagerWorkInPlan.class, new ManagerWorkInPlanDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gamyba.online/api-aiva/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        String userId = "24";

        ApiGetRequest ApiGetRequest = new ApiGetRequest(
                userId,
                generateApiKey(userId, "ricardas", apiKey3),
                "*",
                "",
                "select",
                "[]",
                "10000",
                ""
        );

        Call<ApiResponse> call = apiService.getWorkPlanList("24", apiKey3, "*", "", "select", "[]", "100", "");
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("getWorkPlan", "WorkPlan Request Successful");
                    ApiResponse apiResponse = response.body();
                    for (ManagerWorkInPlan managerWorkInPlan : apiResponse.getData().getManagerWorkInPlanList()) {
                        Timestamp term = managerWorkInPlan.getWorkInPlanTerm();
                        Log.d("WorkPlan term", term.toString());
                    }

                    callback.getResult(apiResponse);

                    if (apiResponse != null) {
                        List<ManagerWorkInPlan> managerWorkInPlanList = apiResponse.getData().getManagerWorkInPlanList();
                        if (managerWorkInPlanList != null) {
                            // Loop through the list and log each ManagerWorkInPlan object
                            for (ManagerWorkInPlan managerWorkInPlan : managerWorkInPlanList) {
                                Log.d("WorkPlan", managerWorkInPlan.toString());
                            }
                        } else {
                            Log.e("WorkPlan", "ManagerWorkInPlanList is null");
                        }
                    } else {
                        Log.e("WorkPlan", "ApiResponse is null");
                    }
                } else {
                    // Handle the case when the response is not successful (e.g., error codes)
                    Log.e("getWorkPlan", "WorkPlan Request Failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            // Log the error body if available
                            Log.e("getWorkPlan", "Error Body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // You can check the response.errorBody() for more details
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle the case when the network request fails
                Log.e("getWorkPlan", "WorkPlan Request Failed: " + t.getMessage());
            }
        });


    }

    public static void getCustomerList(String apiKey3, TasksTab.OnCustomerListRetrieved callback ) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gamyba.online/api-aiva/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        String userId = "24";

        ApiGetRequest ApiGetRequest = new ApiGetRequest(
                userId,
                generateApiKey(userId, "ricardas", apiKey3),
                "*",
                "",
                "select",
                "[]",
                "5",
                ""
        );

        Call<CustomerListResponse> call = apiService.getCustomerList("24", apiKey3, "*", "", "select", "[]", "5", "");
        call.enqueue(new Callback<CustomerListResponse>() {
            @Override
            public void onResponse(Call<CustomerListResponse> call, Response<CustomerListResponse> response) {
                if (response.isSuccessful()) {
                    callback.getResult(response.body()); // Pass the result to the callback
                    Log.d("Callback", "Customer List Result Callback Invoked");
                    CustomerListResponse customerListResponse = response.body();
                    if (customerListResponse != null) {
                        List<Customer> customers = customerListResponse.getData().getCustomers();
                        // Process the list of customers here
                    }
                } else {
                    // Handle unsuccessful response
                    // You can check the response.errorBody() for more details
                }
            }
            @Override
            public void onFailure(Call<CustomerListResponse> call, Throwable t) {
                System.out.println("error");
            }
        });}

    public static String generateApiKey(String userId, String username, String apiKey) {
        String formString =userId + username + apiKey;
        return sha256(formString);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

    }
}
