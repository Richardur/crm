package data;

import android.content.Context;
import android.util.Log;

import com.aiva.aivacrm.home.TaskInfoActivity;
import com.aiva.aivacrm.home.TaskInfoActivity;
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
import network.api_response.CRMWorkResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetTasks {
    static String apiKey3;

   public static void getCustomer(Context context, String CustomerID, TaskInfoActivity.OnCustomerRetrieved callback) {
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

       String whereJsonString = String.format("{\"customerID\":\"%s\"}", CustomerID);

       Call<ApiResponseGetCustomer> call = apiService.getCustomer(userId, apiKey, "*",
               "", "select", whereJsonString, "100", "");


       call.enqueue(new Callback<ApiResponseGetCustomer>() {
           @Override
           public void onResponse(Call<ApiResponseGetCustomer> call, Response<ApiResponseGetCustomer> response) {
               ApiResponseGetCustomer customerResponse = response.body();
               if (customerResponse != null && customerResponse.isSuccess()) {
                   // Access the nested "data" object
                   ApiResponseGetCustomer.Data data = customerResponse.getData();
                   if (data != null) {
                       // Access the "Customer" list
                       List<Customer> customers = data.getCustomers(); // Correct method name to getCustomers
                       if (customers != null && !customers.isEmpty()) {
                           // Process each customer in the list
                           for (Customer customer : customers) {
                               if (customer != null) {
                                   // Now you can access the "customerName" field and other details
                                   String customerName = customer.getCustomerName();
                                   String customerEmail = customer.getCustomerContactMail();
                                   String customerPhone = customer.getCustomerContactPhone();
                                   Log.d("Customer Info", "Name: " + customerName + ", Email: " + customerEmail + ", Phone: " + customerPhone);
                                   // Additional logging or processing can be done here
                               }
                           }
                       }
                       callback.getResult(customerResponse); // Notify the caller that the customer data is available
                   }
               } else {
                   Log.d("Customer log", "Customer Request Failed - Response not successful");
               }
           }

           @Override
           public void onFailure(Call<ApiResponseGetCustomer> call, Throwable t) {
               Log.d("Customer log", "Customer Request Failed");
               Log.d("Customer log", "Error: " + t.getMessage());
           }
       });

   }

    public static void editCustomer(String apiKey3, CustomerEdit customerEdit) {
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

        Call<ApiResponseWorkPlan> call = null;

        call.enqueue(new Callback<ApiResponseWorkPlan>() {
            @Override
            public void onResponse(Call<ApiResponseWorkPlan> call, Response<ApiResponseWorkPlan> response) {
                if (response.isSuccessful()) {
                    //callback.onCustomerEdited(); // Notify that customer edit is successful
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
    public static void getActionsInfo(Context context, TasksTab.OnActionsInfoRetrieved callback){
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


        Call<CRMWorkResponse> call = apiService.getActionDetails(userId, apiKey, "*",
                "", "select", "", "1000", "");

        call.enqueue(new Callback<CRMWorkResponse>() {
            @Override
            public void onResponse(Call<CRMWorkResponse> call, Response<CRMWorkResponse> response) {
                CRMWorkResponse crmWorkResponse = response.body();
                if (crmWorkResponse != null && crmWorkResponse.isSuccess()) {
                    CRMWorkResponse.Data data = crmWorkResponse.getData();
                    if (data != null) {

                        callback.getResult(crmWorkResponse); // Notify the caller that the action data is available
                    }
                } else {
                    Log.d("Action log", "Action Request Failed - Response not successful");
                }
            }

            @Override
            public void onFailure(Call<CRMWorkResponse> call, Throwable t) {
                Log.d("Action log", "Action Request Failed");
                Log.d("Action log", "Error: " + t.getMessage());

            }
        });
        }



    public static void getWorkPlan(Context context, String t1, String t2, TasksTab.OnTasksRetrieved callback) {

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

        String whereJsonString = String.format("{\"reactionWorkTermFrom\":\"%s\",\"reactionWorkTermTo\":\"%s\"}", t1, t2);


        Call<ApiResponseReactionPlan> call = apiService.getTasksForDate(userId, apiKey, "*",
                "", "select", whereJsonString, "1000", "");
        call.enqueue(new Callback<ApiResponseReactionPlan>() {
            @Override
            public void onResponse(Call<ApiResponseReactionPlan> call, Response<ApiResponseReactionPlan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseReactionPlan apiResponseReactionPlan = response.body();
                    // Assuming getData() correctly returns a Data object that includes a list of ManagerReactionInPlanHeader
                    List<ManagerReactionWorkInPlan.ManagerReactionInPlanHeader> headers = null;
                    headers = apiResponseReactionPlan.getData().getManagerReactionInPlanHeaderList();
                    for (ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header : headers) {
                        // Assuming getManagerReactionWork() correctly returns a list of ManagerReactionWork within each header
                        for (ManagerReactionWorkInPlan.ManagerReactionWork work : header.getManagerReactionWork()) {
                            // Now you can access properties of each work, e.g., reactionWorkTerm
                            try {
                                Timestamp term = Timestamp.valueOf(work.getReactionWorkTerm());
                                // Log.d("WorkPlan term", term.toString());
                            } catch (IllegalArgumentException e) {
                                Log.e("getWorkPlan", "Error parsing date: " + e.getMessage());
                            }
                        }
                    }
                    // Trigger your callback or further processing here
                    callback.getResult(apiResponseReactionPlan);
                } else {
                    Log.e("getWorkPlan", "WorkPlan Request Failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("getWorkPlan", "Error Body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e("getWorkPlan", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseReactionPlan> call, Throwable t) {
                Log.e("getWorkPlan", "WorkPlan Request Failed: " + t.getMessage());
            }
        });


    }

    /* public static void getCustomerList(String apiKey3, TasksTab.OnCustomerListRetrieved callback ) {
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
*/
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
