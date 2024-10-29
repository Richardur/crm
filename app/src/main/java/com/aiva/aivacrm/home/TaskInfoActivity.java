package com.aiva.aivacrm.home;

import static data.GetTasks.getCustomer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.aiva.aivacrm.databinding.ActivityTaskInfoBinding;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aiva.aivacrm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import model.CRMWork;
import model.Customer;
import model.Employe;
import model.Task;
import network.ApiResponseUpdate;
import network.ApiService;
import network.CustomerUpdateRequest;
import network.EmployeResponse;
import network.GoogleCalendarService;
import network.ManagerReactionUpdateRequest;
import network.RetrofitClientInstance;
import network.UserSessionManager;
import network.api_request_model.ApiResponseGetCustomer;
import network.api_request_model.ApiResponseReactionPlan;
import network.api_request_model.ManagerReactionWorkInPlan;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import util.NotificationWorker;

public class TaskInfoActivity extends AppCompatActivity implements NewTaskDialogFragment.OnNewTaskCreatedListener {

    private ActivityTaskInfoBinding binding;


    private String taskCustomerId;
    private int taskId;
    private String taskComment, taskCustomer, taskDate, taskDone, taskDoneDate;
    private String repName, repSurname, repPhone, repEmail;
    private String taskName, order, website, address;

    private String reactionHeaderId, reactionHeaderManagerId, reactionWorkManagerId, managerName, reactionWorkActionId;

    private TextView clientNameTextView, actionTextView, dueDateTextView, commentTextView, statusTextView;
    private TextView phoneTextView, emailTextView, websiteTextView, addressTextView;
    private TextView dueDateTitleTextView, statusTitleTextView;
    private TextView orderTextView, orderTitleTextView;
    private LinearLayout orderLayout;
    private TextInputLayout phoneLayout, emailLayout, websiteLayout, addressLayout, commentLayout;
    private EditText editPhone, editEmail, editWebsite, editAddress, editComment;
    private ImageButton callButton, emailButton, mapButton, editButton, saveButton, cancelButton;
    private CheckBox statusCheckbox;
    private FloatingActionButton newTaskButton, deleteButton;
    private TextView taskAssignee;

    private String newAssigneeId;
    private List<Employe> employeeList; // Add this line
    private Task task; // Also declare task as a member variable

    private List<Integer> dateOnlyActionIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViews();
        setListeners();

        task = getIntent().getParcelableExtra("Task"); // Assign to the member variable
        if (task != null) {
            extractTaskData(task);
            setTaskInfo(task);
            getCustomerCredentials(task);
        }
        // Fetch CRMWork data
        fetchCRMWorkData(null);

        Log.d("TaskInfoActivity", "Initializing Task with workInPlanID: " + task.getWorkInPlanID());

    }

    private void initializeViews() {
        clientNameTextView = findViewById(R.id.client_name);
        actionTextView = findViewById(R.id.action);
        dueDateTitleTextView = findViewById(R.id.due_date_title);
        dueDateTextView = findViewById(R.id.due_date);
        commentTextView = findViewById(R.id.comment);
        statusTitleTextView = findViewById(R.id.status_title);
        statusTextView = findViewById(R.id.status);
        orderTextView = findViewById(R.id.order);
        orderTitleTextView = findViewById(R.id.order_title);
        orderLayout = findViewById(R.id.order_layout);

        phoneTextView = findViewById(R.id.phone);
        emailTextView = findViewById(R.id.email);
        websiteTextView = findViewById(R.id.website);
        addressTextView = findViewById(R.id.address);

        callButton = findViewById(R.id.call_button);
        emailButton = findViewById(R.id.email_button);
        mapButton = findViewById(R.id.map_button);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);
        newTaskButton = findViewById(R.id.new_task_button);

        phoneLayout = findViewById(R.id.phone_layout);
        emailLayout = findViewById(R.id.email_layout);
        websiteLayout = findViewById(R.id.website_layout);
        addressLayout = findViewById(R.id.address_layout);
        commentLayout = findViewById(R.id.comment_layout);

        editPhone = findViewById(R.id.edit_phone);
        editEmail = findViewById(R.id.edit_email);
        editWebsite = findViewById(R.id.edit_website);
        editAddress = findViewById(R.id.edit_address);
        editComment = findViewById(R.id.edit_comment);

        statusCheckbox = findViewById(R.id.status_checkbox);
        deleteButton = findViewById(R.id.delete_button);

        taskAssignee = findViewById(R.id.task_assignee);
    }

    private void setListeners() {
        callButton.setOnClickListener(v -> {
            if (!repPhone.equals("Unassigned")) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneTextView.getText().toString()));
                startActivity(intent);
            } else {
                Toast.makeText(TaskInfoActivity.this, "Phone number is not assigned", Toast.LENGTH_SHORT).show();
            }
        });
        orderTextView.setOnClickListener(v -> {
            Intent intent = new Intent(TaskInfoActivity.this, TaskListActivity.class);
            intent.putExtra("selectedTaskId", taskId);
            intent.putExtra("clientId", taskCustomerId);
            intent.putExtra("orderId", order);
            startActivity(intent);
        });
        orderTitleTextView.setOnClickListener(v -> {
            Intent intent = new Intent(TaskInfoActivity.this, TaskListActivity.class);
            intent.putExtra("selectedTaskId", taskId);
            intent.putExtra("clientId", taskCustomerId);
            intent.putExtra("orderId", order);
            startActivity(intent);
        });
        orderLayout.setOnClickListener(v -> {
            Intent intent = new Intent(TaskInfoActivity.this, TaskListActivity.class);
            intent.putExtra("selectedTaskId", taskId);
            intent.putExtra("clientId", taskCustomerId);
            intent.putExtra("orderId", order);
            startActivity(intent);
        });

        emailButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTextView.getText().toString()});
            startActivity(Intent.createChooser(intent, "Send Email"));
        });

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=" + addressTextView.getText().toString()));
            startActivity(intent);
        });

        statusCheckbox.setOnClickListener(v -> {
            if (statusCheckbox.isChecked()) {
                showStatusConfirmationDialog("Complete the task?", true);
            } else {
                showStatusConfirmationDialog("Mark the task as not completed?", false);
            }
        });

        taskAssignee.setOnClickListener(v -> {
            // Prompt the user if they want to reassign the task
            showReassignConfirmationDialog();
        });

        editButton.setOnClickListener(v -> showEditConfirmationDialog());
        newTaskButton.setOnClickListener(v -> showNewTaskDialog());
        deleteButton.setOnClickListener(v -> deleteTask());
        binding.deleteButton.setOnClickListener(v -> deleteTask());
    }

    private void showReassignConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle("Reassign Task");
        builder.setMessage("Do you want to reassign this task?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Fetch the employee list and show the pop-up
            getEmployeeListAndShowPopup();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void getEmployeeListAndShowPopup() {
        // Show a progress dialog or loader if desired
        String apiKey = UserSessionManager.getApiKey(this);
        String userId = UserSessionManager.getUserId(this);

        if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<EmployeResponse> call = service.getEmployeeDetails(userId, apiKey, "*", "lt", "select", "", "", "");
        call.enqueue(new Callback<EmployeResponse>() {
            @Override
            public void onResponse(Call<EmployeResponse> call, Response<EmployeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    employeeList = response.body().getData().getEmploye();
                    if (employeeList != null && !employeeList.isEmpty()) {
                        showEmployeeSelectionPopup(employeeList);
                    }
                } else {
                    Toast.makeText(TaskInfoActivity.this, "Failed to get employees", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmployeResponse> call, Throwable t) {
                Toast.makeText(TaskInfoActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmployeeSelectionPopup(List<Employe> employees) {
        List<String> employeeNames = new ArrayList<>();
        for (Employe employee : employees) {
            employeeNames.add(employee.getEmployeName() + " " + employee.getEmploeerSurname());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle("Select Employee");
        builder.setItems(employeeNames.toArray(new String[0]), (dialog, which) -> {
            Employe selectedEmployee = employees.get(which);
            // Update the task assignment
            reassignTask(selectedEmployee);
        });
        builder.show();
    }

    private void reassignTask(Employe selectedEmployee) {
        String newAssigneeId = String.valueOf(selectedEmployee.getEmployeID());
        String newAssigneeName = String.valueOf(selectedEmployee.getEmployeName()+" "+selectedEmployee.getEmploeerSurname());

        String apiKey = UserSessionManager.getApiKey(TaskInfoActivity.this);
        String userId = UserSessionManager.getUserId(TaskInfoActivity.this);

        if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
            Toast.makeText(TaskInfoActivity.this, "Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        ManagerReactionUpdateRequest taskUpdateRequest = new ManagerReactionUpdateRequest();
        taskUpdateRequest.setUserId(userId);
        taskUpdateRequest.setApiKey(apiKey);
        taskUpdateRequest.setAction("update");
        taskUpdateRequest.setLanguageCode("lt");

        // Ensure reactionHeaderManagerId is not null or empty
        if (reactionHeaderManagerId == null || reactionHeaderManagerId.isEmpty()) {
            reactionHeaderManagerId = UserSessionManager.getEmployeeId(this);
        }

        ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header = new ManagerReactionWorkInPlan.ManagerReactionInPlanHeader();
        header.setReactionHeaderID(reactionHeaderId);
        header.setReactionManagerID(reactionHeaderManagerId); // Must not be null or empty
        header.setReactionForCustomerID(taskCustomerId);

        List<ManagerReactionWorkInPlan.ManagerReactionWork> works = new ArrayList<>();
        ManagerReactionWorkInPlan.ManagerReactionWork work = new ManagerReactionWorkInPlan.ManagerReactionWork();
        work.setReactionWorkID(String.valueOf(taskId));
        work.setReactionWorkManagerID(reactionWorkManagerId); // Ensure this is set
        work.setReactionWorkDoneByID(Integer.valueOf(newAssigneeId));
        work.setReactionWorkDoneByName(newAssigneeName);
        work.setReactionWorkActionID(reactionWorkActionId); // Ensure this is set
        work.setReactionWorkActionName(taskName); // Ensure this is set
        work.setReactionWorkNote(taskComment); // Ensure this is set
        work.setReactionWorkTerm(taskDate); // Ensure this is set
        // Add any other necessary fields

        works.add(work);
        taskUpdateRequest.setManagerReactionInPlanHeaderReg(header);
        taskUpdateRequest.setManagerReactionWorkReg(works);

        updateWorkPlan(this, taskUpdateRequest, new Callback<ApiResponseUpdate>() {
            @Override
            public void onResponse(Call<ApiResponseUpdate> call, Response<ApiResponseUpdate> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TaskInfoActivity.this, "Task reassigned successfully", Toast.LENGTH_SHORT).show();
                    // Update the task object with new assignee details
                    task.setReactionWorkDoneByID(newAssigneeId);
                    task.setReactionWorkDoneByName(selectedEmployee.getEmployeName() + " " + selectedEmployee.getEmploeerSurname());

                    // Update the UI
                    taskAssignee.setText("Assigned to: " + task.getReactionWorkDoneByName());
                } else {
                    Toast.makeText(TaskInfoActivity.this, "Failed to reassign task", Toast.LENGTH_SHORT).show();
                    Log.e("ReassignTask", "Failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                Toast.makeText(TaskInfoActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNewTaskDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        NewTaskDialogFragment newTaskDialogFragment = NewTaskDialogFragment.newInstance(this);
        newTaskDialogFragment.show(fragmentManager, "NewTaskDialogFragment");
    }

    @Override
    public void onNewTaskCreated(CRMWork action, String date, String time, String comment, boolean addToCalendar, boolean sendInvite) {
        createNewTask(action, date, time, comment, addToCalendar, sendInvite);
    }

    private void createNewTask(CRMWork action, String date, String time, String comment, boolean addToCalendar, boolean sendInvite) {
        String apiKey = UserSessionManager.getApiKey(this);
        String userId = UserSessionManager.getUserId(this);

        String currentEmployeeId = UserSessionManager.getEmployeeId(this);
        String currentEmployeeName = UserSessionManager.getEmployeeName(this);
        Log.d("TaskInfoActivity", "Creating new task with workInPlanID: " + task.getWorkInPlanID());


        if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        ManagerReactionUpdateRequest taskUpdateRequest = new ManagerReactionUpdateRequest();
        taskUpdateRequest.setUserId(userId);
        taskUpdateRequest.setApiKey(apiKey);
        taskUpdateRequest.setAction("update");
        taskUpdateRequest.setLanguageCode("lt");

        ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header = new ManagerReactionWorkInPlan.ManagerReactionInPlanHeader();
        header.setReactionHeaderID(reactionHeaderId);
        header.setReactionManagerID(reactionHeaderManagerId);
        header.setReactionForCustomerID(taskCustomerId);

        List<ManagerReactionWorkInPlan.ManagerReactionWork> works = new ArrayList<>();
        ManagerReactionWorkInPlan.ManagerReactionWork newWork = new ManagerReactionWorkInPlan.ManagerReactionWork();


        newWork.setReactionWorkManagerID(reactionWorkManagerId);

        // Assign the task to the current user
        newWork.setReactionWorkDoneByID(Integer.valueOf(currentEmployeeId));
        newWork.setReactionWorkDoneByName(currentEmployeeName);

        newWork.setReactionWorkActionID(String.valueOf(action.getCRMWorkID()));
        newWork.setReactionWorkActionName(action.getCRMWorkName());
        newWork.setReactionWorkTerm(date + (time.isEmpty() ? "" : " " + time));
        newWork.setReactionWorkNote(comment);
        works.add(newWork);

        taskUpdateRequest.setManagerReactionInPlanHeaderReg(header);
        taskUpdateRequest.setManagerReactionWorkReg(works);

        updateWorkPlan(this, taskUpdateRequest, new Callback<ApiResponseUpdate>() {
            @Override
            public void onResponse(Call<ApiResponseUpdate> call, Response<ApiResponseUpdate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UpdateWorkPlan", "New task creation successful");
                    int newHeaderId = response.body().getData().getReactionHeaderID();
                    // Fetch details of the newly created task
                    fetchNewTaskDetails(newHeaderId, addToCalendar, action, date, time, comment, sendInvite);

                  /*  if (addToCalendar) {
                        createGoogleCalendarEvent(action, date, time, comment, sendInvite, new Runnable() {
                            @Override
                            public void run() {
                                // After everything is done, show confirmation and finish activity
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TaskInfoActivity.this, "Task created and added to Google Calendar.", Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK);
                                        //finish();
                                    }
                                });
                            }
                        });
                    } else {
                        // If not adding to calendar, just finish
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TaskInfoActivity.this, "Task created.", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                //finish();
                            }
                        });
                    } */
                } else {
                    Log.e("UpdateWorkPlan", "New task creation failed: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TaskInfoActivity.this, "Failed to create new task.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                Log.e("UpdateWorkPlan", "Network error: " + t.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TaskInfoActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private boolean isDateOnlyTask(Task task) {
        return dateOnlyActionIds.contains(Integer.parseInt(task.getReactionWorkActionID()));
    }

    private void fetchNewTaskDetails(int headerId, boolean addToCalendar, CRMWork action, String date, String time, String comment, boolean sendInvite) {
        ApiService apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        String apiKey = UserSessionManager.getApiKey(this);
        String userId = UserSessionManager.getUserId(this);

        Call<ApiResponseReactionPlan> call = apiService.getTasksForDate(userId, apiKey, "*", "lt", "select", "{\"reactionHeaderID\":\"" + headerId + "\"}", "", "");
        call.enqueue(new Callback<ApiResponseReactionPlan>() {
            @Override
            public void onResponse(Call<ApiResponseReactionPlan> call, Response<ApiResponseReactionPlan> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ApiResponseReactionPlan.Data data = response.body().getData();

                    if (data != null && data.getManagerReactionInPlanHeaderList() != null) {
                        List<ManagerReactionWorkInPlan.ManagerReactionInPlanHeader> headers = data.getManagerReactionInPlanHeaderList();

                        // Declare latestTask and maxWorkInPlanID outside of loops
                        ManagerReactionWorkInPlan.ManagerReactionWork latestTask = null;
                        int maxWorkInPlanID = 0;

                        for (ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header : headers) {
                            for (ManagerReactionWorkInPlan.ManagerReactionWork work : header.getManagerReactionWork()) {
                                if (work.getReactionWorkID() != null && work.getReactionWorkID() > maxWorkInPlanID) {
                                    maxWorkInPlanID = work.getReactionWorkID();
                                    latestTask = work;
                                }
                            }
                        }

                        boolean isDateOnlyAction = dateOnlyActionIds.contains(action.getCRMWorkID());

                        if (latestTask != null) {
                            // Convert or parse fields as needed
                            String headerIdString = String.valueOf(headerId);
                            Timestamp workInPlanTerm = parseTimestamp(latestTask.getReactionWorkTerm());
                            Timestamp workInPlanDoneDate = parseTimestamp(latestTask.getReactionWorkDoneDate());

                            Task newTask = new Task(
                                    headerIdString,
                                    reactionHeaderManagerId,
                                    String.valueOf(latestTask.getReactionWorkForCustomerID()),
                                    order,
                                    latestTask.getReactionWorkID(),
                                    String.valueOf(latestTask.getReactionWorkManagerID()),
                                    latestTask.getReactionWorkManageName(),
                                    String.valueOf(latestTask.getReactionWorkDoneByID()),
                                    latestTask.getReactionWorkDoneByName(),
                                    String.valueOf(latestTask.getReactionWorkActionID()),
                                    latestTask.getReactionWorkActionName(),
                                    latestTask.getReactionWorkNote(),
                                    latestTask.getReactionWorkForCustomerName(),
                                    workInPlanTerm,
                                    workInPlanDoneDate,
                                    latestTask.getReactionWorkDone(),
                                    isDateOnlyAction
                            );

                            int newTaskId = latestTask.getReactionWorkID();
                            Log.d("TaskInfoActivity", "Fetched latest task with ID: " + newTaskId);

                            if (addToCalendar) {
                                createGoogleCalendarEvent(action, date, time, comment, sendInvite, eventId -> {
                                    if (eventId != null) {
                                        saveGoogleCalendarEventId(TaskInfoActivity.this, newTaskId, eventId);
                                    }
                                    openNewTaskInfoActivity(newTask);
                                });
                            } else {
                                openNewTaskInfoActivity(newTask);
                            }
                        } else {
                            Toast.makeText(TaskInfoActivity.this, "No task details found under the specified header ID.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TaskInfoActivity.this, "No data found in response.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TaskInfoActivity.this, "Error retrieving task details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseReactionPlan> call, Throwable t) {
                Toast.makeText(TaskInfoActivity.this, "Network error while fetching task details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to parse date strings into Timestamps
    private Timestamp parseTimestamp(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            return Timestamp.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            Log.e("parseTimestamp", "Failed to parse timestamp: " + dateString, e);
            return null;
        }
    }

    // Helper method to safely parse integers
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.e("parseInt", "Failed to parse integer: " + value, e);
            return 0; // Default value, adjust if needed
        }
    }

    public interface EventCreationCallback {
        void onEventCreated(String eventId);
    }

    private void openNewTaskInfoActivity(Task newTask) {
        Intent intent = new Intent(TaskInfoActivity.this, TaskInfoActivity.class);
        intent.putExtra("Task", newTask);
        startActivity(intent);
    }

    private void createGoogleCalendarEvent(final CRMWork action, String date, String time, final String comment, final boolean sendInvite, final EventCreationCallback callback) {
        GoogleCalendarService googleCalendarService = new GoogleCalendarService(this);
        final com.google.api.services.calendar.Calendar calendarService = googleCalendarService.getCalendarService();

        if (calendarService == null) {
            Toast.makeText(this, "Google Calendar service is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Event event = new Event()
                .setSummary(action.getCRMWorkName())
                .setDescription(comment);

        String dateTimeString = date + " " + time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date startDate = sdf.parse(dateTimeString);
            DateTime startDateTime = new DateTime(startDate);

            event.setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone("UTC"));
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.HOUR, 1);
            DateTime endDateTime = new DateTime(cal.getTime());
            event.setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone("UTC"));

            new Thread(() -> {
                try {
                    Event createdEvent = calendarService.events().insert("primary", event).execute();
                    if (createdEvent != null && createdEvent.getId() != null) {
                        String eventId = createdEvent.getId();
                        runOnUiThread(() -> {
                            Toast.makeText(TaskInfoActivity.this, "Event added to Google Calendar.", Toast.LENGTH_SHORT).show();
                            if (callback != null) callback.onEventCreated(eventId);
                        });

                        if (sendInvite) {
                            sendEmailInvite(action, comment, startDate, cal.getTime());
                        }
                    }
                } catch (Exception e) {
                    Log.e("TaskInfoActivity", "Error creating event: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(TaskInfoActivity.this, "Failed to add event to Google Calendar.", Toast.LENGTH_SHORT).show());
                    if (callback != null) callback.onEventCreated(null); // Pass null if creation failed
                }
            }).start();
        } catch (Exception e) {
            Log.e("TaskInfoActivity", "Date parsing error: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to parse date and time.", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveGoogleCalendarEventId(Context context, int taskId, String eventId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TaskPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("googleCalendarEventId_" + taskId, eventId);
        Log.d("TaskInfoActivity", "Saving Google Calendar Event ID with workInPlanID: " + taskId + " and Event ID: " + eventId);

        editor.commit();
        Log.d("Debug", "Event ID saved: " + eventId + " for Task ID: " + taskId);
    }

    private String getGoogleCalendarEventId(Context context, int taskId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TaskPrefs", MODE_PRIVATE);
        String eventId = sharedPreferences.getString("googleCalendarEventId_" + taskId, null);
        Log.d("TaskInfoActivity", "Retrieving Google Calendar Event ID for workInPlanID: " + taskId + " Resulting Event ID: " + eventId);

        return eventId;
    }

    private void removeGoogleCalendarEventId(Context context, int taskId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TaskPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("googleCalendarEventId_" + taskId);
        editor.apply();
    }


    private void deleteGoogleCalendarEvent() {
        String eventId = getGoogleCalendarEventId(this, task.getWorkInPlanID());
        if (eventId == null) {
            Log.e("TaskInfoActivity", "No Event ID found for this task.");
            Toast.makeText(TaskInfoActivity.this, "Event not found in Google Calendar.", Toast.LENGTH_SHORT).show();
            return;
        }

        GoogleCalendarService googleCalendarService = new GoogleCalendarService(this);
        com.google.api.services.calendar.Calendar calendarService = googleCalendarService.getCalendarService();

        if (calendarService != null) {
            new Thread(() -> {
                try {
                    calendarService.events().delete("primary", eventId).execute();
                    // Remove event ID from SharedPreferences
                    removeGoogleCalendarEventId(this, task.getWorkInPlanID());

                    runOnUiThread(() -> Toast.makeText(TaskInfoActivity.this, "Event deleted from Google Calendar.", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    Log.e("TaskInfoActivity", "Failed to delete event: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(TaskInfoActivity.this, "Failed to delete event.", Toast.LENGTH_SHORT).show());
                }
            }).start();
        }
    }

    private void sendEmailInvite(final CRMWork action, final String comment, final Date startDate, final Date endDate) {
        if (repEmail != null && !repEmail.isEmpty() && !repEmail.equals("Unassigned")) {
            // Generate .ics file content
            String icsContent = generateICSFileContent(action.getCRMWorkName(), comment, startDate, endDate);

            // Write the content to a file
            try {
                File icsFile = new File(getFilesDir(), "invite.ics");
                try (FileOutputStream fos = new FileOutputStream(icsFile)) {
                    fos.write(icsContent.getBytes());
                }

                // Get the Uri using FileProvider
                Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", icsFile);

                // Localized text for email subject and body
                String emailSubject = getString(R.string.invitation_subject, action.getCRMWorkName());
                String emailBody = getString(R.string.invitation_body, action.getCRMWorkName(), comment);

                // Send email with .ics file attached
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("application/ics");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{repEmail});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
                emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

                // Grant temporary read permission to the content Uri
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_using)));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.invite_send_failure), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_rep_email_available), Toast.LENGTH_SHORT).show();
        }
    }


    private String generateICSFileContent(String summary, String description, Date startDate, Date endDate) {
        SimpleDateFormat icsDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault());
        icsDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\r\n");
        sb.append("VERSION:2.0\r\n");
        sb.append("PRODID:-//Your Company//Your Product//EN\r\n");
        sb.append("CALSCALE:GREGORIAN\r\n"); // Ensure compatibility
        sb.append("METHOD:REQUEST\r\n"); // This can indicate an invite
        sb.append("BEGIN:VEVENT\r\n");
        sb.append("UID:").append(UUID.randomUUID().toString()).append("\r\n");
        sb.append("DTSTAMP:").append(icsDateFormat.format(new Date())).append("\r\n");
        sb.append("DTSTART:").append(icsDateFormat.format(startDate)).append("\r\n");
        sb.append("DTEND:").append(icsDateFormat.format(endDate)).append("\r\n");
        sb.append("SUMMARY:").append(summary).append("\r\n");
        if (description != null && !description.isEmpty()) {
            sb.append("DESCRIPTION:").append(description).append("\r\n");
        }
        sb.append("LOCATION:Online\r\n"); // Add default location
        sb.append("STATUS:CONFIRMED\r\n"); // Set event status
        sb.append("SEQUENCE:0\r\n"); // Event update sequence
        sb.append("END:VEVENT\r\n");
        sb.append("END:VCALENDAR\r\n");
        return sb.toString();
    }


    private void deleteTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String apiKey = UserSessionManager.getApiKey(TaskInfoActivity.this);
                    String userId = UserSessionManager.getUserId(TaskInfoActivity.this);

                    if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
                        Toast.makeText(TaskInfoActivity.this, "Please login again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    ManagerReactionUpdateRequest taskUpdateRequest = new ManagerReactionUpdateRequest();
                    taskUpdateRequest.setUserId(userId);
                    taskUpdateRequest.setApiKey(apiKey);
                    taskUpdateRequest.setAction("update");
                    taskUpdateRequest.setLanguageCode("lt");

                    ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header = new ManagerReactionWorkInPlan.ManagerReactionInPlanHeader();
                    header.setReactionHeaderID(reactionHeaderId);
                    header.setReactionManagerID(reactionHeaderManagerId);
                    header.setReactionForCustomerID(taskCustomerId);

                    List<ManagerReactionWorkInPlan.ManagerReactionWork> works = new ArrayList<>();
                    ManagerReactionWorkInPlan.ManagerReactionWork work = new ManagerReactionWorkInPlan.ManagerReactionWork();
                    work.setReactionWorkID(String.valueOf(taskId));
                    works.add(work);

                    taskUpdateRequest.setManagerReactionInPlanHeaderReg(header);
                    taskUpdateRequest.setManagerReactionWorkReg(works);

                    updateWorkPlan(TaskInfoActivity.this, taskUpdateRequest, new Callback<ApiResponseUpdate>() {
                        @Override
                        public void onResponse(Call<ApiResponseUpdate> call, Response<ApiResponseUpdate> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(TaskInfoActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                Log.d("UpdateWorkPlan", "Task deleted successfully") ;
                                setResult(RESULT_OK);
                                deleteGoogleCalendarEvent();
                                finish();
                            } else {
                                Toast.makeText(TaskInfoActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                                Log.e("UpdateWorkPlan", "Failed to delete task: " + response.code());
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                            Log.e("UpdateWorkPlan", "Network error: " + t.getMessage());
                            Toast.makeText(TaskInfoActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private Map<String, CRMWork> crmWorkMap = new HashMap<>();

    private void fetchCRMWorkData(Runnable onComplete) {
        data.GetTasks.getActionsInfo(this, result -> {
            if (result != null && result.isSuccess()) {
                List<CRMWork> crmWorkList = result.getData().getCrmWorkList();
                if (crmWorkList != null) {
                    for (CRMWork work : crmWorkList) {
                        crmWorkMap.put(String.valueOf(work.getCRMWorkID()), work);

                        // Check if the CRMWork format matches "yyyy.mm.dd" and add to dateOnlyActionIds if true
                        if ("yyyy.mm.dd".equals(work.getCRMWorkFormat())) {
                            dateOnlyActionIds.add(work.getCRMWorkID());
                        }
                    }
                }
                Log.d("TaskInfoActivity", "Fetched CRMWork data and updated crmWorkMap and dateOnlyActionIds");
            } else {
                Log.e("TaskInfoActivity", "Failed to fetch CRMWork data");
            }

            // Invoke the onComplete callback
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    private CRMWork getCRMWorkById(String crmWorkId) {
        return crmWorkMap.get(crmWorkId);
    }

    private void showStatusConfirmationDialog(String title, boolean isChecked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.YES, (dialogInterface, i) -> {
            statusTextView.setText(isChecked ? "Completed" : "Not completed");
            statusCheckbox.setChecked(isChecked);
            handleStatusChange(isChecked);
        });
        builder.setNegativeButton(R.string.NO, (dialogInterface, i) -> {
            statusTextView.setText(isChecked ? "Not completed" : "Completed");
            statusCheckbox.setChecked(!isChecked);
        });
        builder.show();
    }

    private void handleStatusChange(boolean isChecked) {
        String workDone = isChecked ? "1" : "0";
        String workDoneDate = isChecked ? getCurrentTimestamp() : null;

        // Update task status in the backend
        updateTaskStatus(taskId, workDone, workDoneDate);

        if (isChecked) {
            // Task is marked as completed
            WorkManager.getInstance(this).cancelUniqueWork("task_notification_" + taskId);
            Log.d("TaskInfoActivity", "Canceled notification for task ID: " + taskId);

            // Delete Google Calendar event if exists
            deleteGoogleCalendarEvent();
            Log.d("TaskInfoActivity", "Deleted Google Calendar event for task ID: " + taskId);
        } else {
            // Task is marked as not completed
            if (crmWorkMap.isEmpty()) {
                fetchCRMWorkData(() -> {
                    scheduleTaskNotification(task);
                    Log.d("TaskInfoActivity", "Rescheduled notification for task ID: " + taskId);
                });
            } else {
                scheduleTaskNotification(task);
                Log.d("TaskInfoActivity", "Rescheduled notification for task ID: " + taskId);
            }

            // Check and update Google Calendar event if term has changed
            if (getGoogleCalendarEventId(this, task.getWorkInPlanID()) != null) {
                updateGoogleCalendarEvent(getGoogleCalendarEventId(this, task.getWorkInPlanID()), task.getWorkInPlanTerm());
                Log.d("TaskInfoActivity", "Updated Google Calendar event for task ID: " + taskId);
            }
        }

        // Update the task object's fields
        task.setWorkInPlanDone(workDone);
        task.setWorkInPlanDoneDate(workDoneDate);
    }

    private void updateGoogleCalendarEvent(String eventId, Timestamp newTerm) {
        new Thread(() -> {
            try {
                GoogleCalendarService googleCalendarService = new GoogleCalendarService(this);
                com.google.api.services.calendar.Calendar calendarService = googleCalendarService.getCalendarService();

                if (calendarService == null) {
                    Log.e("TaskInfoActivity", "Google Calendar service is not available.");
                    return;
                }

                // Parse new start and end times
                Date startDate = new Date(newTerm.getTime());
                DateTime startDateTime = new DateTime(startDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.add(Calendar.HOUR, 1); // Set end time 1 hour later
                DateTime endDateTime = new DateTime(cal.getTime());

                Log.d("TaskInfoActivity", "Attempting to fetch event with ID: " + eventId);

                // Fetch the event
                Event event = calendarService.events().get("primary", eventId).execute();
                if (event == null) {
                    Log.e("TaskInfoActivity", "Event not found: " + eventId);
                    return;
                }

                // Log before updating
                Log.d("TaskInfoActivity", "Event start time before update: " + event.getStart().getDateTime());
                Log.d("TaskInfoActivity", "Event end time before update: " + event.getEnd().getDateTime());

                // Update event with new start and end times
                event.setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone("UTC"));
                event.setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone("UTC"));

                // Execute update
                calendarService.events().update("primary", eventId, event).execute();
                Log.d("TaskInfoActivity", "Google Calendar event updated: " + eventId);

            } catch (Exception e) {
                Log.e("TaskInfoActivity", "Failed to update Google Calendar event: " + e.getMessage(), e);
            }
        }).start();
    }


    private void scheduleTaskNotification(Task task) {
    // Skip scheduling if the task is already completed
    if ("1".equals(task.getWorkInPlanDone())) {
        return;
    }

    // Get the CRMWorkRemindTime from the CRMWork associated with this task
    CRMWork crmWork = getCRMWorkById(task.getReactionWorkActionID());
    if (crmWork == null) {
        Log.d("TaskInfoActivity", "CRMWork not found for action ID: " + task.getReactionWorkActionID());
        return;
    }

    long remindTimeInMillis = crmWork.getRemindTimeInMillis();

    // Calculate the trigger time
    long taskTimeInMillis = task.getWorkInPlanTerm().getTime();
    long currentTimeInMillis = System.currentTimeMillis();
    long delay = taskTimeInMillis - remindTimeInMillis - currentTimeInMillis;

    if (delay <= 0) {
        // If the delay is negative or zero, skip scheduling
        Log.d("TaskInfoActivity", "Notification time has already passed for task ID: " + task.getWorkInPlanID());
        return;
    }

    // Prepare input data for the worker
    Data inputData = new Data.Builder()
            .putString(NotificationWorker.TASK_NAME_KEY, task.getWorkInPlanName())
            .putInt(NotificationWorker.TASK_ID_KEY, task.getWorkInPlanID())
            .build();

    // Create a OneTimeWorkRequest
    OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build();

    // Enqueue the work
    WorkManager.getInstance(this).enqueueUniqueWork(
            "task_notification_" + task.getWorkInPlanID(),
            ExistingWorkPolicy.REPLACE,
            notificationWork
    );

    Log.d("TaskInfoActivity", "Scheduled notification for task ID: " + task.getWorkInPlanID());
}



    private void updateTaskStatus(int taskId, String workDone, String workDoneDate) {
        String apiKey = UserSessionManager.getApiKey(this);
        String userId = UserSessionManager.getUserId(this);
        Log.d("TaskInfoActivity", "Updating task status with workInPlanID: " + taskId + " New Status: " + workDone);

        if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        ManagerReactionUpdateRequest taskUpdateRequest = new ManagerReactionUpdateRequest();
        taskUpdateRequest.setUserId(userId);
        taskUpdateRequest.setApiKey(apiKey);
        taskUpdateRequest.setAction("update");
        taskUpdateRequest.setLanguageCode("lt");

        ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header = new ManagerReactionWorkInPlan.ManagerReactionInPlanHeader();
        header.setReactionHeaderID(reactionHeaderId);
        header.setReactionManagerID(reactionHeaderManagerId);
        header.setReactionForCustomerID(taskCustomerId);

        List<ManagerReactionWorkInPlan.ManagerReactionWork> works = new ArrayList<>();
        ManagerReactionWorkInPlan.ManagerReactionWork work = new ManagerReactionWorkInPlan.ManagerReactionWork();
        work.setReactionWorkID(String.valueOf(taskId));
        work.setReactionWorkDone(workDone);
        work.setReactionWorkDoneDate(workDoneDate);

        works.add(work);
        taskUpdateRequest.setManagerReactionInPlanHeaderReg(header);
        taskUpdateRequest.setManagerReactionWorkReg(works);

        updateWorkPlan(this, taskUpdateRequest, new Callback<ApiResponseUpdate>() {
            @Override
            public void onResponse(Call<ApiResponseUpdate> call, Response<ApiResponseUpdate> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TaskInfoActivity.this, "Task status updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TaskInfoActivity.this, "Failed to update task status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                Toast.makeText(TaskInfoActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle("Edit?");
        builder.setPositiveButton(R.string.YES, (dialogInterface, i) -> enterEditMode());
        builder.setNegativeButton(R.string.NO, null);
        builder.show();
    }

    private void enterEditMode() {
        phoneTextView.setVisibility(View.GONE);
        editPhone.setText(phoneTextView.getText().toString());
        phoneLayout.setVisibility(View.VISIBLE);

        emailTextView.setVisibility(View.GONE);
        editEmail.setText(emailTextView.getText().toString());
        emailLayout.setVisibility(View.VISIBLE);

        websiteTextView.setVisibility(View.GONE);
        editWebsite.setText(websiteTextView.getText().toString());
        websiteLayout.setVisibility(View.VISIBLE);

        addressTextView.setVisibility(View.GONE);
        editAddress.setText(addressTextView.getText().toString());
        addressLayout.setVisibility(View.VISIBLE);

        commentTextView.setVisibility(View.GONE);
        editComment.setText(commentTextView.getText().toString());
        commentLayout.setVisibility(View.VISIBLE);

        editButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        dueDateTitleTextView.setOnClickListener(v -> showDateTimePicker());
        dueDateTextView.setOnClickListener(v -> showDateTimePicker());

        saveButton.setOnClickListener(v -> saveChanges());
        cancelButton.setOnClickListener(v -> exitEditMode());

        editButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        newTaskButton.setVisibility(View.GONE);
    }

    private void saveChanges() {
        // Retrieve and format edited fields
        String editedPhone = editPhone.getText().toString();
        String editedEmail = editEmail.getText().toString();
        String editedWebsite = editWebsite.getText().toString();
        String editedAddress = addressTextView.getText().toString();
        String editedComment = editComment.getText().toString();

        phoneTextView.setText(editedPhone);
        emailTextView.setText(editedEmail);
        websiteTextView.setText(editedWebsite);
        addressTextView.setText(editedAddress);
        commentTextView.setText(editedComment);

        exitEditMode();

        String apiKey = UserSessionManager.getApiKey(this);
        String userId = UserSessionManager.getUserId(this);
        if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
            Log.e("UpdateWorkPlan", "Missing API Key or User ID. Please login again.");
            Toast.makeText(this, "Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean taskChanged = false;
        boolean customerChanged = false;

        ManagerReactionUpdateRequest taskUpdateRequest = new ManagerReactionUpdateRequest();
        taskUpdateRequest.setUserId(userId);
        taskUpdateRequest.setApiKey(apiKey);
        taskUpdateRequest.setAction("update");
        taskUpdateRequest.setLanguageCode("lt");

        ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header = new ManagerReactionWorkInPlan.ManagerReactionInPlanHeader();
        header.setReactionHeaderID(reactionHeaderId);
        header.setReactionManagerID(reactionHeaderManagerId);
        header.setReactionForCustomerID(taskCustomerId);

        List<ManagerReactionWorkInPlan.ManagerReactionWork> works = new ArrayList<>();
        ManagerReactionWorkInPlan.ManagerReactionWork work = new ManagerReactionWorkInPlan.ManagerReactionWork();
        work.setReactionWorkID(String.valueOf(taskId));
        work.setReactionWorkManagerID(reactionWorkManagerId);
        work.setReactionWorkActionID(reactionWorkActionId);

        // Validate and format newDueDate
        String newDueDate = dueDateTextView.getText().toString();
        if (!newDueDate.equals(taskDate)) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                Date parsedDate = inputFormat.parse(newDueDate); // Parse with HH:mm format
                String formattedDate = outputFormat.format(parsedDate); // Reformat to HH:mm:ss

                Timestamp newTermTimestamp = Timestamp.valueOf(formattedDate); // Convert to Timestamp
                work.setReactionWorkTerm(formattedDate); // Set formatted date to work object
                taskChanged = true;

                // Update Google Calendar if an event exists
                String eventId = getGoogleCalendarEventId(this, task.getWorkInPlanID());
                Log.d("TaskInfoActivity", "Retrieved Event ID for updating: " + eventId);
                if (eventId != null) {
                    updateGoogleCalendarEvent(eventId, newTermTimestamp); // Update Google Calendar event
                }
            } catch (ParseException | IllegalArgumentException e) {
                Log.e("UpdateWorkPlan", "Date formatting error for newDueDate: " + newDueDate, e);
                Toast.makeText(this, "Invalid date format. Please enter date as yyyy-MM-dd HH:mm.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Handle comment change
        if (!editedComment.equals(taskComment)) {
            work.setReactionWorkNote(editedComment);
            taskChanged = true;
        }

        // Handle task status change
        boolean newStatus = statusCheckbox.isChecked();
        if (newStatus && !"Completed".equals(taskDoneDate)) {
            work.setReactionWorkDoneDate(getCurrentTimestamp());
            work.setReactionWorkDone("1");
            taskChanged = true;
        } else if (!newStatus && "Completed".equals(taskDoneDate)) {
            work.setReactionWorkDoneDate(null);
            work.setReactionWorkDone("0");
            taskChanged = true;
        }

        // Add work if task data changed
        if (taskChanged) {
            works.add(work);
            taskUpdateRequest.setManagerReactionInPlanHeaderReg(header);
            taskUpdateRequest.setManagerReactionWorkReg(works);
        }

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setUserId(userId);
        customerUpdateRequest.setApiKey(apiKey);
        customerUpdateRequest.setAction("update");
        customerUpdateRequest.setLanguageCode("lt");
        customerUpdateRequest.setCustomerId(taskCustomerId);

        customerUpdateRequest.setCustomerAdressCity(editedAddress);

        if (!editedPhone.equals(repPhone) || !editedEmail.equals(repEmail) || !editedAddress.equals(address)) {
            customerChanged = true;
            Customer.CustomerContactPerson selectedPerson = getSelectedContactPerson();
            selectedPerson.setContactPersonMobPhone(editedPhone);
            selectedPerson.setContactPersonMail(editedEmail);

            List<Customer.CustomerContactPerson> contactPersons = new ArrayList<>();
            contactPersons.add(selectedPerson);
            customerUpdateRequest.setCustomerContactPersons(contactPersons);
        }

        if (taskChanged) {
            updateWorkPlan(this, taskUpdateRequest, new Callback<ApiResponseUpdate>() {
                @Override
                public void onResponse(Call<ApiResponseUpdate> call, Response<ApiResponseUpdate> response) {
                    if (response.isSuccessful()) {
                        Log.d("UpdateWorkPlan", "Work plan update successful.");
                        String eventId = getGoogleCalendarEventId(TaskInfoActivity.this, task.getWorkInPlanID());
                        if (eventId != null) {
                            updateGoogleCalendarEvent(eventId, task.getWorkInPlanTerm());
                        }
                    } else {
                        Log.e("UpdateWorkPlan", "Work plan update failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                    Log.e("UpdateWorkPlan", "Network error while updating work plan: " + t.getMessage());
                }
            });
        }

        if (customerChanged) {
            updateCustomer(this, customerUpdateRequest, new Callback<ApiResponseUpdate>() {
                @Override
                public void onResponse(Call<ApiResponseUpdate> call, Response<ApiResponseUpdate> response) {
                    if (response.isSuccessful()) {
                        Log.d("UpdateCustomer", "Customer update successful.");
                    } else {
                        Log.e("UpdateCustomer", "Customer update failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                    Log.e("UpdateCustomer", "Network error while updating customer: " + t.getMessage());
                }
            });
        }
    }




    private Customer.CustomerContactPerson getSelectedContactPerson() {
        Spinner contactPersonSpinner = findViewById(R.id.contact_person_spinner);
        int selectedPosition = contactPersonSpinner.getSelectedItemPosition();
        List<Customer.CustomerContactPerson> contactPersons = getCustomerContactPersons();
        return contactPersons.get(selectedPosition);
    }

    private List<Customer.CustomerContactPerson> getCustomerContactPersons() {
        return new ArrayList<>();
    }

    private void updateCustomer(Context context, CustomerUpdateRequest request, Callback<ApiResponseUpdate> callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gamyba.online/api-aiva/v1/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ApiResponseUpdate> call = apiService.updateCustomer(
                request.getUserId(),
                request.getApiKey(),
                request.getAction(),
                request.getLanguageCode(),
                request.getCustomer()
        );
        call.enqueue(callback);
    }

    private void exitEditMode() {
        phoneTextView.setVisibility(View.VISIBLE);
        phoneLayout.setVisibility(View.GONE);

        emailTextView.setVisibility(View.VISIBLE);
        emailLayout.setVisibility(View.GONE);

        websiteTextView.setVisibility(View.VISIBLE);
        websiteLayout.setVisibility(View.GONE);

        addressTextView.setVisibility(View.VISIBLE);
        addressLayout.setVisibility(View.GONE);

        commentTextView.setVisibility(View.VISIBLE);
        commentLayout.setVisibility(View.GONE);

        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

        editButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        newTaskButton.setVisibility(View.VISIBLE);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void extractTaskData(Task task) {
        taskCustomerId = task.getWorkInPlanForCustomerID();
        taskId = task.getWorkInPlanID();
        Log.d("TaskInfoActivity", "Extracting data for task with workInPlanID: " + task.getWorkInPlanID());

        taskComment = task.getWorkInPlanNote();
        taskCustomer = task.getWorkInPlanForCustomerName();
        taskDate = task.getWorkInPlanTerm().toString();
        taskDoneDate = String.valueOf(task.getWorkInPlanDoneDate());
        taskDone = task.getWorkInPlanDone();
        taskName = task.getWorkInPlanName();
        order = task.getWorkInPlanForCustomerOrder();

        reactionHeaderId = task.getReactionHeaderID();
        reactionHeaderManagerId = task.getReactionHeaderManagerID();
        reactionWorkManagerId = task.getReactionWorkManagerID();
        managerName = task.getManagerName();
        reactionWorkActionId = task.getReactionWorkActionID();
    }

    public void setTaskInfo(Task task) {
        clientNameTextView.setText(task.getWorkInPlanForCustomerName());
        actionTextView.setText(task.getWorkInPlanName());
        dueDateTextView.setText(task.getWorkInPlanTerm().toString());
        commentTextView.setText(task.getWorkInPlanNote());
        phoneTextView.setText(repPhone);
        emailTextView.setText(repEmail);
        orderTextView.setText(order);
        addressTextView.setText(address);
        websiteTextView.setText(website);

        boolean isCompleted = "1".equals(task.getWorkInPlanDone()) && task.getWorkInPlanDoneDate() != null;
        statusCheckbox.setChecked(isCompleted);
        statusTextView.setText(isCompleted ? "Completed" : "Not completed");



        // Use the passed fields directly to display the assignee's name
        String assignedEmployeeName;
                if (task.getReactionWorkDoneByID()==null || task.getReactionWorkDoneByID().equals("")){ assignedEmployeeName = task.getManagerName();}
                else assignedEmployeeName = task.getReactionWorkDoneByName();


        taskAssignee.setText("Assigned to: " + assignedEmployeeName);
    }
    private String getEmployeeNameById(String employeeId) {
        if (employeeList != null) {
            for (Employe employee : employeeList) {
                if (String.valueOf(employee.getEmployeID()).equals(employeeId)) {
                    return employee.getEmployeName() + " " + employee.getEmploeerSurname();
                }
            }
        }
        // Fallback to reactionWorkDoneByName if available
        if (task != null && task.getReactionWorkDoneByName() != null) {
            return task.getReactionWorkDoneByName();
        }
        return "Unknown";
    }

    public void getCustomerCredentials(Task task) {
        getCustomer(this, taskCustomerId, new OnCustomerRetrieved() {
            @Override
            public void getResult(ApiResponseGetCustomer result) {
                if (result != null && result.isSuccess() && result.getData() != null) {
                    List<Customer> customers = result.getData().getCustomers();
                    if (customers != null && !customers.isEmpty()) {
                        for (Customer customer : customers) {
                            if (customer.getCustomerID().equals(taskCustomerId)) {
                                website = customer.getCustomerWWW();
                                address = customer.getCustomerAdressCity() + " " + customer.getCustomerAdressStreet() + " " +
                                        customer.getCustomerAdressHouse() + " " + customer.getCustomerAdressPostIndex() + " " +
                                        customer.getCustomerCountryCode();

                                List<Customer.CustomerContactPerson> contactPersons = customer.getCustomerContactPersons();
                                if (contactPersons != null && !contactPersons.isEmpty()) {
                                    boolean foundRepresentative = false;
                                    for (Customer.CustomerContactPerson contactPerson : contactPersons) {
                                        if (contactPerson.isRepresentative()) {
                                            repName = contactPerson.getContactPersonName();
                                            repSurname = contactPerson.getContactPersonSurname();
                                            repEmail = contactPerson.getContactPersonMail();
                                            repPhone = contactPerson.getContactPersonMobPhone();
                                            if (repPhone == null || repPhone.isEmpty()) {
                                                repPhone = contactPerson.getContactPersonPhone();
                                            }
                                            foundRepresentative = true;
                                            break;
                                        }
                                    }
                                    if (!foundRepresentative) {
                                        Customer.CustomerContactPerson firstContactPerson = contactPersons.get(0);
                                        repName = firstContactPerson.getContactPersonName();
                                        repSurname = firstContactPerson.getContactPersonSurname();
                                        repEmail = firstContactPerson.getContactPersonMail();
                                        repPhone = firstContactPerson.getContactPersonMobPhone();
                                        if (repPhone == null || repPhone.isEmpty()) {
                                            repPhone = firstContactPerson.getContactPersonPhone();
                                        }
                                    }
                                    initializeSpinner(contactPersons);
                                } else {
                                    repName = "";
                                    repSurname = "";
                                    repEmail = customer.getCustomerContactMail();
                                    repPhone = customer.getCustomerContactPhone();
                                }
                            }
                        }
                    }
                    if (repPhone == null || repPhone.isEmpty()) {
                        repPhone = "Unassigned";
                    }
                    if (repEmail == null || repEmail.isEmpty()) {
                        repEmail = "Unassigned";
                    }
                    setTaskInfo(task);
                } else {
                    Log.e("TaskInfo", "Result is null, not successful, or contains no data.");
                }
            }
        });
    }

    private void initializeSpinner(List<Customer.CustomerContactPerson> contactPersons) {
        Spinner contactPersonSpinner = findViewById(R.id.contact_person_spinner);

        List<String> contactPersonNames = new ArrayList<>();
        for (Customer.CustomerContactPerson contactPerson : contactPersons) {
            String nameWithType = contactPerson.getContactPersonName() + " " + contactPerson.getContactPersonSurname() +
                    " (" + getContactPersonTypeShortForm(contactPerson.getContactPersonType()) + ")";
            contactPersonNames.add(nameWithType);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, contactPersonNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactPersonSpinner.setAdapter(adapter);

        for (int i = 0; i < contactPersons.size(); i++) {
            if (contactPersons.get(i).isRepresentative()) {
                contactPersonSpinner.setSelection(i);
                break;
            }
        }

        contactPersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Customer.CustomerContactPerson selectedPerson = contactPersons.get(position);
                repName = selectedPerson.getContactPersonName();
                repSurname = selectedPerson.getContactPersonSurname();
                repEmail = selectedPerson.getContactPersonMail();
                repPhone = selectedPerson.getContactPersonMobPhone();
                if (repPhone == null || repPhone.isEmpty()) {
                    repPhone = selectedPerson.getContactPersonPhone();
                }
                updateContactInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private String getContactPersonTypeShortForm(String contactPersonType) {
        switch (contactPersonType) {
            case "1000": return "M";
            case "0100": return "CEO";
            case "0010": return "<A>";
            case "0001": return "P";
            case "1100": return "M/CEO";
            case "1010": return "<A>/M";
            case "1001": return "M/P";
            case "0110": return "<A>/CEO";
            case "0101": return "CEO/P";
            case "0011": return "<A>/P";
            case "1110": return "<A>/M/CEO";
            case "1101": return "M/CEO/P";
            case "1011": return "<A>/M/P";
            case "0111": return "<A>/CEO/P";
            case "1111": return "All";
            default: return "Unknown";
        }
    }

    private void updateContactInfo() {
        phoneTextView.setText(repPhone);
        emailTextView.setText(repEmail);
    }

    private void showNewTaskConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle("Create new task?");
        builder.setMessage("Do you want to create the same task for the selected client?");
        builder.setPositiveButton(R.string.YES, (dialogInterface, i) -> showTaskTypeSelectionDialog());
        builder.setNegativeButton(R.string.NO, null);
        builder.show();
    }

    private void showTaskTypeSelectionDialog() {
        // List of task types
        String[] taskTypes = {"Task Type 1", "Task Type 2", "Task Type 3"};
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle("Select Task Type");
        builder.setItems(taskTypes, (dialogInterface, i) -> {
            String selectedTaskType = taskTypes[i];
            showDateTimeSelectionDialog(selectedTaskType);
        });
        builder.show();
    }

    private void showDateTimeSelectionDialog(String taskType) {
        // Check if the task type requires date or date+time
        boolean isDateOnly = taskType.equals("Task Type 1"); // Example condition, adjust accordingly
        if (isDateOnly) {
            showDatePickerDialog();
        } else {
            showDateTimePickerDialog();
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfoActivity.this, (view, year1, monthOfYear, dayOfMonth) -> {
            String dateString = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            //showCommentDialog(dateString, null);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showDateTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfoActivity.this, (view, year1, monthOfYear, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(TaskInfoActivity.this, (view1, hourOfDay, minute1) -> {
                String dateString = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute1;
                //showCommentDialog(dateString, null);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showCommentDialog(CRMWork action, String date, String time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle("Add Comment");

        final EditText input = new EditText(TaskInfoActivity.this);
        input.setHint("Optional Comment");
        builder.setView(input);

        builder.setPositiveButton(R.string.YES, (dialog, which) -> {
            String comment = input.getText().toString();
            //createNewTask(action, date, time, comment);
        });
        builder.setNegativeButton(R.string.NO, (dialog, which) -> dialog.cancel());
        builder.show();
    }


    public interface OnCustomerRetrieved {
        void getResult(ApiResponseGetCustomer result);
    }

    public static void updateWorkPlan(Context context, ManagerReactionUpdateRequest request, Callback<ApiResponseUpdate> callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gamyba.online/api-aiva/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ApiResponseUpdate> call = apiService.updateManagerReaction(
                request.getUserId(),
                request.getApiKey(),
                request.getAction(),
                request.getLanguageCode(),
                request.getManagerReactionInPlanHeaderReg(),
                request.getManagerReactionWorkReg()
        );
        call.enqueue(new Callback<ApiResponseUpdate>() {
            @Override
            public void onResponse(Call<ApiResponseUpdate> call, Response<ApiResponseUpdate> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(call, response);
                } else {
                    Log.e("UpdateWorkPlan", "Update failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                Log.e("UpdateWorkPlan", "Network error: " + t.getMessage());
            }
        });
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfoActivity.this, (view, year1, monthOfYear, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(TaskInfoActivity.this, (view1, hourOfDay, minute1) -> {
                String dateString = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute1;
                dueDateTextView.setText(dateString);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);
        datePickerDialog.show();
    }
}
