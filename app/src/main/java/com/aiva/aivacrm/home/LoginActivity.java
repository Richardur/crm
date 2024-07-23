package com.aiva.aivacrm.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.aiva.aivacrm.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import model.Employe;
import network.ApiService;
import network.AuthResponse;
import network.EmployeResponse;
import network.RetrofitClientInstance;
import network.UserSessionManager;
import network.api_request_model.ApiResponseReactionPlan;
import network.api_request_model.ManagerWorkInPlan;
import network.api_request_model.ManagerWorkInPlanDeserializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String hashedPassword = hashPassword(password).toLowerCase(); // Ensure it's lowercase

                ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
                Call<AuthResponse> call = service.authenticate(username, hashedPassword);
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null ) {
                            String userId = response.body().getUserId();
                            String apiKey = hashPassword(response.body().getUserId() + username + response.body().getApiKey()).toLowerCase();
                            UserSessionManager.saveUsername(LoginActivity.this, username);
                            UserSessionManager.saveApiKey(LoginActivity.this, apiKey);
                            UserSessionManager.saveUserId(LoginActivity.this, response.body().getUserId());

                            // Make the new API call to get employee details
                            getEmployeeDetails(username, hashedPassword);


                            startActivity(new Intent(LoginActivity.this, DailyTasks.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void getEmployeeDetails(String username, String hashedPassword) {
        String apiKey = UserSessionManager.getApiKey(this);
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e("GetTasks", "API Key not found. Please login again.");
            return;
        }
        String userId = UserSessionManager.getUserId(this);
        if (userId == null || userId.isEmpty()) {
            Log.e("GetTasks", "User ID not found. Please login again.");
            return;
        }

        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<EmployeResponse> call = service.getEmployeeDetails(userId, apiKey, "*", "", "select", "", "1", "");
        call.enqueue(new Callback<EmployeResponse>() {
            @Override
            public void onResponse(Call<EmployeResponse> call, Response<EmployeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EmployeResponse employeeResponse = response.body();
                    if (employeeResponse.getData() != null && !employeeResponse.getData().getEmploye().isEmpty()) {
                        Employe employee = employeeResponse.getData().getEmploye().get(0); // Assuming you're interested in the first employee
                        Log.d("LoginActivity", "Employee received: ID = " + employee.getEmployeID() + ", Name = " + employee.getEmployeName());

                        if (employee.getEmployeID() == 0) {
                            Log.e("LoginActivity", "Received employee with invalid ID");
                            return;
                        }

                        // Save employee details
                        UserSessionManager.saveEmployeeDetails(LoginActivity.this, employee);
                        startActivity(new Intent(LoginActivity.this, DailyTasks.class));
                        finish();
                    } else {
                        Log.e("LoginActivity", "No employee data found in response.");
                        Toast.makeText(LoginActivity.this, "No employee details found", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("LoginActivity", "Failed to get employee details: " + response.code() + " - " + response.message());
                    Toast.makeText(LoginActivity.this, "Failed to get employee details", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<EmployeResponse> call, Throwable t) {
                Log.e("LoginActivity", "API call failed", t);
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Handle appropriately
        }
    }
}

