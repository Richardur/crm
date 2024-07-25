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

                // Automatically log in with test API credentials
                authenticateWithTestCredentials();

                // Save credentials if "Remember Me" is checked
                if (rememberMeCheckBox.isChecked()) {
                    UserSessionManager.saveRememberMeCredentials(LoginActivity.this, username, password);
                } else {
                    UserSessionManager.clearRememberMeCredentials(LoginActivity.this);
                }
            }
        });
    }

    private void authenticateWithTestCredentials() {
        final String testUsername = "ricardas";
        final String testPassword = "ricardas";
        final String hashedTestPassword = hashPassword(testPassword).toLowerCase();

        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<AuthResponse> call = service.authenticate(testUsername, hashedTestPassword);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String userId = response.body().getUserId();
                    String apiKey = hashPassword(response.body().getUserId() + testUsername + response.body().getApiKey()).toLowerCase();
                    UserSessionManager.saveUsername(LoginActivity.this, testUsername);
                    UserSessionManager.saveApiKey(LoginActivity.this, apiKey);
                    UserSessionManager.saveUserId(LoginActivity.this, response.body().getUserId());

                    // Proceed with the entered employee credentials
                    final EditText usernameEditText = findViewById(R.id.username);
                    final EditText passwordEditText = findViewById(R.id.password);
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String hashedPassword = hashPassword(password).toLowerCase();
                    getEmployeeDetails(username, hashedPassword);
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
