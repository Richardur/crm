package com.aiva.aivacrm.home;

import static data.GetTasks.connectApi;

import static data.GetTasks.getCustomer;
//import static data.GetTasks.testAPI;
import network.ApiKeyCallback;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.aiva.aivacrm.databinding.ActivityTaskInfoBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.aiva.aivacrm.R;
import com.google.android.material.textfield.TextInputLayout;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.AdapterTasks;
import model.Address;
import model.Customer;
import network.AuthResponse;
import network.CustomerListResponse;
import network.CustomerResponse;

public class TaskInfo extends AppCompatActivity {

    private ActivityTaskInfoBinding binding;



    int CustomerId, TaskId, AddressId;
    String TaskComment, TaskCustomer, TaskDuration, TaskDate, TaskDoneDate, TaskStartedDate;
    int Atlikta, Pradeta;
    String RepName, RepSurname, RepPhone, RepEmail;
    String Veiksmas;

    TextView customer, action, dueDate, comment, status;
    TextView phone, email, website, address, subject;
    TextView dueDateTitle, statusTitle;
    TextInputLayout phoneLayout, emailLayout, websiteLayout, addressLayout, commentLayout;
    EditText editPhone, editEmail, editWebsite, editAddress, editComment;
    ImageButton callButton, emailButton, mapButton, editButton, saveButton, cancelButton;
    CheckBox statusCheckbox;

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
        dueDateTitle = findViewById(R.id.due_date_title);
        dueDate = findViewById(R.id.due_date);
        comment = findViewById(R.id.comment);
        statusTitle = findViewById(R.id.status_title);
        status = findViewById(R.id.status);

        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        website = findViewById(R.id.website);
        address = findViewById(R.id.address);
        subject = findViewById(R.id.subject);

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

        getIncomingIntent();
        setTaskInfo();
        //scheduleCall();

        //on clicking the call button, the phone app opens with the number of the client
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone.getText().toString()));
                startActivity(intent);
            }
        });
        //on clicking the email button, check if there is an email app installed on the device and if so, open it with the email of the client
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, email.getText().toString());
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });


        //on clicking the map button, the map app opens with the address of the client
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:0,0?q=" + address.getText().toString()));
                startActivity(intent);
            }
        });
        //on clicking the status checkbox, the status changes to completed
        statusCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusCheckbox.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfo.this);
                    //set the alert dialog title and center it
                    builder.setTitle("Complete the task?");
                    builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            status.setText("Completed");
                            statusCheckbox.setChecked(true);
                        }
                    });
                    builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            status.setText("Not completed");
                            statusCheckbox.setChecked(false);
                        }
                    });
                    builder.setNeutralButton("ATTACH PICTURE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else if (!statusCheckbox.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfo.this);
                    //set the alert dialog title and center it
                    builder.setTitle("Mark the task as not completed?");
                    builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            status.setText("Not completed");
                            statusCheckbox.setChecked(false);
                        }
                    });
                    builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            status.setText("Completed");
                            statusCheckbox.setChecked(true);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
        //on clicking the edit button, change the textview of phone, email, website, address to edittext and make them editable
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfo.this);
                //set the alert dialog title and center it
                builder.setTitle("Edit?");
                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        phone.setVisibility(View.GONE);
                        editPhone.setText(phone.getText().toString());
                        phoneLayout.setVisibility(View.VISIBLE);

                        email.setVisibility(View.GONE);
                        editEmail.setText(email.getText().toString());
                        emailLayout.setVisibility(View.VISIBLE);

                        website.setVisibility(View.GONE);
                        editWebsite.setText(website.getText().toString());
                        websiteLayout.setVisibility(View.VISIBLE);

                        address.setVisibility(View.GONE);
                        editAddress.setText(address.getText().toString());
                        addressLayout.setVisibility(View.VISIBLE);

                        comment.setVisibility(View.GONE);
                        editComment.setText(comment.getText().toString());
                        commentLayout.setVisibility(View.VISIBLE);

                        editButton.setVisibility(View.GONE);
                        saveButton.setVisibility(View.VISIBLE);
                        cancelButton.setVisibility(View.VISIBLE);


                        //set duedatetitle color to yellow_A700
                        dueDateTitle.setTextColor(getResources().getColor(R.color.yellow_A700));
                        //set status title color to yellow_A700
                        statusTitle.setTextColor(getResources().getColor(R.color.yellow_A700));
                        //set action color to yellow_A700
                        action.setTextColor(getResources().getColor(R.color.yellow_A700));
                        //on clicking the due date title, open the date picker dialog with date and hour and minute options and set the date to the due date textview
                        dueDateTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);

                                DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfo.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        //open the time picker dialog with hour and minute options and set the time to the due date textview
                                        TimePickerDialog timePickerDialog = new TimePickerDialog(TaskInfo.this, new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                                String dateString = year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute;
                                                dueDate.setText(dateString);
                                            }
                                        }, hour, minute, true);
                                        timePickerDialog.show();
                                    }
                                }, year, month, day);
                                datePickerDialog.show();
                            }
                        });
                        dueDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);

                                DatePickerDialog datePickerDialog = new DatePickerDialog(TaskInfo.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        //open the time picker dialog with hour and minute options and set the time to the due date textview
                                        TimePickerDialog timePickerDialog = new TimePickerDialog(TaskInfo.this, new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                                String dateString = year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute;
                                                dueDate.setText(dateString);
                                            }
                                        }, hour, minute, true);
                                        timePickerDialog.show();
                                    }
                                }, year, month, day);
                                datePickerDialog.show();
                            }
                        });



                    }
                });
                builder.setNegativeButton(R.string.NO, null);
                builder.show();

                statusCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (statusCheckbox.isChecked()) {
                            status.setText("Completed");
                        } else {
                            status.setText("Not completed");
                        }
                    }
                });

            }
        });


    }

    private void getIncomingIntent() {

        if (getIntent().hasExtra("CustomerId")) {
            CustomerId = getIntent().getIntExtra("CustomerId", 0);
        }
        if (getIntent().hasExtra("TaskId")) {
            TaskId = getIntent().getIntExtra("TaskId", 0);
        }
        if (getIntent().hasExtra("Veiksmas")) {
            Veiksmas = getIntent().getStringExtra("Veiksmas");
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
        action.setText(Veiksmas);
        dueDate.setText(TaskDate);
        comment.setText(TaskComment);
        //status.setText(Atlikta);

        phone.setText(RepPhone);
        email.setText(RepEmail);
        String subjectText = RepName + " " + RepSurname;
        subject.setText("Robertas Šertvytis");
    }
    public interface OnApiKeyRetrieved {
        void onApiKeyReceived(String apiKey);
    }
    public interface OnCustomerRetrieved {
        void getResult(Customer customer);
    }

    /*public void scheduleCall() {
        connectApi("ricardas", "0ff4b70dabd059fa7b86d631eb6005a0479845bc2d03f66338bb848a90c2867e", new ApiKeyCallback.OnApiKeyRetrieved() {
            @Override
            public void onApiKeyReceived(String apiKey) {
                getCustomer(apiKey, new OnCustomerRetrieved() {
                    @Override
                    public void getResult(Customer customer) {

                    }
                });
            }
        } );
    }
*/
    public interface onAddressesRetrieved {
        void getResult(List<Address> result);
    }
    public interface MyTasksRetrievedCallback {
        void onTasksRetrieved(AuthResponse response);
    }
}