package data;

import android.content.Context;
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
import java.util.List;
import model.Customer;
import network.ApiGetRequest;
import network.ApiService;
import network.AuthResponse;
import network.CustomerDeserializer;
import network.CustomerEdit;
import network.CustomerListResponse;
import network.UserSessionManager;
import network.api_request_model.ApiResponseGetCustomer;
import network.api_request_model.ApiResponseReactionPlan;
import network.api_request_model.ApiResponseWorkPlan;
import network.api_request_model.ManagerReactionWorkInPlan;
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

   public static void getCustomer(String apiKey3, String CustomerID, TaskInfo.OnCustomerRetrieved callback) {
       HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
       interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
       OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

       Gson gson = new GsonBuilder()
               .setDateFormat("yyyy-MM-dd HH:mm:ss")
               .registerTypeAdapter(Customer.class, new CustomerDeserializer())
               .setLenient()
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
               whereObject.put("column", "CustomerID"); // Change to the correct column name
               whereObject.put("operator", "=");
               whereObject.put("value", CustomerID); // Use the provided customerID
           } catch (JSONException e) {
               e.printStackTrace();
           }

           whereArray.put(whereObject);


           // Convert the JSON array to a string without URL encoding
           String whereJsonString = "{\"customerID\":\"" + CustomerID + "\"}";
           // Use the whereJsonString in your API call
           Call<ApiResponseGetCustomer> call = apiService.getCustomer(userId, apiKey3, "*", "", "select", whereJsonString, "100", "");


        call.enqueue(new Callback<ApiResponseGetCustomer>() {
            @Override
            public void onResponse(Call<ApiResponseGetCustomer> call, Response<ApiResponseGetCustomer> response) {
                ApiResponseGetCustomer customerResponse = response.body();
                if (customerResponse != null && customerResponse.isSuccess()) {
                    // Access the nested "data" object
                    ApiResponseGetCustomer.Data data = customerResponse.getData();
                    if (data != null) {
                        // Access the "Customer" list
                        List<Customer> customers = data.getCustomer();
                        if (customers != null && !customers.isEmpty()) {
                            // Access the first customer in the list (you may need to loop through the list if there are multiple)
                            Customer customer = customers.get(0);
                            if (customer != null) {
                                // Now you can access the "customerName" field
                                String customerName = customer.getCustomerName();
                                Log.d("Customer log", "Customer Name: " + customerName);
                            }
                        }
                    }
                } else {
                    Log.d("Customer log", "Customer Request Failed");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseGetCustomer> call, Throwable t) {
                Log.d("getCustomer", "Customer Request Failed");
                Log.d("getCustomer", t.getMessage());
            }
        });
    } catch (Exception e) {
           e.printStackTrace();
       }}

    public static void editCustomer(String apiKey3, CustomerEdit customerEdit, TaskInfo.OnCustomerEdited callback) {
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
        String apiKey = apiKey3; // Ensure you have the correct apiKey3 value
        String language = "en"; // Replace with the appropriate language
        String putType = "putTypeValue"; // Replace with the appropriate putType value
        String limit = "100"; // Replace with the desired limit
        String id = "customerId"; // Replace with the customer ID you want to edit

        Call<ApiResponseWorkPlan> call = apiService.editCustomer(
                apiKey,
                userId,
                language,
                putType,
                limit,
                id,
                customerEdit.getCustomerName(), // Replace with the appropriate customerEdit field values
                customerEdit.getCustomerCode()
        );

        call.enqueue(new Callback<ApiResponseWorkPlan>() {
            @Override
            public void onResponse(Call<ApiResponseWorkPlan> call, Response<ApiResponseWorkPlan> response) {
                if (response.isSuccessful()) {
                    callback.onCustomerEdited(); // Notify that customer edit is successful
                    Log.d("editCustomer", "Customer Edit Request Successful");
                } else {
                    // Handle unsuccessful response
                    Log.e("editCustomer", "Customer Edit Request Failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            // Log the error body if available
                            Log.e("editCustomer", "Error Body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseWorkPlan> call, Throwable t) {
                // Handle the network error
                Log.e("editCustomer", "Customer Edit Request Failed: " + t.getMessage());
            }
        });
    }

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
    public static void getWorkPlan(Context context, TasksTab.OnTasksRetrieved callback ) {
        String apiKey = UserSessionManager.getApiKey(context);
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e("GetTasks", "API Key not found. Please login again.");
            return;
        }
        String userId = UserSessionManager.getUserId(context);
        if (userId == null || userId.isEmpty()) {
            Log.e("GetTasks", "User ID not found. Please login again.");
            return;
        }
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


        ApiGetRequest ApiGetRequest = new ApiGetRequest(
                userId,
                apiKey,
                "*",
                "",
                "select",
                "[]",
                "10000",
                ""
        );

        Call<ApiResponseReactionPlan> call = apiService.getReactionPlanList(userId, apiKey, "*", "", "select", "[]", "100", "");
        call.enqueue(new Callback<ApiResponseReactionPlan>() {
            @Override
            public void onResponse(Call<ApiResponseReactionPlan> call, Response<ApiResponseReactionPlan> response) {
                if (response.isSuccessful()) {
                    Log.d("getWorkPlan", "WorkPlan Request Successful");
                    ApiResponseReactionPlan apiResponseReactionPlan = response.body();
                    for (ManagerReactionWorkInPlan managerReactionWorkInPlan : apiResponseReactionPlan.getData().getManagerReactionWorkList()) {
                        Timestamp term = Timestamp.valueOf(managerReactionWorkInPlan.getReactionWorkTerm());
                        //Log.d("WorkPlan term", term.toString());
                    }

                    callback.getResult(apiResponseReactionPlan);

                    if (apiResponseReactionPlan != null) {
                        List<ManagerReactionWorkInPlan> managerReactionWorkInPlanList = apiResponseReactionPlan.getData().getManagerReactionWorkList();
                        if (managerReactionWorkInPlanList != null) {
                            // Loop through the list and log each ManagerWorkInPlan object
                            for (ManagerReactionWorkInPlan managerReactionWorkInPlan : managerReactionWorkInPlanList) {
                               // Log.d("WorkPlan", managerWorkInPlan.toString());
                            }
                        } else {
                            //Log.e("WorkPlan", "ManagerWorkInPlanList is null");
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
            public void onFailure(Call<ApiResponseReactionPlan> call, Throwable t) {
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
                    System.out.println(response.errorBody());                    // You can check the response.errorBody() for more details

                }
            }
            @Override
            public void onFailure(Call<CustomerListResponse> call, Throwable t) {
                // Handle the network error

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
