package com.aiva.aivacrm.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static data.GetTasks.getWorkPlan;

import android.content.Context;
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
import java.time.format.DateTimeFormatter;
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
import network.api_request_model.ApiResponseReactionPlan;
import network.api_request_model.ManagerReactionWorkInPlan;

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
    private List<ManagerReactionWorkInPlan> workPlan = new ArrayList<>();
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
                //usable
                Intent taskInfoIntent = new Intent(getContext(), TaskInfo.class);
                taskInfoIntent.putExtra("TaskId", task.getID());
                taskInfoIntent.putExtra("TaskName", task.getWorkInPlanName());
                taskInfoIntent.putExtra("TaskDate", task.getWorkInPlanTerm());
                taskInfoIntent.putExtra("TaskCustomer", task.getWorkInPlanForCutomerName());
                taskInfoIntent.putExtra("TaskComment", task.getWorkInPlanNote());
                taskInfoIntent.putExtra("TaskCustomerID", task.getWorkInPlanForCustomerID());
                taskInfoIntent.putExtra("TaskOrderID", task.getWorkInPlanForCustomerOrder());
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
            String workInPlanOrderID = "";

            Task task = new Task(ID, workInPlanForCustomerName, workInPlanName, workInPlanNote, workInPlanTerm, workInPlanDone, "0", workInPlanOrderID);
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

        // Convert LocalDateTime to java.util.Date first
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

        // Convert java.util.Date to java.sql.Timestamp
        t1 = new Timestamp(startDate.getTime());
        t2 = new Timestamp(endDate.getTime());

        // Formatting timestamps to String is not necessary for SQL query, so remove it if not needed
        // If you still need to format these timestamps:
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime1 = dateFormat.format(t1);
        String formattedTime2 = dateFormat.format(t2);

        // Now use formattedTime1 and formattedTime2 as string representations if needed for logging or API parameters
        Log.d(TAG, "Formatted Time1: " + formattedTime1 + ", Time2: " + formattedTime2);


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

        /* if (t1 == null || t2 == null) {
            //set t1 and t2 to current day
            t1 = new Timestamp(System.currentTimeMillis());
            t2 = new Timestamp(System.currentTimeMillis());
            //format t1 and t2 according to formatter
            time1 = String.format(t1.toString(), formatter);
            time2 = String.format(t2.toString(), formatter);
            //change time1 to keep the date and set time to 00:00:00
            time1 = time1.substring(0, 11) + "00:00:00";
            //change time2 to keep the date and set time to 23:59:59
            time2 = time2.substring(0, 11) + "23:59:59";

        } */

        Context context = getContext();
                getWorkPlan(context, formattedTime1, formattedTime2, new OnTasksRetrieved() {
                    @Override
                    public void getResult(ApiResponseReactionPlan result) {
                        if (result != null && result.isSuccess() && result.getData() != null) {
                            List<ManagerReactionWorkInPlan.ManagerReactionInPlanHeader> headers = result.getData().getManagerReactionInPlanHeaderList();


                            List<Task> items2 = new ArrayList<>();
                            for (ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header : headers) {
                                for (ManagerReactionWorkInPlan.ManagerReactionWork work : header.getManagerReactionWork()) {
                                    int workInPlanID = work.getReactionWorkID() != null ? work.getReactionWorkID() : 0;
                                    String workInPlanForCustomerName = work.getReactionWorkForCustomerName() != null ? work.getReactionWorkForCustomerName() : "";
                                    String workInPlanName = work.getReactionWorkActionName() != null ? work.getReactionWorkActionName() : "";
                                    String workInPlanNote = work.getReactionWorkNote() != null ? work.getReactionWorkNote() : "";

                                    Timestamp workInPlanTerm = parseTimestamp(work.getReactionWorkTerm());
                                    Timestamp workInPlanDone = parseTimestamp(work.getReactionWorkDone());

                                    // Use these timestamps as needed
                                    // For example, if a timestamp is null, you might want to use a default value
                                    if (workInPlanTerm == null) {
                                        workInPlanTerm = t1; // Use default timestamp t1
                                    }
                                    if (workInPlanDone == null) {
                                        workInPlanDone = t1; // Use default timestamp t1
                                    }

                                    //Timestamp workInPlanTerm = work.getReactionWorkTerm() != null ? Timestamp.valueOf(work.getReactionWorkTerm()) : t1;
                                    //Timestamp workInPlanDone = work.getReactionWorkDone() != null ? Timestamp.valueOf(work.getReactionWorkDone()) : t1;
                                    String workInPlanForCustomerID = work.getReactionWorkForCustomerID() != null ? String.valueOf(work.getReactionWorkForCustomerID()) : "";
                                    //get the order of the customer from header (getReactionByOrderNo())
                                    String workInPlanForCustomerOrder = header.getReactionByOrderNo() != null ? header.getReactionByOrderNo() : "";

                                    Task task = new Task(workInPlanID, workInPlanForCustomerName, workInPlanName, workInPlanNote, workInPlanTerm, workInPlanDone, workInPlanForCustomerID, workInPlanForCustomerOrder);
                                    items2.add(task);
                                }
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
            }


    public interface OnTasksRetrieved {
        void getResult(ApiResponseReactionPlan result);
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
        return null; // Return null if input is null or empty
    }


}



