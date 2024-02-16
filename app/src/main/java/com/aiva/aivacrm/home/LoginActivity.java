package com.aiva.aivacrm.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.aiva.aivacrm.R;

import network.ApiService;
import network.AuthResponse;
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
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
                Call<AuthResponse> call = service.authenticate(username, password);
                call.enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Assuming AuthResponse contains an API key
                            String apiKey = response.body().getApiKey();
                            UserSessionManager.saveApiKey(LoginActivity.this, apiKey);

                            // Proceed to DailyTasks activity
                            startActivity(new Intent(LoginActivity.this, DailyTasks.class));
                            finish();
                        } else {
                            // Handle login failure
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        // Handle error (e.g., network error)
                        Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

