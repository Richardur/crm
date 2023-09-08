package com.aiva.aivacrm.home;

import static data.GetTasks.connectApi;
import static data.GetTasks.getCustomerList;
//import static data.GetTasks.getCustomersData;
import static data.GetTasks.getWorkPlan;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import adapter.ItemAnimation;

import model.Task;
import network.CustomerListResponse;
import network.api_request_model.ApiResponse;
import network.api_request_model.ManagerWorkInPlan;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TasksTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksTab extends Fragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "time1";
    private static final String ARG_PARAM2 = "time2";

    private String time1;
    private String time2;
    Timestamp t1, t2;
    View view2;

    private RecyclerView recyclerView;
    private AdapterTasks mAdapter;
    List<model.Task> items = new ArrayList<>();
    List<ManagerWorkInPlan> workPlan = new ArrayList<>();
    private int animation_type = ItemAnimation.FADE_IN;

    public TasksTab() {
        // Required empty public constructor
    }

    public static TasksTab newInstance(String param1, String param2) {
        TasksTab fragment = new TasksTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            time1 = getArguments().getString(ARG_PARAM1);
            time2 = getArguments().getString(ARG_PARAM2);

            // Convert time1 and time2 to Timestamp objects
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            try {
                Date date1 = dateFormat.parse(time1);
                Date date2 = dateFormat.parse(time2);
                t1 = new Timestamp(date1.getTime());
                t2 = new Timestamp(date2.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        setAdapter(view);
        view2= view;
        scheduleCall();
        return view;


    }

    private void setAdapter(View view) {

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterTasks(getContext(), items, new AdapterTasks.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(Task task) {
                Intent taskInfoIntent = new Intent(getContext(), TaskInfo.class);
                taskInfoIntent.putExtra("TaskId", task.getID());
                taskInfoIntent.putExtra("Veiksmas", task.getWorkInPlanName());
                taskInfoIntent.putExtra("TaskDate", task.getWorkInPlanTerm().toString());
                taskInfoIntent.putExtra("TaskCustomer", task.getWorkInPlanForCutomerName());
                taskInfoIntent.putExtra("TaskComment", task.getWorkInPlanNote());
                // Add more extras as needed to pass task information to TaskInfo activity.
                startActivity(taskInfoIntent);
            }
        });
        mAdapter.setOnTaskItemClickListener(new AdapterTasks.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(model.Task task) {
                // Handle regular task item click here
                // You can start the TaskInfo activity and pass relevant data to it
                // Example:
                Intent taskInfoIntent = new Intent(getContext(), TaskInfo.class);
                taskInfoIntent.putExtra("TaskId", task.getID());
                taskInfoIntent.putExtra("TaskName", task.getWorkInPlanName());
                taskInfoIntent.putExtra("TaskDate", task.getWorkInPlanTerm());
                // Add more extras as needed to pass task information to TaskInfo activity.
                startActivity(taskInfoIntent);
            }
        });
        Log.d("TasksTab", "Setting adapter with OnTaskItemClickListener");
        recyclerView.setAdapter(mAdapter);

    }

    private void scheduleCall() {
        connectApi("ricardas", "0ff4b70dabd059fa7b86d631eb6005a0479845bc2d03f66338bb848a90c2867e", new OnApiKeyRetrieved() {
            @Override
            public void onApiKeyReceived(String apiKey) {
                // Create a CountDownLatch with a count of 2 (two network requests)
                //CountDownLatch latch = new CountDownLatch(2);
                // Variables to hold the results of the network requests
                //List<ManagerWorkInPlan> managerWorkInPlanResult = new ArrayList<>();
                //final CustomerListResponse[] customerListResponseResult = {null};

                getWorkPlan(apiKey, new OnTasksRetrieved() {
                    @Override
                    public void getResult(ApiResponse result) {
                        workPlan = result.getData().getManagerWorkInPlanList();
                        if (result != null) {
                            // Log response status
                            Log.d("API Response", "Success: " + result.isSuccess());

                            // Log the data field, if not null
                            if (result.getData() != null) {
                                // Log managerWorkInPlanList size, if not null
                                if (result.getData().getManagerWorkInPlanList() != null) {
                                    int managerWorkInPlanListSize = result.getData().getManagerWorkInPlanList().size();
                                    Log.d("API Response", "ManagerWorkInPlanList size: " + managerWorkInPlanListSize);
                                } else {
                                    Log.d("API Response", "ManagerWorkInPlanList is null");
                                }
                            } else {
                                Log.d("API Response", "Data field is null");
                            }

                            // Log the entire API response
                            Log.d("API Response", result.toString());
                            // Check if the necessary data fields are not null
                            if (result.isSuccess() && result.getData() != null && result.getData().getManagerWorkInPlanList() != null) {
                                List<ManagerWorkInPlan> managerWorkInPlanList = result.getData().getManagerWorkInPlanList();

                                List<model.Task> items2 = new ArrayList<>();

                                // Loop through the list and log each ManagerWorkInPlan object
                                for (ManagerWorkInPlan managerWorkInPlan : managerWorkInPlanList) {
                                    Log.d("WorkPlan", managerWorkInPlan.toString());

                                    // Initialize variables with default values or empty strings
                                    int workInPlanID = 0;  // Change 0 to your desired default value
                                    String workInPlanForCustomerName = "";  // Change "" to your desired default value
                                    String workInPlanName = "";  // Change "" to your desired default value
                                    String workInPlanNote = "";  // Change "" to your desired default value
                                    Timestamp workInPlanTerm = t1;
                                            // Change "" to your desired default value
                                    Timestamp workInPlanDone = t1;  // Change 0 to your desired default value

                                    // Check for null values before assigning
                                    if (managerWorkInPlan.getWorkInPlanID() != null) {
                                        workInPlanID = managerWorkInPlan.getWorkInPlanID();
                                    }
                                    if (managerWorkInPlan.getWorkInPlanForCutomerName() != null) {
                                        workInPlanForCustomerName = managerWorkInPlan.getWorkInPlanForCutomerName();
                                    }
                                    if (managerWorkInPlan.getWorkInPlanName() != null) {
                                        workInPlanName = managerWorkInPlan.getWorkInPlanName();
                                    }
                                    if (managerWorkInPlan.getWorkInPlanNote() != null) {
                                        workInPlanNote = managerWorkInPlan.getWorkInPlanNote();
                                    }
                                    if (managerWorkInPlan.getWorkInPlanTerm() != null) {
                                        workInPlanTerm = managerWorkInPlan.getWorkInPlanTerm();
                                    }
                                    if (managerWorkInPlan.getWorkInPlanDone() != null) {
                                        workInPlanDone = managerWorkInPlan.getWorkInPlanDone();
                                    }

                                    // Create the model.Task object with the assigned values
                                    model.Task task = new model.Task(workInPlanID, workInPlanForCustomerName, workInPlanName, workInPlanNote, workInPlanTerm, workInPlanDone);
                                    items2.add(task);

                                    Log.d("WorkPlan task", task.toString());
                                }
                                // Filter items based on date range
                                List<model.Task> filteredItems = new ArrayList<>();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                for (model.Task t : items2) {
                                    try {
                                        // Convert Timestamp to String
                                        String workInPlanTermStr = t.getWorkInPlanTerm().toString();

                                        // Parse the String to a Date
                                        Date workInPlanTerm = dateFormat.parse(workInPlanTermStr);

                                        // Convert Date to Timestamp if needed
                                        Timestamp timestamp = new Timestamp(workInPlanTerm.getTime());

                                        if (timestamp.after(t1) && timestamp.before(t2)) {
                                            filteredItems.add(t);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // Log the count of filtered items
                                Log.d("Filtered Items Count", String.valueOf(filteredItems.size()));



                                // Sort filteredItems based on AtlikData
                                Collections.sort(filteredItems, new Comparator<model.Task>() {
                                    @Override
                                    public int compare(model.Task task1, model.Task task2) {
                                        return task1.getWorkInPlanTerm().compareTo(task2.getWorkInPlanTerm());
                                    }
                                });

                                // Update the adapter with the filtered and sorted data
                                items = filteredItems;
                                mAdapter.setItems(items);
                                mAdapter.notifyDataSetChanged();

                                AdapterTasks adapterTasks = new AdapterTasks(getContext(), filteredItems, new AdapterTasks.OnTaskItemClickListener() {
                                    @Override
                                    public void onTaskItemClick(Task task) {

                                    }
                                });
                                mAdapter = adapterTasks;
                                setAdapter(view2);
                            } else {
                                Log.e("WorkPlan", "No data found in response or response is not successful.");
                            }
                        } else {
                            Log.e("WorkPlan", "API response is null.");
                        }
                        //latch.countDown();
                    }
                });
               /* getTasksData(
                        new OnTasksRetrieved() {
                            @Override
                            public void getResult(List<Task> result) {
                                // The code in here runs much later in the future - the adapter
                                // will already have been set up, but will be empty.
                                List<Task> items2 = new ArrayList<>();

                                t1 = Timestamp.valueOf(time1);
                                t2 = Timestamp.valueOf(time2);
                                for (Task t : result) {
                                    if (t.AtlikData.after(t1) && t.AtlikData.before(t2)) {
                                        items2.add(t);
                                    }
                                }

                                //sort items2 based on hours
                                for (int i = 0; i < items2.size(); i++) {
                                    for (int j = i + 1; j < items2.size(); j++) {
                                        if (items2.get(i).AtlikData.after(items2.get(j).AtlikData)) {
                                            Task temp = items2.get(i);
                                            items2.set(i, items2.get(j));
                                            items2.set(j, temp);
                                        }
                                    }
                                }

                                AdapterTasks.setItems(items2);
                                setAdapter(view2);// add this method to the adapter
                            }
                        }


                );*/
                /*connectApi("ricardas", "0ff4b70dabd059fa7b86d631eb6005a0479845bc2d03f66338bb848a90c2867e", new OnApiKeyRetrieved() {
                    @Override
                    public void onApiKeyReceived(String apiKey) {
                        // Create a CountDownLatch with a count of 1 (one network request)
                        CountDownLatch latch = new CountDownLatch(1);

                        // Get work plan data
                        getWorkPlan(apiKey, new OnTasksRetrieved() {
                            @Override
                            public void getResult(ApiResponse result) {
                                // Process the work plan data here
                                if (result != null) {
                                    // Check if the necessary data fields are not null
                                    if (result.isSuccess() && result.getData() != null && result.getData().getManagerWorkInPlanList() != null) {
                                        List<ManagerWorkInPlan> managerWorkInPlanList = result.getData().getManagerWorkInPlanList();
                                        // Loop through the list and log each ManagerWorkInPlan object
                                        for (ManagerWorkInPlan managerWorkInPlan : managerWorkInPlanList) {
                                            Log.d("WorkPlan", managerWorkInPlan.toString());
                                        }
                                    } else {
                                        Log.e("WorkPlan", "No data found in response or response is not successful.");
                                    }
                                } else {
                                    Log.e("WorkPlan", "API response is null.");
                                }

                                // Process the merged data as needed here
                                List<Task> items2 = new ArrayList<>();
                                t1 = Timestamp.valueOf(time1);
                                t2 = Timestamp.valueOf(time2);
                                for (Task t : result) {
                                    if (t.AtlikData.after(t1) && t.AtlikData.before(t2)) {
                                        items2.add(t);
                                    }
                                }

                                // Sort items2 based on hours if needed
                                for (int i = 0; i < items2.size(); i++) {
                                    for (int j = i + 1; j < items2.size(); j++) {
                                        if (items2.get(i).AtlikData.after(items2.get(j).AtlikData)) {
                                            Task temp = items2.get(i);
                                            items2.set(i, items2.get(j));
                                            items2.set(j, temp);
                                        }
                                    }
                                }

                                // Update the adapter with the merged data
                                AdapterTasks.setItems(items2);
                                setAdapter(view2);

                                // Decrease the latch count
                                latch.countDown();
                            }
                        });

                        try {
                            // Wait for the task to complete
                            latch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }); */
                getCustomerList(apiKey, new OnCustomerListRetrieved() {
                    @Override
                    public void getResult(CustomerListResponse result) {
                       // customerListResponseResult[0] = result;
                        // Decrease the latch count
                        //latch.countDown();
                        //List<Customer> items2 = new ArrayList<>();
                        //for (Customer c : result) {
                       //     items2.add(c);
                        //}
                        //AdapterTasks.setCustomers(items2);
                    }
                }
                );
            }
        });
    }



    public interface OnTasksRetrieved {
        void getResult(ApiResponse result);
    }
    public interface OnCustomerListRetrieved {
        void getResult(CustomerListResponse result);
    }
    public interface OnApiKeyRetrieved {
        void onApiKeyReceived(String apiKey);
    }

    public interface OnTaskItemClickListener {
        void onTaskItemClick(model.Task task);
    }
}