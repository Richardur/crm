package com.aiva.aivacrm.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static data.GetTasks.getWorkPlan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.aiva.aivacrm.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import adapter.AdapterTasks;
import model.Task;
import network.UserSessionManager;
import network.api_request_model.ApiResponseReactionPlan;
import network.api_request_model.ManagerReactionWorkInPlan;

public class TaskListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdapterTasks mAdapter;
    private List<Task> tasks = new ArrayList<>();
    private int selectedTaskId;
    private String clientId;
    private String orderId;
    private List<Integer> dateOnlyActionIds = new ArrayList<>();  // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedTaskId") && intent.hasExtra("clientId") && intent.hasExtra("orderId")) {
            selectedTaskId = intent.getIntExtra("selectedTaskId", -1);
            clientId = intent.getStringExtra("clientId");
            orderId = intent.getStringExtra("orderId");

            Log.d(TAG, "Received intent data: selectedTaskId=" + selectedTaskId + ", clientId=" + clientId + ", orderId=" + orderId);

            fetchTasksForClientAndOrder(clientId, orderId);
        } else {
            Toast.makeText(this, "No tasks available", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchTasksForClientAndOrder(String clientId, String orderId) {
        String apiKey = UserSessionManager.getApiKey(this);
        String userId = UserSessionManager.getUserId(this);

        if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Fetching tasks for clientId=" + clientId + " and orderId=" + orderId);

        getWorkPlan(this, clientId, orderId, "", "", new TasksTab.OnTasksRetrieved() {
            @Override
            public void getResult(ApiResponseReactionPlan result) {
                if (result != null && result.isSuccess() && result.getData() != null) {
                    List<ManagerReactionWorkInPlan.ManagerReactionInPlanHeader> headers = result.getData().getManagerReactionInPlanHeaderList();
                    for (ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header : headers) {
                        if (header.getReactionForCustomerID().equals(clientId) && header.getReactionByOrderNo().equals(orderId)) {
                            for (ManagerReactionWorkInPlan.ManagerReactionWork work : header.getManagerReactionWork()) {
                                Task task = new Task(
                                        header.getReactionHeaderID(),
                                        header.getReactionManagerID(),
                                        header.getReactionForCustomerID(),
                                        header.getReactionByOrderNo(),
                                        work.getReactionWorkID(),
                                        work.getReactionWorkManagerID().toString(),
                                        work.getReactionWorkManageName(),
                                        work.getReactionWorkDoneByID().toString(),
                                        work.getReactionWorkDoneByName(),
                                        work.getReactionWorkActionID().toString(),
                                        work.getReactionWorkActionName(),
                                        work.getReactionWorkNote(),
                                        work.getReactionWorkForCustomerName(),
                                        parseTimestamp(work.getReactionWorkTerm()),
                                        parseTimestamp(work.getReactionWorkDoneDate()),
                                        work.getReactionWorkDone(),
                                        dateOnlyActionIds.contains(work.getReactionWorkActionID())

                                );
                                tasks.add(task);
                            }
                        }
                    }
                    Log.d(TAG, "Tasks fetched: " + tasks.size());
                    sortTasksByCreationDate(tasks);
                    runOnUiThread(new Runnable() {  // Ensure UI updates are on the main thread
                        @Override
                        public void run() {
                            updateRecyclerView();
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to fetch tasks or no data returned.");
                    runOnUiThread(new Runnable() {  // Ensure UI updates are on the main thread
                        @Override
                        public void run() {
                            Toast.makeText(TaskListActivity.this, "Failed to fetch tasks.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void sortTasksByCreationDate(List<Task> tasks) {
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task2.getWorkInPlanTerm().compareTo(task1.getWorkInPlanTerm());
            }
        });
    }

    private void updateRecyclerView() {
        mAdapter = new AdapterTasks(this, tasks, new AdapterTasks.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(Task task) {
                Intent taskInfoIntent = new Intent(TaskListActivity.this, TaskInfoActivity.class);
                taskInfoIntent.putExtra("Task", task);
                startActivity(taskInfoIntent);
            }
        }, true, true); // Show full date and time, and indicate it's TaskListActivity
        recyclerView.setAdapter(mAdapter);
        highlightSelectedTask();
    }

    private void highlightSelectedTask() {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getWorkInPlanID() == selectedTaskId) {
                recyclerView.scrollToPosition(i);
                break;
            }
        }
    }

    private Timestamp parseTimestamp(String dateString) {
        if (dateString != null && !dateString.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedDate = dateFormat.parse(dateString);
                return new Timestamp(parsedDate.getTime());
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: " + dateString, e);
            }
        } else {
            Log.w(TAG, "Attempted to parse a null or empty date string.");
        }
        return null;
    }
}
