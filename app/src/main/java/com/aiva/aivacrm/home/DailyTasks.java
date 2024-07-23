package com.aiva.aivacrm.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.aiva.aivacrm.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.tabs.TabLayout;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import adapter.AdapterTasks;
import model.Task;
import network.ApiService;
import network.GoogleCalendarService;
import network.GoogleCalendarServiceSingleton;
import network.GoogleSignInHelper;
import network.RetrofitClientInstance;
import network.UserSessionManager;
import network.api_request_model.ApiResponseReactionPlan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.common.api.Scope;
import com.google.api.services.calendar.CalendarScopes;

public class DailyTasks extends AppCompatActivity {

    private TextView monthYearText;
    private LocalDate selectedDate;
    private Button pickDate;
    private TabLayout weekdays;
    private Fragment fragment;
    private RecyclerView recyclerView;
    private AdapterTasks adapterTasks;
    private List<Task> taskList = new ArrayList<>();
    private String t1, t2;
    private static final int RC_SIGN_IN = 123;
    private GoogleCalendarServiceSingleton calendarServiceSingleton; // Google Calendar service
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tasks);




        initComponents();
        selectedDate = LocalDate.now();
        getTimestamps();
        setMonthView();
        setTabDate(selectedDate);

        calendarServiceSingleton = new GoogleCalendarServiceSingleton(this);

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInHelper signInHelper = new GoogleSignInHelper();
                String clientId = signInHelper.getClientId(getResources());
                Log.d(TAG, "Sign-in button clicked");

                // Step 1: Initialize Google Sign-In Options
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                       // .requestIdToken(clientId)  // Use the actual clientId here, not the string "clientId"
                        .requestEmail()
                        .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(DailyTasks.this, gso);

                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(DailyTasks.this);
                //updateUI(account);
                // Step 2: Start the Google Sign-In process
                signInWithGoogle();
            }
        });

        fragment = TasksTab.newInstance(t1, t2, selectedDate);

        getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
                setMonthView();
                int dayOfWeek = selectedDate.getDayOfWeek().getValue();
                weekdays.getTabAt(dayOfWeek - 1).select();
            }
        });
    }

    // Handle Google Sign-In
    private void signInWithGoogle() {
        // Step 3: Start the Google Sign-In process
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    // Handle Sign-In Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            com.google.android.gms.tasks.Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(com.google.android.gms.tasks.Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, you can now use the account information.
            UserSessionManager.userSignedIn = true;
            // Fetch and display calendar events without setting the API key.
            fetchAndDisplayCalendarEvents();
        } catch (ApiException e) {
            // Handle sign-in failure (e.g., user canceled, network error, etc.)
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            // You may want to show an error message to the user.
        }
    }


    // Fetch and display calendar events
    private void fetchAndDisplayCalendarEvents() {
        try {
            // Fetch calendar events using the Google Calendar service
            GoogleCalendarService calendarService = calendarServiceSingleton.getCalendarService();
            if (calendarService.getIsInitialized() == false) {
                Log.e(TAG, "Calendar service is not initialized");
                return; // Return early if the service is not initialized
            }
            List<Event> events = null;
            if (calendarService.getIsInitialized() == true){
                Log.e(TAG, "Calendar service is initialized");
                 events = calendarService.getCalendarEvents();
            }


            if (events != null) {
                // Convert events to tasks and display them in your RecyclerView
                List<Task> tasks = convertEventsToTasks(events);

                // Update your RecyclerView adapter with the tasks
                adapterTasks.updateTasks(tasks);
            } else {
                Log.e(TAG, "Failed to fetch calendar events: events list is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error fetching calendar events: " + e.getMessage(), e);

            // Handle the error appropriately in your app
            // Example: showToast("Error fetching calendar events. Please try again.");
        }
    }

    // Convert Google Calendar events to Task objects
    private List<Task> convertEventsToTasks(List<Event> events) {
        List<Task> tasks = new ArrayList<>();
        for (Event event : events) {
            // Extract relevant information from the Google Calendar event
            String taskName = event.getSummary();
            DateTime startTime = event.getStart().getDateTime();
            DateTime endTime = event.getEnd().getDateTime();
            String taskDescription = event.getDescription();

            // Transform the data to fit your Task constructor
            int ID = 0; // Set an appropriate ID or leave it as 0
            String workInPlanForCustomerName = ""; // Set the customer name if available
            String workInPlanName = taskName;
            String workInPlanNote = taskDescription != null ? taskDescription : ""; // Handle potential null description
            Timestamp workInPlanTerm = new Timestamp(startTime.getValue()); // Convert DateTime to Timestamp
            Timestamp workInPlanDone = new Timestamp(endTime.getValue()); // Convert DateTime to Timestamp

            // Additional parameters for the Task constructor
            String workInPlanForCustomerID = "0"; // Example value, replace with actual data if available
            String workInPlanForCustomerOrder = ""; // Example value, replace with actual data if available
            String workInPlanDoneBy = ""; // Example value, replace with actual data if available
            String workInPlanReviewed = ""; // Example value, replace with actual data if available
            String workInPlanDelayed = ""; // Example value, replace with actual data if available

            // Create a Task object and add it to the list
            //Task task = new Task(ID, workInPlanForCustomerName, workInPlanName, workInPlanNote, workInPlanTerm, workInPlanDone, workInPlanForCustomerID, workInPlanForCustomerOrder, workInPlanDoneBy, workInPlanReviewed);
            //tasks.add(task);
        }
        return tasks;
    }



    private void initComponents() {
        pickDate = findViewById(R.id.pickDate);
        monthYearText = findViewById(R.id.monthYearTV);
        weekdays = findViewById(R.id.tab_layout);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
        return date.format(formatter);
    }

    private void setTabDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd");
        int dayOfWeek = selectedDate.getDayOfWeek().getValue();
        String s = "" + dayOfWeek;

        String s1 = this.getResources().getString(R.string.day1);
        String s2 = this.getResources().getString(R.string.day2);
        String s3 = this.getResources().getString(R.string.day3);
        String s4 = this.getResources().getString(R.string.day4);
        String s5 = this.getResources().getString(R.string.day5);
        String s6 = this.getResources().getString(R.string.day6);
        String s7 = this.getResources().getString(R.string.day7);
        String[] st = {s1, s2, s3, s4, s5, s6, s7};

        LocalDate tempDate = date;

        for (int i = dayOfWeek; i <= 7; i++) {
            TabLayout.Tab weekday = weekdays.getTabAt(i - 1);
            weekday.setText(st[i - 1] + "\n" + tempDate.format(formatter));
            tempDate = tempDate.plusDays(1);
        }
        tempDate = date;
        for (int i = dayOfWeek; i > 0; i--) {
            TabLayout.Tab weekday = weekdays.getTabAt(i - 1);
            weekday.setText(st[i - 1] + "\n" + tempDate.format(formatter));
            tempDate = tempDate.minusDays(1);
        }
        dayOfWeek = selectedDate.getDayOfWeek().getValue();
        weekdays.getTabAt(dayOfWeek - 1).select();

        weekdays.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int prevDayOfWeek = selectedDate.getDayOfWeek().getValue();
                int selDayOfWeek = tab.getPosition() + 1;
                if (selDayOfWeek > prevDayOfWeek) {
                    selectedDate = selectedDate.plusDays(selDayOfWeek - prevDayOfWeek);
                } else if (selDayOfWeek < prevDayOfWeek) {
                    selectedDate = selectedDate.minusDays(prevDayOfWeek - selDayOfWeek);
                }
                setMonthView();
                getTimestamps();
                getSupportFragmentManager().beginTransaction().detach(fragment).commit();
                fragment = TasksTab.newInstance(t1, t2, selectedDate);
                getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void pickDate() {
        int mYear = selectedDate.getYear();
        int mMonth = selectedDate.getMonthValue() - 1; // Subtract 1 to match Calendar.MONTH indexing
        int mDay = selectedDate.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        setMonthView();
                        setTabDate(selectedDate);
                        //getTimestamps();
                        getSupportFragmentManager().beginTransaction().detach(fragment).commit();
                        fragment = TasksTab.newInstance(t1, t2, selectedDate);
                        getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void getTimestamps() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        t1 = selectedDate.atStartOfDay().toString().format(String.valueOf(formatter));
        t2 = selectedDate.plusDays(1).atStartOfDay().toString().format(String.valueOf(formatter));
    }
}
