package com.aiva.aivacrm.home;

import static data.GetTasks.getCustomer;

import android.app.DatePickerDialog;
import android.app.TaskInfo;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.aiva.aivacrm.databinding.ActivityTaskInfoBinding;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aiva.aivacrm.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.Customer;
import model.Task;
import network.ApiResponseUpdate;
import network.ApiService;
import network.CustomerUpdateRequest;
import network.ManagerReactionUpdateRequest;
import network.UserSessionManager;
import network.api_request_model.ApiResponseGetCustomer;
import network.api_request_model.ManagerReactionWorkInPlan;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TaskInfoActivity extends AppCompatActivity {

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
    private TextView orderTextView;
    private TextInputLayout phoneLayout, emailLayout, websiteLayout, addressLayout, commentLayout;
    private EditText editPhone, editEmail, editWebsite, editAddress, editComment;
    private ImageButton callButton, emailButton, mapButton, editButton, saveButton, cancelButton;
    private CheckBox statusCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTaskInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        initializeViews();
        setListeners();

        Task task = getIntent().getParcelableExtra("Task");
        if (task != null) {
            extractTaskData(task);
            setTaskInfo(task);
            getCustomerCredentials(task);
        }
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

        editButton.setOnClickListener(v -> showEditConfirmationDialog());
    }

    private void showStatusConfirmationDialog(String title, boolean isChecked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle(title);
        builder.setPositiveButton(R.string.YES, (dialogInterface, i) -> {
            statusTextView.setText(isChecked ? "Completed" : "Not completed");
            statusCheckbox.setChecked(isChecked);
        });
        builder.setNegativeButton(R.string.NO, (dialogInterface, i) -> {
            statusTextView.setText(isChecked ? "Not completed" : "Completed");
            statusCheckbox.setChecked(!isChecked);
        });
        builder.show();
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

        editAddress.setOnClickListener(v -> showEditAddressDialog());

        commentTextView.setVisibility(View.GONE);
        editComment.setText(commentTextView.getText().toString());
        commentLayout.setVisibility(View.VISIBLE);

        editButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        // Set listeners for date picking
        dueDateTitleTextView.setOnClickListener(v -> showDateTimePicker());
        dueDateTextView.setOnClickListener(v -> showDateTimePicker());

        saveButton.setOnClickListener(v -> saveChanges());
        cancelButton.setOnClickListener(v -> exitEditMode());
    }

    private void showEditAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_address, null);
        builder.setView(view);

        EditText editStreet = view.findViewById(R.id.edit_street);
        EditText editHouse = view.findViewById(R.id.edit_house);
        EditText editPostIndex = view.findViewById(R.id.edit_post_index);
        EditText editCity = view.findViewById(R.id.edit_city);
        EditText editCountryCode = view.findViewById(R.id.edit_country_code);

        // Populate the fields with current address data
        String[] addressParts = addressTextView.getText().toString().split(" ");
        editCity.setText(addressParts[0]);
        editStreet.setText(addressParts[1]);
        editHouse.setText(addressParts[2]);
        editPostIndex.setText(addressParts[3]);
        editCountryCode.setText(addressParts[4]);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newStreet = editStreet.getText().toString();
            String newHouse = editHouse.getText().toString();
            String newPostIndex = editPostIndex.getText().toString();
            String newCity = editCity.getText().toString();
            String newCountryCode = editCountryCode.getText().toString();

            addressTextView.setText(newCity + " " + newStreet + " " + newHouse + " " + newPostIndex + " " + newCountryCode);
            address = addressTextView.getText().toString();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveChanges() {
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

        String apiKey = UserSessionManager.getApiKey(TaskInfoActivity.this);
        if (apiKey == null || apiKey.isEmpty()) {
            Log.e("UpdateWorkPlan", "API Key not found. Please login again.");
            Toast.makeText(TaskInfoActivity.this, "Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        String userId = UserSessionManager.getUserId(TaskInfoActivity.this);
        if (userId == null || userId.isEmpty()) {
            Log.e("UpdateWorkPlan", "User ID not found. Please login again.");
            Toast.makeText(TaskInfoActivity.this, "Please login again.", Toast.LENGTH_LONG).show();
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

        String newDueDate = dueDateTextView.getText().toString();
        if (!newDueDate.equals(taskDate)) {
            work.setReactionWorkTerm(newDueDate);
            taskChanged = true;
        }
        if (!editedComment.equals(taskComment)) {
            work.setReactionWorkNote(editedComment);
            taskChanged = true;
        }

        boolean newStatus = statusCheckbox.isChecked();
        if (newStatus && !taskDoneDate.equals("Completed")) {
            work.setReactionWorkDoneDate(getCurrentTimestamp());
            work.setReactionWorkDone("1");
            taskChanged = true;
        } else if (!newStatus && taskDoneDate.equals("Completed")) {
            work.setReactionWorkDoneDate(null);
            work.setReactionWorkDone("0");
            taskChanged = true;
        }

        if (taskChanged) {
            works.add(work);
            taskUpdateRequest.setManagerReactionInPlanHeaderReg(header);
            taskUpdateRequest.setManagerReactionWorkReg(works);
        }

        // Create the update request for customer info
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest();
        customerUpdateRequest.setUserId(userId);
        customerUpdateRequest.setApiKey(apiKey);
        customerUpdateRequest.setAction("update");
        customerUpdateRequest.setLanguageCode("lt");
        customerUpdateRequest.setCustomerId(taskCustomerId);

        // Set customer data
        customerUpdateRequest.setCustomerAdressCity(editAddress.getText().toString()); // Assume all fields are filled in dialog
        // set other address fields accordingly

        if (!editedPhone.equals(repPhone) || !editedEmail.equals(repEmail) || !editedAddress.equals(address)) {
            customerChanged = true;

            // Update the contact person
            Customer.CustomerContactPerson selectedPerson = getSelectedContactPerson();
            selectedPerson.setContactPersonMobPhone(editedPhone);
            selectedPerson.setContactPersonMail(editedEmail);

            // Ensure you add the contact person to the customer object
            List<Customer.CustomerContactPerson> contactPersons = new ArrayList<>();
            contactPersons.add(selectedPerson);
            customerUpdateRequest.setCustomerContactPersons(contactPersons);
        }

        if (taskChanged) {
            updateWorkPlan(this, taskUpdateRequest, new Callback<ApiResponseUpdate>() {
                @Override
                public void onResponse(Call<ApiResponseUpdate> call, Response<ApiResponseUpdate> response) {
                    if (response.isSuccessful()) {
                        Log.d("UpdateWorkPlan", "Update successful");
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

        if (customerChanged) {
            updateCustomer(this, customerUpdateRequest, new Callback<ApiResponseUpdate>() {
                @Override
                public void onResponse(Call<ApiResponseUpdate> call, Response<ApiResponseUpdate> response) {
                    if (response.isSuccessful()) {
                        Log.d("UpdateCustomer", "Update successful");
                    } else {
                        Log.e("UpdateCustomer", "Update failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseUpdate> call, Throwable t) {
                    Log.e("UpdateCustomer", "Network error: " + t.getMessage());
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
        // Fetch the list of customer contact persons from the data source
        return new ArrayList<>(); // Replace with actual data retrieval logic
    }

    private void updateCustomer(Context context, CustomerUpdateRequest request, Callback<ApiResponseUpdate> callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
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

        dueDateTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
        dueDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void extractTaskData(Task task) {
        taskCustomerId = task.getWorkInPlanForCustomerID();
        taskId = task.getWorkInPlanID();
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

        // Ensure that taskDone and taskDoneDate are not null before checking their values
        boolean isCompleted = "1".equals(task.getWorkInPlanDone()) && task.getWorkInPlanDoneDate() != null;
        statusCheckbox.setChecked(isCompleted);
        statusTextView.setText(isCompleted ? "Completed" : "Not completed");
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

    public interface OnCustomerRetrieved {
        void getResult(ApiResponseGetCustomer result);
    }

    public static void updateWorkPlan(Context context, ManagerReactionUpdateRequest request, Callback<ApiResponseUpdate> callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gamyba.online/api-aiva/v1/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(TaskInfoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String dateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute;
                        dueDateTextView.setText(dateString);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }
}
