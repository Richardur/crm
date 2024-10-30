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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

    private List<Customer.CustomerContactPerson> contactPersons;
    private int selectedRepIndex = 0;

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
        // Remove this block:
    /*
    if (getIntent().getBooleanExtra("SendInvite", false)) {
        String actionName = getIntent().getStringExtra("ActionName");
        String comment = getIntent().getStringExtra("Comment");
        long startDateMillis = getIntent().getLongExtra("StartDate", -1);
        long endDateMillis = getIntent().getLongExtra("EndDate", -1);
        if (startDateMillis != -1 && endDateMillis != -1) {
            Date startDate = new Date(startDateMillis);
            Date endDate = new Date(endDateMillis);
            sendEmailInvite(actionName, comment, startDate, endDate);
        }
    }
    */
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
            if (!repPhone.equals(getString(R.string.unassigned))) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneTextView.getText().toString()));
                startActivity(intent);
            } else {
                Toast.makeText(TaskInfoActivity.this, R.string.phone_number_not_assigned, Toast.LENGTH_SHORT).show();
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
            startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
        });

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=" + addressTextView.getText().toString()));
            startActivity(intent);
        });

        statusCheckbox.setOnClickListener(v -> {
            if (statusCheckbox.isChecked()) {
                showStatusConfirmationDialog(getString(R.string.complete_task_confirmation), true);
            } else {
                showStatusConfirmationDialog(getString(R.string.mark_task_not_completed_confirmation), false);
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
        builder.setTitle(R.string.reassign_task);
        builder.setMessage(R.string.reassign_confirmation_message);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            // Fetch the employee list and show the pop-up
            getEmployeeListAndShowPopup();
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();
    }

    private void getEmployeeListAndShowPopup() {
        // Show a progress dialog or loader if desired
        String apiKey = UserSessionManager.getApiKey(this);
        String userId = UserSessionManager.getUserId(this);

        if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_login_again), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(TaskInfoActivity.this, getString(R.string.failed_to_get_employees), Toast.LENGTH_SHORT).show();
// ...
                }
            }

            @Override
            public void onFailure(Call<EmployeResponse> call, Throwable t) {
                Toast.makeText(TaskInfoActivity.this, getString(R.string.network_error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmployeeSelectionPopup(List<Employe> employees) {
        List<String> employeeNames = new ArrayList<>();
        for (Employe employee : employees) {
            employeeNames.add(employee.getEmployeName() + " " + employee.getEmploeerSurname());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle(R.string.select_employee);
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
            Toast.makeText(TaskInfoActivity.this, getString(R.string.please_login_again), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(TaskInfoActivity.this, getString(R.string.task_reassigned_successfully), Toast.LENGTH_SHORT).show();
                    // Update the task object with new assignee details
                    task.setReactionWorkDoneByID(newAssigneeId);
                    task.setReactionWorkDoneByName(selectedEmployee.getEmployeName() + " " + selectedEmployee.getEmploeerSurname());

                    // Update the UI
                    taskAssignee.setText("Assigned to: " + task.getReactionWorkDoneByName());
                } else {
                    Toast.makeText(TaskInfoActivity.this, getString(R.string.failed_to_reassign_task), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                Toast.makeText(TaskInfoActivity.this, getString(R.string.network_error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNewTaskDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        NewTaskDialogFragment newTaskDialogFragment = NewTaskDialogFragment.newInstance(this, contactPersons, selectedRepIndex);
        newTaskDialogFragment.show(fragmentManager, "NewTaskDialogFragment");
    }

    @Override
    public void onNewTaskCreated(CRMWork action, String date, String time, String comment, boolean addToCalendar, boolean sendInvite, Customer.CustomerContactPerson selectedRep) {
        createNewTask(action, date, time, comment, addToCalendar, sendInvite, selectedRep);
    }

    private void createNewTask(CRMWork action, String date, String time, String comment, boolean addToCalendar, boolean sendInvite, Customer.CustomerContactPerson selectedRep) {
        String apiKey = UserSessionManager.getApiKey(this);
        String userId = UserSessionManager.getUserId(this);

        String currentEmployeeId = UserSessionManager.getEmployeeId(this);
        String currentEmployeeName = UserSessionManager.getEmployeeName(this);
        Log.d("TaskInfoActivity", "Creating new task with workInPlanID: " + task.getWorkInPlanID());

        if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_login_again), Toast.LENGTH_LONG).show();
            return;
        }

        // Set representative email and phone using selected representative details
        repEmail = selectedRep.getContactPersonMail();
        repPhone = selectedRep.getContactPersonMobPhone();
        if (repPhone == null || repPhone.isEmpty()) {
            repPhone = selectedRep.getContactPersonPhone();
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

                } else {
                    Log.e("UpdateWorkPlan", "New task creation failed: " + response.code());
                    runOnUiThread(() ->
                            Toast.makeText(TaskInfoActivity.this, getString(R.string.failed_to_create_new_task), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                Log.e("UpdateWorkPlan", "Network error: " + t.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(TaskInfoActivity.this, getString(R.string.network_error) + t.getMessage(), Toast.LENGTH_SHORT).show());
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
                                createGoogleCalendarEvent(action, date, time, comment, sendInvite, (eventId, startDate, endDate) -> {
                                    if (eventId != null) {
                                        saveGoogleCalendarEventId(TaskInfoActivity.this, newTaskId, eventId);
                                    }
                                    openNewTaskInfoActivity(newTask, sendInvite, action.getCRMWorkName(), comment, startDate, endDate);
                                });
                            } else {
                                openNewTaskInfoActivity(newTask, false, null, null, null, null);
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
        void onEventCreated(String eventId, Date startDate, Date endDate);
    }


    private void openNewTaskInfoActivity(Task newTask, boolean sendInvite, String actionName, String comment, Date startDate, Date endDate) {
        Intent intent = new Intent(TaskInfoActivity.this, TaskInfoActivity.class);
        intent.putExtra("Task", newTask);
        intent.putExtra("SendInvite", sendInvite);
        intent.putExtra("ActionName", actionName);
        intent.putExtra("Comment", comment);
        if (startDate != null) {
            intent.putExtra("StartDate", startDate.getTime());
        }
        if (endDate != null) {
            intent.putExtra("EndDate", endDate.getTime());
        }
        startActivity(intent);
        finish();
    }


    private void createGoogleCalendarEvent(final CRMWork action, String date, String time, final String comment, final boolean sendInvite, final EventCreationCallback callback) {
        GoogleCalendarService googleCalendarService = new GoogleCalendarService(this);
        final com.google.api.services.calendar.Calendar calendarService = googleCalendarService.getCalendarService();

        if (calendarService == null) {
            Toast.makeText(this, getString(R.string.google_calendar_not_available), Toast.LENGTH_SHORT).show();
            return;
        }

        final Event event = new Event()
                .setSummary(action.getCRMWorkName())
                .setDescription(comment);

        try {
            if (time == null || time.trim().isEmpty()) {
                // Handle date-only event (All-day event)
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date startDate = inputDateFormat.parse(date);
                if (startDate == null) {
                    throw new ParseException("Parsed startDate is null", 0);
                }

                // Set the end date as the day after the start date
                Date endDate = addDays(startDate, 1);

                // Use DateTime with dateOnly = true and tzShift = null for all-day events
                EventDateTime start = new EventDateTime()
                        .setDate(new com.google.api.client.util.DateTime(true, startDate.getTime(), null));
                EventDateTime end = new EventDateTime()
                        .setDate(new com.google.api.client.util.DateTime(true, endDate.getTime(), null));

                event.setStart(start);
                event.setEnd(end);

            } else {
                // Handle date-time event
                String dateTimeString = date + "T" + time + ":00";
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                dateTimeFormat.setTimeZone(TimeZone.getDefault());
                Date startDate = dateTimeFormat.parse(dateTimeString);
                if (startDate == null) {
                    throw new ParseException("Parsed startDate is null", 0);
                }

                // Set DateTime with the correct timezone for start and end time
                DateTime startDateTime = new DateTime(startDate);
                event.setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone(TimeZone.getDefault().getID()));

                // Set end time to one hour later
                Date endDate = addHours(startDate, 1);
                DateTime endDateTime = new DateTime(endDate);
                event.setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone(TimeZone.getDefault().getID()));
            }

            // Log the event details for debugging
            Log.d("TaskInfoActivity", "Event to be created: " + event.toPrettyString());

            new Thread(() -> {
                try {
                    Event createdEvent = calendarService.events().insert("primary", event).execute();
                    if (createdEvent != null && createdEvent.getId() != null) {
                        String eventId = createdEvent.getId();
                        runOnUiThread(() -> {
                            Toast.makeText(TaskInfoActivity.this, getString(R.string.event_added_to_google_calendar), Toast.LENGTH_SHORT).show();
                            if (callback != null) callback.onEventCreated(eventId, null, null);
                        });

                        if (sendInvite) {
                            Date actualStartDate = event.getStart().getDateTime() != null
                                    ? new Date(event.getStart().getDateTime().getValue())
                                    : new Date(event.getStart().getDate().getValue());
                            Date actualEndDate = event.getEnd().getDateTime() != null
                                    ? new Date(event.getEnd().getDateTime().getValue())
                                    : new Date(event.getEnd().getDate().getValue());

                            runOnUiThread(() -> sendEmailInvite(
                                    action.getCRMWorkName(),
                                    comment,
                                    actualStartDate,
                                    actualEndDate
                            ));
                        }
                    }
                } catch (Exception e) {
                    Log.e("TaskInfoActivity", "Error creating event: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(TaskInfoActivity.this, getString(R.string.failed_to_add_event_to_google_calendar), Toast.LENGTH_SHORT).show());
                    if (callback != null) callback.onEventCreated(null, null, null);
                }
            }).start();
        } catch (ParseException | IOException e) {
            Log.e("TaskInfoActivity", "Date parsing error: " + e.getMessage(), e);
            Toast.makeText(this, getString(R.string.failed_to_parse_date_and_time), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to add days to a date
    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    // Helper method to add hours to a date
    private Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
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
            Toast.makeText(TaskInfoActivity.this, getString(R.string.event_not_found), Toast.LENGTH_SHORT).show();
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

                    runOnUiThread(() -> Toast.makeText(TaskInfoActivity.this, getString(R.string.event_deleted), Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    Log.e("TaskInfoActivity", "Failed to delete event: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(TaskInfoActivity.this, getString(R.string.event_delete_failed), Toast.LENGTH_SHORT).show());
                }
            }).start();
        }
    }

    private void sendEmailInvite(final String actionName, final String comment, final Date startDate, final Date endDate) {
        if (repEmail != null && !repEmail.isEmpty() && !repEmail.equals(getString(R.string.unassigned))) {
            // Fetch organizer's name and email
            String organizerName = UserSessionManager.getEmployeeName(this); // Assuming this returns "Name Surname"
            String organizerEmail = getEmployeeEmail(); // Implement this method

            // Fallback to Google account's email if organizerEmail is not available
            if (organizerEmail == null || organizerEmail.isEmpty()) {
                organizerEmail = getGoogleAccountEmail(); // Implement this method
            }

            // Generate .ics file content with organizer details
            String icsContent = generateICSFileContent(actionName, comment, startDate, endDate, organizerName, organizerEmail);

            // Write the content to a file
            try {
                File icsFile = new File(getFilesDir(), "invite.ics");
                try (FileOutputStream fos = new FileOutputStream(icsFile)) {
                    fos.write(icsContent.getBytes());
                }

                // Get the Uri using FileProvider
                Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", icsFile);

                // Localized text for email subject and body
                String emailSubject = getString(R.string.invitation_subject, actionName);
                String emailBody = getString(R.string.invitation_body, actionName, comment);

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
    // Method to fetch employee's email from UserSessionManager
    private String getEmployeeEmail() {
        // Assuming you have stored the employee's email in UserSessionManager
        // Modify this according to your actual implementation
        return UserSessionManager.getEmployeeMail(this);
    }

    // Method to get the signed-in Google account's email
    private String getGoogleAccountEmail() {
        // Implement Google Sign-In to retrieve the user's email
        // Ensure you've set up Google Sign-In in your project

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            return account.getEmail();
        }
        return null;
    }



    private String generateICSFileContent(String summary, String description, Date startDate, Date endDate, String organizerName, String organizerEmail) {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\r\n");
        sb.append("VERSION:2.0\r\n");
        sb.append("PRODID:-//Your Company//Your Product//EN\r\n");
        sb.append("CALSCALE:GREGORIAN\r\n"); // Ensure compatibility
        sb.append("METHOD:REQUEST\r\n"); // Indicates an invite
        sb.append("BEGIN:VEVENT\r\n");
        sb.append("UID:").append(UUID.randomUUID().toString()).append("\r\n");
        sb.append("DTSTAMP:").append(getICSTimestamp(new Date(), false)).append("\r\n");

        if (startDate != null && endDate != null) {
            // Check if the event is all-day
            boolean isAllDay = (startDate.getHours() == 0 && startDate.getMinutes() == 0 && startDate.getSeconds() == 0) &&
                    (endDate.getHours() == 0 && endDate.getMinutes() == 0 && endDate.getSeconds() == 0);

            if (isAllDay) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                String start = sdf.format(startDate);
                String end = sdf.format(endDate); // End date should be the day after the event

                sb.append("DTSTART;VALUE=DATE:").append(start).append("\r\n");
                sb.append("DTEND;VALUE=DATE:").append(end).append("\r\n");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String start = sdf.format(startDate);
                String end = sdf.format(endDate);

                sb.append("DTSTART:").append(start).append("\r\n");
                sb.append("DTEND:").append(end).append("\r\n");
            }
        }

        sb.append("SUMMARY:").append(escapeString(summary)).append("\r\n");
        if (description != null && !description.isEmpty()) {
            sb.append("DESCRIPTION:").append(escapeString(description)).append("\r\n");
        }
        sb.append("LOCATION:Online\r\n"); // Default location
        sb.append("STATUS:CONFIRMED\r\n"); // Event status
        sb.append("ORGANIZER;CN=").append(escapeString(organizerName)).append(":MAILTO:").append(organizerEmail).append("\r\n"); // Organizer
        sb.append("SEQUENCE:0\r\n"); // Event update sequence
        sb.append("END:VEVENT\r\n");
        sb.append("END:VCALENDAR\r\n");
        return sb.toString();
    }
    private String getICSTimestamp(Date date, boolean isAllDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    // Helper method to escape special characters in ICS files
    private String escapeString(String input) {
        return input.replace("\\", "\\\\").replace(",", "\\,").replace(";", "\\;").replace("\n", "\\n");
    }


    private void deleteTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle(R.string.delete_task)
                .setMessage(R.string.delete_task_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    String apiKey = UserSessionManager.getApiKey(TaskInfoActivity.this);
                    String userId = UserSessionManager.getUserId(TaskInfoActivity.this);

                    if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
                        Toast.makeText(TaskInfoActivity.this, getString(R.string.please_login_again), Toast.LENGTH_LONG).show();
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
                                Toast.makeText(TaskInfoActivity.this, getString(R.string.task_deleted_successfully), Toast.LENGTH_SHORT).show();
                                Log.d("UpdateWorkPlan", "Task deleted successfully") ;
                                setResult(RESULT_OK);
                                deleteGoogleCalendarEvent();
                                finish();
                            } else {
                                Toast.makeText(TaskInfoActivity.this, getString(R.string.failed_to_delete_task), Toast.LENGTH_SHORT).show();
                                Log.e("UpdateWorkPlan", "Failed to delete task: " + response.code());
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                            Log.e("UpdateWorkPlan", "Network error: " + t.getMessage());
                            Toast.makeText(TaskInfoActivity.this, getString(R.string.network_error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    finish();
                })
                .setNegativeButton(R.string.no, null)
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
            statusTextView.setText(isChecked ? getString(R.string.completed) : getString(R.string.not_completed));
            statusCheckbox.setChecked(isChecked);
            handleStatusChange(isChecked);
        });
        builder.setNegativeButton(R.string.NO, (dialogInterface, i) -> {
            statusTextView.setText(isChecked ? getString(R.string.not_completed) : getString(R.string.completed));
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
            Toast.makeText(this, getString(R.string.please_login_again), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(TaskInfoActivity.this, getString(R.string.task_status_updated), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TaskInfoActivity.this, getString(R.string.failed_to_update_task_status), Toast.LENGTH_SHORT).show();
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
        builder.setTitle(R.string.edit_confirmation_title);
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
            Toast.makeText(this, getString(R.string.please_login_again), Toast.LENGTH_LONG).show();
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

        // Format the due date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(task.getWorkInPlanTerm());
        dueDateTextView.setText(formattedDate);

        commentTextView.setText(task.getWorkInPlanNote());
        phoneTextView.setText(repPhone);
        emailTextView.setText(repEmail);
        orderTextView.setText(order);
        addressTextView.setText(address);
        websiteTextView.setText(website);

        boolean isCompleted = "1".equals(task.getWorkInPlanDone()) && task.getWorkInPlanDoneDate() != null;
        statusCheckbox.setChecked(isCompleted);
        statusTextView.setText(isCompleted ? getString(R.string.completed) : getString(R.string.not_completed));

        String assignedEmployeeName;
        if (task.getReactionWorkDoneByID() == null || task.getReactionWorkDoneByID().isEmpty()) {
            assignedEmployeeName = task.getManagerName();
        } else {
            assignedEmployeeName = task.getReactionWorkDoneByName();
        }

        taskAssignee.setText(getString(R.string.assigned_to, assignedEmployeeName));
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

                                // Fetch contact persons and set them to the member variable
                                contactPersons = customer.getCustomerContactPersons();
                                selectedRepIndex = -1; // Default in case no representative is found

                                if (contactPersons != null && !contactPersons.isEmpty()) {
                                    // Search for a representative among the contact persons
                                    for (int i = 0; i < contactPersons.size(); i++) {
                                        Customer.CustomerContactPerson contactPerson = contactPersons.get(i);
                                        if (contactPerson.isRepresentative()) {
                                            selectedRepIndex = i;
                                            setRepresentativeInfo(contactPerson);
                                            break;
                                        }
                                    }

                                    // If no representative is found, use the first contact person
                                    if (selectedRepIndex == -1) {
                                        Customer.CustomerContactPerson firstContactPerson = contactPersons.get(0);
                                        setRepresentativeInfo(firstContactPerson);
                                        selectedRepIndex = 0;
                                    }

                                    // Initialize spinner with the contact persons list
                                    initializeSpinner(contactPersons);
                                } else {
                                    // No contact persons, fall back to general customer info
                                    repName = "";
                                    repSurname = "";
                                    repEmail = customer.getCustomerContactMail();
                                    repPhone = customer.getCustomerContactPhone();
                                }
                            }
                        }
                    }

                    // Set defaults if no phone/email provided
                    if (repPhone == null || repPhone.isEmpty()) {
                        repPhone = getString(R.string.unassigned);
                    }
                    if (repEmail == null || repEmail.isEmpty()) {
                        repEmail = getString(R.string.unassigned);
                    }

                    // Update UI with task information
                    setTaskInfo(task);

                    // Handle invite if necessary
                    handleSendInviteIfRequired();
                } else {
                    Log.e("TaskInfo", "Result is null, not successful, or contains no data.");
                }
            }
        });
    }

    // Helper method to set representative info
    private void setRepresentativeInfo(Customer.CustomerContactPerson contactPerson) {
        repName = contactPerson.getContactPersonName();
        repSurname = contactPerson.getContactPersonSurname();
        repEmail = contactPerson.getContactPersonMail();
        repPhone = contactPerson.getContactPersonMobPhone();
        if (repPhone == null || repPhone.isEmpty()) {
            repPhone = contactPerson.getContactPersonPhone();
        }
    }

    // Separate method to handle sending an invite if requested
    private void handleSendInviteIfRequired() {
        boolean sendInvite = getIntent().getBooleanExtra("SendInvite", false);
        if (sendInvite) {
            String actionName = getIntent().getStringExtra("ActionName");
            String comment = getIntent().getStringExtra("Comment");
            long startDateMillis = getIntent().getLongExtra("StartDate", -1);
            long endDateMillis = getIntent().getLongExtra("EndDate", -1);
            if (startDateMillis != -1 && endDateMillis != -1) {
                Date startDate = new Date(startDateMillis);
                Date endDate = new Date(endDateMillis);
                sendEmailInvite(actionName, comment, startDate, endDate);
            }
        }
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
        contactPersonSpinner.setSelection(selectedRepIndex);

        contactPersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRepIndex = position;
                Customer.CustomerContactPerson selectedPerson = contactPersons.get(position);
                setRepresentativeInfo(selectedPerson);
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
        builder.setTitle(R.string.create_new_task);
        builder.setMessage(R.string.create_same_task_confirmation);
        builder.setPositiveButton(R.string.YES, (dialogInterface, i) -> showTaskTypeSelectionDialog());
        builder.setNegativeButton(R.string.NO, null);
        builder.show();
    }

    private void showTaskTypeSelectionDialog() {
        // List of task types
        String[] taskTypes = {
                getString(R.string.task_type_1),
                getString(R.string.task_type_2),
                getString(R.string.task_type_3)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle(R.string.select_task_type);
        builder.setItems(taskTypes, (dialogInterface, i) -> {
            String selectedTaskType = taskTypes[i];
            showDateTimeSelectionDialog(selectedTaskType);
        });
        builder.show();
    }

    private void showDateTimeSelectionDialog(String taskType) {
        // Check if the task type requires date or date+time
        boolean isDateOnly = taskType.equals(getString(R.string.task_type_1)); // Use localized string
        if (isDateOnly) {
            showDatePickerDialog(isDateOnly);
        } else {
            showDateTimePickerDialog();
        }
    }

    private void showDatePickerDialog(boolean isDateOnly) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfoActivity.this, (view, year1, monthOfYear, dayOfMonth) -> {
            String dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
            dueDateTextView.setText(dateString);

            if (isDateOnly) {
                // Directly proceed without time
                // Optionally, prompt for a comment or confirmation
                //showCommentDialog(null);
            } else {
                // Proceed to pick time
                showTimePickerDialog();
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(TaskInfoActivity.this, (view, hourOfDay, minute1) -> {
            String timeString = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
            // Combine with existing date
            String existingDate = dueDateTextView.getText().toString();
            String dateTimeString = existingDate + " " + timeString;
            dueDateTextView.setText(dateTimeString);

            // Optionally, prompt for a comment or confirmation
            //showCommentDialog(timeString);
        }, hour, minute, true);
        timePickerDialog.show();
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

   /* private void showCommentDialog(CRMWork action, String date, String time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle(R.string.add_comment);

        final EditText input = new EditText(TaskInfoActivity.this);
        input.setHint(R.string.optional_comment);
        builder.setView(input);

        builder.setPositiveButton(R.string.YES, (dialog, which) -> {
            String comment = input.getText().toString();
            //createNewTask(action, date, time, comment);
        });
        builder.setNegativeButton(R.string.NO, (dialog, which) -> dialog.cancel());
        builder.show();
    }
    */



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
