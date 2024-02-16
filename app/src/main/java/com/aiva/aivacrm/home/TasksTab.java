package com.aiva.aivacrm.home;

import static data.GetTasks.connectApi;
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
import android.widget.Toast;

import com.aiva.aivacrm.R;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import adapter.AdapterTasks;
import adapter.ItemAnimation;
import model.Task;
import network.CustomerListResponse;
import network.FetchCalendarEventsTask;
import network.GoogleCalendarServiceSingleton;
import network.UserSessionManager;
import network.api_request_model.ApiResponseWorkPlan;
import network.api_request_model.ManagerWorkInPlan;

public class TasksTab extends Fragment {

    private static final String ARG_PARAM1 = "time1";
    private static final String ARG_PARAM2 = "time2";

    private String time1;
    private String time2;
    private Timestamp t1, t2;
    private View view2;

    private RecyclerView recyclerView;
    private AdapterTasks mAdapter;
    private List<Task> itemsFromDB = new ArrayList<>();
    private List<Task> itemsFromGoogleCalendar = new ArrayList<>();
    private List<Task> adapterItems = new ArrayList<>();
    private List<ManagerWorkInPlan> workPlan = new ArrayList<>();
    private int animation_type = ItemAnimation.FADE_IN;
    private LocalDate selectedDate;
    private GoogleCalendarServiceSingleton calendarServiceSingleton;

    public TasksTab() {
        // Required empty public constructor
    }

    public static TasksTab newInstance(String param1, String param2, LocalDate selectedDate) {
        TasksTab fragment = new TasksTab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putSerializable("selectedDate", selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleCalendarServiceSingleton.getInstance(getContext()); // Initialize without API key
        if (getArguments() != null) {
            time1 = getArguments().getString(ARG_PARAM1);
            time2 = getArguments().getString(ARG_PARAM2);
            selectedDate = (LocalDate) getArguments().getSerializable("selectedDate");
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
        // Initialize Google Calendar service through Singleton
        calendarServiceSingleton = GoogleCalendarServiceSingleton.getInstance(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        setAdapter(view);
        view2 = view;
        scheduleCall();
        return view;
    }

    private void setAdapter(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterTasks(getContext(), itemsFromDB, new AdapterTasks.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(Task task) {
                Intent taskInfoIntent = new Intent(getContext(), TaskInfo.class);
                taskInfoIntent.putExtra("TaskId", task.getID());
                taskInfoIntent.putExtra("Veiksmas", task.getWorkInPlanName());
                taskInfoIntent.putExtra("TaskDate", task.getWorkInPlanTerm().toString());
                taskInfoIntent.putExtra("TaskCustomer", task.getWorkInPlanForCutomerName());
                taskInfoIntent.putExtra("TaskComment", task.getWorkInPlanNote());
                taskInfoIntent.putExtra("TaskCustomerID", task.getWorkInPlanForCustomerID());
                startActivity(taskInfoIntent);
            }
        });

        mAdapter.setOnTaskItemClickListener(new AdapterTasks.OnTaskItemClickListener() {
            @Override
            public void onTaskItemClick(Task task) {
                Intent taskInfoIntent = new Intent(getContext(), TaskInfo.class);
                taskInfoIntent.putExtra("TaskId", task.getID());
                taskInfoIntent.putExtra("TaskName", task.getWorkInPlanName());
                taskInfoIntent.putExtra("TaskDate", task.getWorkInPlanTerm());
                taskInfoIntent.putExtra("TaskCustomer", task.getWorkInPlanForCutomerName());
                taskInfoIntent.putExtra("TaskComment", task.getWorkInPlanNote());
                taskInfoIntent.putExtra("TaskCustomerID", task.getWorkInPlanForCustomerID());
                startActivity(taskInfoIntent);
            }
        });

        Log.d("TasksTab", "Setting adapter with OnTaskItemClickListener");
        recyclerView.setAdapter(mAdapter);
    }


    private List<Task> convertEventsToTasks(List<Event> events) {
        List<Task> tasks = new ArrayList<>();
        for (Event event : events) {
            String taskName = event.getSummary();
            DateTime startTime = event.getStart().getDateTime();
            DateTime endTime = event.getEnd().getDateTime();
            String taskDescription = event.getDescription();
            int ID = 0; // Set an appropriate ID or leave it as 0
            String workInPlanForCustomerName = "";
            String workInPlanName = taskName;
            String workInPlanNote = taskDescription;
            Timestamp workInPlanTerm = new Timestamp(startTime.getValue());
            Timestamp workInPlanDone = new Timestamp(endTime.getValue());

            Task task = new Task(ID, workInPlanForCustomerName, workInPlanName, workInPlanNote, workInPlanTerm, workInPlanDone, "0");
            tasks.add(task);
        }
        return tasks;
    }

    private void updateAdapter(List<Task> tasks) {
        // Sort tasks based on workInPlanTerm
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task1.getWorkInPlanTerm().compareTo(task2.getWorkInPlanTerm());
            }
        });

        mAdapter.setItems(tasks);
        mAdapter.notifyDataSetChanged();
    }

    private void scheduleCall() {
        LocalDateTime startOfDay = selectedDate.atStartOfDay();
        LocalDateTime endOfDay = selectedDate.plusDays(1).atStartOfDay();

        // Convert LocalDateTime to DateTime
        DateTime startTime = new DateTime(startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        DateTime endTime = new DateTime(endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        if (UserSessionManager.userSignedIn) {
            new FetchCalendarEventsTask(getContext(), startTime, endTime, calendarServiceSingleton,
                    new FetchCalendarEventsTask.OnCalendarEventsFetchedListener() {
                        @Override
                        public void onCalendarEventsFetched(List<Event> events) {
                            if (events != null) {
                                // Convert events to tasks and update the adapter
                                itemsFromGoogleCalendar = convertEventsToTasks(events);
                                //if there are items from DB, combine the the two lists into adapterItems and sort them accordingly
                                if (itemsFromDB != null) {
                                    adapterItems = new ArrayList<>();
                                    adapterItems.addAll(itemsFromDB);
                                    adapterItems.addAll(itemsFromGoogleCalendar);
                                    updateAdapter(adapterItems);
                                } else {
                                    updateAdapter(itemsFromGoogleCalendar);
                                }

                            } else {
                                // Handle the case where fetching events failed
                                Toast.makeText(getContext(), "Failed to fetch events from Google Calendar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).execute();
        }

        connectApi("ricardas", "0ff4b70dabd059fa7b86d631eb6005a0479845bc2d03f66338bb848a90c2867e", new OnApiKeyRetrieved() {
            @Override
            public void onApiKeyReceived(String apiKey) {
                getWorkPlan(apiKey, new OnTasksRetrieved() {
                    @Override
                    public void getResult(ApiResponseWorkPlan result) {
                        workPlan = result.getData().getManagerWorkInPlanList();
                        if (result != null && result.isSuccess() && result.getData() != null && result.getData().getManagerWorkInPlanList() != null) {
                            List<ManagerWorkInPlan> managerWorkInPlanList = result.getData().getManagerWorkInPlanList();
                            List<Task> items2 = new ArrayList<>();

                            for (ManagerWorkInPlan managerWorkInPlan : managerWorkInPlanList) {
                                int workInPlanID = managerWorkInPlan.getWorkInPlanID() != null ? managerWorkInPlan.getWorkInPlanID() : 0;
                                String workInPlanForCustomerName = managerWorkInPlan.getWorkInPlanForCutomerName() != null ? managerWorkInPlan.getWorkInPlanForCutomerName() : "";
                                String workInPlanName = managerWorkInPlan.getWorkInPlanName() != null ? managerWorkInPlan.getWorkInPlanName() : "";
                                String workInPlanNote = managerWorkInPlan.getWorkInPlanNote() != null ? managerWorkInPlan.getWorkInPlanNote() : "";
                                Timestamp workInPlanTerm = managerWorkInPlan.getWorkInPlanTerm() != null ? managerWorkInPlan.getWorkInPlanTerm() : t1;
                                Timestamp workInPlanDone = managerWorkInPlan.getWorkInPlanDone() != null ? managerWorkInPlan.getWorkInPlanDone() : t1;
                                String workInPlanForCustomerID = managerWorkInPlan.getWorkInPlanForCustomerID() != null ? managerWorkInPlan.getWorkInPlanForCustomerID() : "";

                                Task task = new Task(workInPlanID, workInPlanForCustomerName, workInPlanName, workInPlanNote, workInPlanTerm, workInPlanDone, workInPlanForCustomerID);
                                items2.add(task);
                            }

                            List<Task> filteredItems = new ArrayList<>();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (Task t : items2) {
                                try {
                                    String workInPlanTermStr = t.getWorkInPlanTerm().toString();
                                    Date workInPlanTerm = dateFormat.parse(workInPlanTermStr);
                                    Timestamp timestamp = new Timestamp(workInPlanTerm.getTime());

                                    if (timestamp.after(t1) && timestamp.before(t2)) {
                                        filteredItems.add(t);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            Log.d("Filtered Items Count", String.valueOf(filteredItems.size()));
                            itemsFromDB = filteredItems;
                            //if items from Google Calendar are not null, combine the the two lists into adapterItems and sort them accordingly
                            if (itemsFromGoogleCalendar != null) {
                                adapterItems = new ArrayList<>();
                                adapterItems.addAll(itemsFromGoogleCalendar);
                                adapterItems.addAll(itemsFromDB);
                                updateAdapter(adapterItems);
                            } else {
                            updateAdapter(itemsFromDB);
                            }
                        } else {
                            Log.e("WorkPlan", "No data found in response or response is not successful.");
                        }
                    }
                });

                /* getCustomerList(apiKey, new OnCustomerListRetrieved() {
                    @Override
                    public void getResult(CustomerListResponse result) {
                        // Handle customer list data
                    }
                }); */
            }
        });
    }

    public interface OnTasksRetrieved {
        void getResult(ApiResponseWorkPlan result);
    }

    public interface OnCustomerListRetrieved {
        void getResult(CustomerListResponse result);
    }

    public interface OnApiKeyRetrieved {
        void onApiKeyReceived(String apiKey);
    }

    public interface OnTaskItemClickListener {
        void onTaskItemClick(Task task);
    }
}
// Rest of your code remains unchanged


