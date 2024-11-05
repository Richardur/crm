package com.aiva.aivacrm.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aiva.aivacrm.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.snackbar.Snackbar; // Import Snackbar

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.CRMWork;
import model.Customer;
import network.ApiService;
import network.RetrofitClientInstance;
import network.UserSessionManager;
import network.api_response.CRMWorkResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTaskDialogFragment extends DialogFragment {

    private EditText dateEditText;
    private EditText timeEditText;
    private EditText commentEditText;
    private MaterialAutoCompleteTextView actionSpinner;
    private MaterialAutoCompleteTextView repSpinner;
    private TextInputLayout actionSpinnerLayout;
    private TextInputLayout dateInputLayout;
    private TextInputLayout timeInputLayout;
    private TextInputLayout repSpinnerLayout;
    private OnNewTaskCreatedListener listener;
    private List<CRMWork> actionList = new ArrayList<>();
    private ArrayAdapter<String> actionAdapter;
    private boolean isDateOnly = false;
    private CheckBox addToCalendarCheckBox;
    private CheckBox sendInviteCheckBox;
    private List<Customer.CustomerContactPerson> contactPersons;
    private int selectedRepIndex;

    public interface OnNewTaskCreatedListener {
        void onNewTaskCreated(CRMWork action, String date, String time, String comment, boolean addToCalendar, boolean sendInvite, Customer.CustomerContactPerson selectedRep);
    }

    public static NewTaskDialogFragment newInstance(OnNewTaskCreatedListener listener, List<Customer.CustomerContactPerson> contactPersons, int selectedRepIndex) {
        NewTaskDialogFragment fragment = new NewTaskDialogFragment();
        fragment.listener = listener;
        fragment.contactPersons = contactPersons;
        fragment.selectedRepIndex = selectedRepIndex;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_task_dialog, container, false);

        // Initialize views
        actionSpinner = view.findViewById(R.id.actionSpinner);
        repSpinner = view.findViewById(R.id.repSpinner);
        dateEditText = view.findViewById(R.id.dateEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        commentEditText = view.findViewById(R.id.commentEditText);
        addToCalendarCheckBox = view.findViewById(R.id.addToCalendarCheckBox);
        sendInviteCheckBox = view.findViewById(R.id.sendInviteCheckBox);

        // Initialize TextInputLayouts
        actionSpinnerLayout = view.findViewById(R.id.actionSpinnerLayout);
        dateInputLayout = view.findViewById(R.id.dateInputLayout);
        timeInputLayout = view.findViewById(R.id.timeInputLayout);
        repSpinnerLayout = view.findViewById(R.id.repSpinnerLayout);

        // Disable sendInviteCheckBox initially
        sendInviteCheckBox.setEnabled(false);

        // Enable sendInviteCheckBox only if addToCalendarCheckBox is checked
        addToCalendarCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendInviteCheckBox.setEnabled(isChecked);
            if (!isChecked) {
                sendInviteCheckBox.setChecked(false);
            }
        });

        // Populate representative names
        List<String> repNames = new ArrayList<>();
        for (Customer.CustomerContactPerson contactPerson : contactPersons) {
            String nameWithType = contactPerson.getContactPersonName() + " " + contactPerson.getContactPersonSurname();
            repNames.add(nameWithType);
        }

        // Set up representative spinner adapter
        ArrayAdapter<String> repAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, repNames);
        repSpinner.setAdapter(repAdapter);

        // Set default selection
        if (selectedRepIndex >= 0 && selectedRepIndex < repNames.size()) {
            repSpinner.setText(repNames.get(selectedRepIndex), false);
        }

        // Initialize the actionSpinner with custom adapter to handle multi-line items
        List<String> actionItems = new ArrayList<>();
        actionAdapter = new ArrayAdapter<>(getContext(),
                R.layout.action_spinner, actionItems);
        actionAdapter.setDropDownViewResource(R.layout.action_spinner);
        actionSpinner.setAdapter(actionAdapter);

        // Set hint (TextInputLayout handles the hint)
        actionSpinner.setText(getString(R.string.action_prompt), false);

        // Initialize dropdown items when fetched
        fetchActions();

        // Handle action spinner item selection
        actionSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            if (position >= 0 && position < actionList.size()) {
                CRMWork selectedWork = actionList.get(position);
                if (selectedWork != null) {
                    isDateOnly = "yyyy.mm.dd".equals(selectedWork.getCRMWorkFormat());
                    timeInputLayout.setVisibility(isDateOnly ? View.GONE : View.VISIBLE);
                }
            } else {
                // Invalid selection; hide the timeInputLayout
                timeInputLayout.setVisibility(View.GONE);
            }
        });

        // Initialize date and time pickers
        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        timeEditText.setOnClickListener(v -> showTimePickerDialog());

        // Handle create button click with enhanced validation and confirmation dialog
        MaterialButton createButton = view.findViewById(R.id.createButton);
        createButton.setOnClickListener(v -> {
            String selectedActionName = actionSpinner.getText().toString().trim();
            int selectedActionPosition = actionAdapter.getPosition(selectedActionName);

            boolean hasError = false;

            // Clear previous errors
            actionSpinnerLayout.setError(null);
            dateInputLayout.setError(null);
            timeInputLayout.setError(null);
            repSpinnerLayout.setError(null);

            // Validate Action Selection
            if (selectedActionPosition < 0 || selectedActionPosition >= actionList.size()) {
                actionSpinnerLayout.setError(getString(R.string.error_select_action));
                hasError = true;
            }

            // Validate Representative Selection
            String selectedRepName = repSpinner.getText().toString().trim();
            int selectedRepPosition = repAdapter.getPosition(selectedRepName);
            if (selectedRepPosition < 0 || selectedRepPosition >= contactPersons.size()) {
                repSpinnerLayout.setError(getString(R.string.error_select_representative));
                hasError = true;
            }

            // Validate Date and Time Based on Action Format
            if (isDateOnly) {
                String date = dateEditText.getText().toString().trim();
                if (date.isEmpty()) {
                    dateInputLayout.setError(getString(R.string.error_select_date));
                    hasError = true;
                }
            } else {
                String date = dateEditText.getText().toString().trim();
                String time = timeEditText.getText().toString().trim();
                if (date.isEmpty()) {
                    dateInputLayout.setError(getString(R.string.error_select_date));
                    hasError = true;
                }
                if (time.isEmpty()) {
                    timeInputLayout.setError(getString(R.string.error_select_time));
                    hasError = true;
                }
            }

            if (hasError) {
                Toast.makeText(getContext(), "Please fix the errors above", Toast.LENGTH_SHORT).show();
                return;
            }

            // Proceed to show confirmation dialog
            showConfirmationDialog();
        });

        // Handle cancel button click
        MaterialButton cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Calculate desired width and height as a percentage of screen size
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95); // 95% of screen width
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80); // 80% of screen height

            // Apply the dimensions to the dialog window
            getDialog().getWindow().setLayout(width, height);

            // Optional: Set the dialog's background to be transparent for rounded corners, etc.
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    // Show DatePickerDialog for selecting the date
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.CustomDatePickerDialogTheme,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    dateEditText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Show TimePickerDialog for selecting the time
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.CustomDatePickerDialogTheme,
                (view, hourOfDay, minute) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute);
                    timeEditText.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    // Fetch available actions from the API and populate the actionSpinner
    private void fetchActions() {
        String apiKey = UserSessionManager.getApiKey(getContext());
        String userId = UserSessionManager.getUserId(getContext());

        ApiService apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<CRMWorkResponse> call = apiService.getActionDetails(userId, apiKey, "*", "", "select", "", "1000", "");

        call.enqueue(new Callback<CRMWorkResponse>() {
            @Override
            public void onResponse(Call<CRMWorkResponse> call, Response<CRMWorkResponse> response) {
                CRMWorkResponse crmWorkResponse = response.body();
                if (crmWorkResponse != null && crmWorkResponse.isSuccess()) {
                    List<CRMWork> crmWorkList = crmWorkResponse.getData().getCrmWorkList();
                    if (crmWorkList != null) {
                        // Clear previous data
                        actionList.clear();
                        actionAdapter.clear();

                        // Populate actionList and actionAdapter
                        for (CRMWork work : crmWorkList) {
                            actionList.add(work);
                            actionAdapter.add(work.getCRMWorkName());
                        }
                        actionAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("Action log", "Action Request Failed - Response not successful");
                    Toast.makeText(getContext(), "Failed to load actions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CRMWorkResponse> call, Throwable t) {
                Log.d("Action log", "Action Request Failed");
                Log.d("Action log", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error loading actions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show a confirmation dialog to confirm task creation
    private void showConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext(), R.style.TaskInfo_App_MaterialAlertDialog)
                .setTitle(getString(R.string.confirm_task_creation_title))
                .setMessage(getString(R.string.confirm_task_creation_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    String selectedActionName = actionSpinner.getText().toString().trim();
                    int selectedActionPosition = actionAdapter.getPosition(selectedActionName);
                    CRMWork selectedAction = actionList.get(selectedActionPosition);

                    String date = dateEditText.getText().toString().trim();
                    String time = isDateOnly ? "" : timeEditText.getText().toString().trim();
                    String comment = commentEditText.getText().toString().trim();
                    boolean addToCalendar = addToCalendarCheckBox.isChecked();
                    boolean sendInvite = sendInviteCheckBox.isChecked();

                    String selectedRepName = repSpinner.getText().toString().trim();
                    int selectedRepPosition = ((ArrayAdapter<String>) repSpinner.getAdapter()).getPosition(selectedRepName);
                    Customer.CustomerContactPerson selectedRep = contactPersons.get(selectedRepPosition);

                    // Invoke the listener callback to create the task
                    listener.onNewTaskCreated(selectedAction, date, time, comment, addToCalendar, sendInvite, selectedRep);

                    // Dismiss the dialog
                    dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                    // User canceled, dismiss the dialog
                    dialog.dismiss();
                })
                .show();
    }
}
