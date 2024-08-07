package com.aiva.aivacrm.home;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aiva.aivacrm.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import model.CRMWork;
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
    private Spinner actionSpinner;
    private OnNewTaskCreatedListener listener;
    private List<CRMWork> actionList = new ArrayList<>();
    private ArrayAdapter<String> actionAdapter;
    private boolean isDateOnly = false;

    public interface OnNewTaskCreatedListener {
        void onNewTaskCreated(CRMWork action, String date, String time, String comment);
    }

    public static NewTaskDialogFragment newInstance(OnNewTaskCreatedListener listener) {
        NewTaskDialogFragment fragment = new NewTaskDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_task_dialog, container, false);

        actionSpinner = view.findViewById(R.id.actionSpinner);
        dateEditText = view.findViewById(R.id.dateEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        commentEditText = view.findViewById(R.id.commentEditText);

        actionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(actionAdapter);

        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        timeEditText.setOnClickListener(v -> showTimePickerDialog());

        fetchActions();

        actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CRMWork selectedWork = actionList.get(position);
                isDateOnly = "yyyy.mm.dd".equals(selectedWork.getCRMWorkFormat());
                timeEditText.setVisibility(isDateOnly ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        MaterialButton createButton = view.findViewById(R.id.createButton);
        createButton.setOnClickListener(v -> {
            CRMWork selectedAction = actionList.get(actionSpinner.getSelectedItemPosition());
            String date = dateEditText.getText().toString();
            String time = timeEditText.getText().toString();
            String comment = commentEditText.getText().toString();
            listener.onNewTaskCreated(selectedAction, date, time, comment);
            dismiss();
        });

        MaterialButton cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String date = year + "-" + (month + 1) + "-" + dayOfMonth;
            dateEditText.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            String time = hourOfDay + ":" + minute;
            timeEditText.setText(time);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

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
                        for (CRMWork work : crmWorkList) {
                            actionList.add(work);
                            actionAdapter.add(work.getCRMWorkName());
                        }
                        actionAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("Action log", "Action Request Failed - Response not successful");
                }
            }

            @Override
            public void onFailure(Call<CRMWorkResponse> call, Throwable t) {
                Log.d("Action log", "Action Request Failed");
                Log.d("Action log", "Error: " + t.getMessage());
            }
        });
    }
}
