package com.aiva.aivacrm.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aiva.aivacrm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import model.Employe;
import network.ApiService;
import network.AuthResponse;
import network.EmployeResponse;
import network.RetrofitClientInstance;
import network.UserSessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final CheckBox rememberMeCheckBox = findViewById(R.id.remember_me);
        Button loginButton = findViewById(R.id.loginButton);

        // Load saved credentials if "Remember Me" was checked
        loadSavedCredentials(usernameEditText, passwordEditText, rememberMeCheckBox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String hashedPassword = hashPassword(password).toLowerCase(); // Ensure it's lowercase

                authenticate(username, hashedPassword);

                // Save credentials if "Remember Me" is checked
                if (rememberMeCheckBox.isChecked()) {
                    UserSessionManager.saveRememberMeCredentials(LoginActivity.this, username, password);
                } else {
                    UserSessionManager.clearRememberMeCredentials(LoginActivity.this);
                }
            }
        });
    }

    private void authenticate(String username, String hashedPassword) {
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<AuthResponse> call = service.authenticate(username, hashedPassword);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String userId = response.body().getUserId();
                    Log.d("LoginActivity", "Received userId: " + userId);
                    String apiKey = hashPassword(response.body().getUserId() + username + response.body().getApiKey()).toLowerCase();
                    UserSessionManager.saveUsername(LoginActivity.this, username);
                    UserSessionManager.saveApiKey(LoginActivity.this, apiKey);
                    UserSessionManager.saveUserId(LoginActivity.this, userId);

                    getEmployeeDetails(userId, hashedPassword);
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

    private void getEmployeeDetails(String userId, String hashedPassword) {
        String apiKey = UserSessionManager.getApiKey(this);
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e("GetTasks", "API Key not found. Please login again.");
            return;
        }

        Log.d("LoginActivity", "Fetching details for userId: " + userId);

        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        // Create the JSON object for the where clause
        JSONObject whereClauseJson = new JSONObject();
        try {
            whereClauseJson.put("employeID", userId);
        } catch (JSONException e) {
            Log.e("LoginActivity", "Failed to create where clause JSON", e);
            return;
        }

        String whereClause = whereClauseJson.toString();
        Log.d("LoginActivity", "Using where clause: " + whereClause);

        Call<EmployeResponse> call = service.getEmployeeDetails(userId, apiKey, "*", "", "select", whereClause, "1", "");
        call.enqueue(new Callback<EmployeResponse>() {
            @Override
            public void onResponse(Call<EmployeResponse> call, Response<EmployeResponse> response) {
                Log.d("LoginActivity", "API response: " + response.raw());

                if (response.isSuccessful() && response.body() != null) {
                    EmployeResponse employeeResponse = response.body();
                    Log.d("LoginActivity", "Employee response received: " + employeeResponse);

                    // Handle the case where data might be an empty array or null
                    if (employeeResponse.getData() != null && employeeResponse.getData().getEmploye() != null
                            && !employeeResponse.getData().getEmploye().isEmpty()) {
                        Employe employee = employeeResponse.getData().getEmploye().get(0);
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
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Handle appropriately
        }
    }

    private void loadSavedCredentials(EditText usernameEditText, EditText passwordEditText, CheckBox rememberMeCheckBox) {
        String savedUsername = UserSessionManager.getSavedUsername(this);
        String savedPassword = UserSessionManager.getSavedPassword(this);
        if (savedUsername != null && savedPassword != null) {
            usernameEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);
        }
    }
}
