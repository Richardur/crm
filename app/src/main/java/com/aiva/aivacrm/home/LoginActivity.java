package com.aiva.aivacrm.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.aiva.aivacrm.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.api.services.calendar.CalendarScopes;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInAccount googleAccount;

    // Notification permission launcher for Android 13+
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notification permission is required for reminders", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final CheckBox rememberMeCheckBox = findViewById(R.id.remember_me);
        MaterialButton loginButton = findViewById(R.id.loginButton);
        MaterialButton connectGoogleButton = findViewById(R.id.connect_google_button);

        // Check and request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13+ check
            checkAndRequestNotificationPermission();
        }

        // Load saved credentials if "Remember Me" was checked
        loadSavedCredentials(usernameEditText, passwordEditText, rememberMeCheckBox);

        // Set up Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new com.google.android.gms.common.api.Scope(CalendarScopes.CALENDAR))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Regular sign-in with username/password
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String hashedPassword = hashPassword(password);
                if (hashedPassword == null) {
                    Toast.makeText(LoginActivity.this, "Error processing password", Toast.LENGTH_SHORT).show();
                    return;
                }

                authenticate(username, hashedPassword);

                // Save credentials if "Remember Me" is checked
                if (rememberMeCheckBox.isChecked()) {
                    UserSessionManager.saveRememberMeCredentials(LoginActivity.this, username, password);
                } else {
                    UserSessionManager.clearRememberMeCredentials(LoginActivity.this);
                }
            }
        });

        // "Connect to Google" button click listener
        connectGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        // **Auto-Login Implementation**
        // If "Remember Me" is checked and credentials are saved, perform auto-login
        if (rememberMeCheckBox.isChecked()) {
            String savedUsername = UserSessionManager.getSavedUsername(this);
            String savedPassword = UserSessionManager.getSavedPassword(this);
            if (savedUsername != null && savedPassword != null) {
                // Optionally, show a loading indicator here
                final String hashedPassword = hashPassword(savedPassword);
                if (hashedPassword != null) {
                    authenticate(savedUsername, hashedPassword);
                }
            }
        }
    }

    private boolean isUserSignedIn() {
        String apiKey = UserSessionManager.getApiKey(this);
        String userId = UserSessionManager.getUserId(this);
        return apiKey != null && !apiKey.isEmpty() && userId != null && !userId.isEmpty();
    }

    private void checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            googleAccount = completedTask.getResult(ApiException.class);
            if (googleAccount != null) {
                // Signed in successfully with Google, but stay on the login screen
                Log.d("LoginActivity", "Google Sign-In successful: " + googleAccount.getEmail());
                Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();

                // TODO: Implement any additional logic if needed, such as linking accounts
            }
        } catch (ApiException e) {
            Log.w("LoginActivity", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_LONG).show();
        }
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

                    String apiKey = hashPassword(response.body().getUserId() + username + response.body().getApiKey());
                    if (apiKey == null) {
                        Toast.makeText(LoginActivity.this, "Error processing API key", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserSessionManager.saveUsername(LoginActivity.this, username);
                    UserSessionManager.saveApiKey(LoginActivity.this, apiKey);
                    UserSessionManager.saveUserId(LoginActivity.this, userId);

                    getEmployeeDetails(userId, hashedPassword);
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "API call failed", t);
            }
        });
    }

    private void getEmployeeDetails(String userId, String hashedPassword) {
        String apiKey = UserSessionManager.getApiKey(this);
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e("GetTasks", "API Key not found. Please login again.");
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Internal error. Please try again.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "Invalid employee data.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Save employee details
                        UserSessionManager.saveEmployeeDetails(LoginActivity.this, employee);

                        // **Start SplashActivity before navigating to DailyTasks**
                        Intent splashIntent = new Intent(LoginActivity.this, SplashActivity.class);
                        startActivity(splashIntent);
                        finish();
                    } else {
                        Log.e("LoginActivity", "No employee data found in response.");
                        Toast.makeText(LoginActivity.this, "No employee details found.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("LoginActivity", "Failed to get employee details: " + response.code() + " - " + response.message());
                    Toast.makeText(LoginActivity.this, "Failed to retrieve employee details.", Toast.LENGTH_LONG).show();
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
