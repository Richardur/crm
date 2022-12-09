package com.aiva.aivacrm.home;

import static data.GetTasks.getAddressesData;

import android.os.Bundle;

import com.aiva.aivacrm.databinding.ActivityTaskInfoBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aiva.aivacrm.R;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import adapter.AdapterTasks;
import model.Address;
import model.Customer;

public class TaskInfo extends AppCompatActivity {

    private ActivityTaskInfoBinding binding;

    int CustomerId, TaskId, VeiksmoID, AddressId;
    String TaskComment, TaskCustomer, TaskDuration, TaskDate, TaskDoneDate, TaskStartedDate;
    int Atlikta, Pradeta;
    String RepName, RepSurname, RepPhone, RepEmail;

    TextView customer, action, dueDate, comment, status;
    TextView phone, email, website, address, subject;

    List<Address> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTaskInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        customer = findViewById(R.id.client_name);
        action = findViewById(R.id.action);
        dueDate = findViewById(R.id.due_date);
        comment = findViewById(R.id.comment);
        status = findViewById(R.id.status);

        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        website = findViewById(R.id.website);
        address = findViewById(R.id.address);
        subject = findViewById(R.id.subject);


        getIncomingIntent();
        setTaskInfo();
        scheduleCall();


    }

    private void getIncomingIntent() {

        if (getIntent().hasExtra("CustomerId")) {
            CustomerId = getIntent().getIntExtra("CustomerId", 0);
        }
        if (getIntent().hasExtra("TaskId")) {
            TaskId = getIntent().getIntExtra("TaskId", 0);
        }
        if (getIntent().hasExtra("VeiksmoID")) {
            VeiksmoID = getIntent().getIntExtra("VeiksmoID", 0);
        }
        if (getIntent().hasExtra("TaskComment")) {
            TaskComment = getIntent().getStringExtra("TaskComment");
        }
        if (getIntent().hasExtra("TaskCustomer")) {
            TaskCustomer = getIntent().getStringExtra("TaskCustomer");
        }
        if (getIntent().hasExtra("TaskDuration")) {
            TaskDuration = getIntent().getStringExtra("TaskDuration");
        }
        if (getIntent().hasExtra("TaskDate")) {
            TaskDate = getIntent().getStringExtra("TaskDate");
        }
        if (getIntent().hasExtra("TaskDoneDate")) {
            TaskDoneDate = getIntent().getStringExtra("TaskDoneDate");
        }
        if (getIntent().hasExtra("TaskStartedDate")) {
            TaskStartedDate = getIntent().getStringExtra("TaskStartedDate");
        }
        if (getIntent().hasExtra("Atlikta")) {
            Atlikta = getIntent().getIntExtra("Atlikta", 0);
        }
        if (getIntent().hasExtra("Pradeta")) {
            Pradeta = getIntent().getIntExtra("Pradeta", 0);
        }
        if (getIntent().hasExtra("RepName")) {
            RepName = getIntent().getStringExtra("RepName");
        }
        if (getIntent().hasExtra("RepSurname")) {
            RepSurname = getIntent().getStringExtra("RepSurname");
        }
        if (getIntent().hasExtra("RepPhone")) {
            RepPhone = getIntent().getStringExtra("RepPhone");
        }
        if (getIntent().hasExtra("RepEmail")) {
            RepEmail = getIntent().getStringExtra("RepEmail");
        }
        if (getIntent().hasExtra("AddressId")) {
            AddressId = getIntent().getIntExtra("AddressId", 0);
        }

    }

    public void setTaskInfo() {
        //TODO: set action and status
        customer.setText(TaskCustomer);
        //action.setText(VeiksmoID);
        dueDate.setText(TaskDate);
        comment.setText(TaskComment);
        //status.setText(Atlikta);

        phone.setText(RepPhone);
        email.setText(RepEmail);
        String subjectText = RepName + " " + RepSurname;
        subject.setText(subjectText);
    }

    public void scheduleCall(){
        getAddressesData(
                new onAddressesRetrieved() {
                    @Override
                    public void getResult(List<Address> result) {
                        // The code in here runs much later in the future - the adapter
                        // will already have been set up, but will be empty.
                        items = result;
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).adrid == AddressId) {
                                website.setText(items.get(i).adrwww);
                                String addressText = items.get(i).adrgat + " " + items.get(i).adrnam + ", " +
                                        items.get(i).adrmiest + ", " + items.get(i).sal_kodas;
                                address.setText(addressText);
                            }
                        }

                    };
    });
    }

    public interface onAddressesRetrieved {
        void getResult(List<Address> result);
    }
}