package com.aiva.aivacrm.home;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static data.GetTasks.getActionsInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import adapter.AdapterTasks;
import adapter.ItemAnimation;
import data.GetTasks;
import model.CRMWork;
import model.Task;
import network.FetchCalendarEventsTask;
import network.GoogleCalendarServiceSingleton;
import network.UserSessionManager;
import network.api_request_model.ApiResponseReactionPlan;
import network.api_request_model.ManagerReactionWorkInPlan;
import network.api_response.CRMWorkResponse;

public class TasksTab extends Fragment {
    private boolean isApiCallInProgress = false;
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
    private String currentEmployeeID;
    private List<Integer> dateOnlyActionIds = new ArrayList<>();

    private ActivityResultLauncher<Intent> taskInfoLauncher;

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

        taskInfoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Handle the result, refresh tasks
                        refreshTasks(DailyTasks.TaskFilter.ALL);
                    }
                }
        );

        GoogleCalendarServiceSingleton.getInstance(getContext()); // Initialize without API key
        if (getArguments() != null) {
            time1 = getArguments().getString(ARG_PARAM1);
            time2 = getArguments().getString(ARG_PARAM2);
            selectedDate = (LocalDate) getArguments().getSerializable("selectedDate");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                LocalDateTime date1 = LocalDateTime.parse(time1, formatter);
                LocalDateTime date2 = LocalDateTime.parse(time2, formatter);
                t1 = Timestamp.valueOf(String.valueOf(date1));
                t2 = Timestamp.valueOf(String.valueOf(date2));
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }
        calendarServiceSingleton = GoogleCalendarServiceSingleton.getInstance(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        setAdapter(view);
        view2 = view;
        currentEmployeeID = UserSessionManager.getEmployeeId(getContext());
        fetchActionInfo(); // Initial call to fetch action info and then tasks
        return view;
    }

    private void setAdapter(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterTasks(getContext(), new ArrayList<>(), task -> {
            Intent taskInfoIntent = new Intent(getContext(), TaskInfoActivity.class);
            taskInfoIntent.putExtra("Task", task);
            taskInfoLauncher.launch(taskInfoIntent);
        }, false, false); // Do not show full date and time, isTaskListActivity = false

        Log.d("TasksTab", "Setting adapter with OnTaskItemClickListener");
        recyclerView.setAdapter(mAdapter);

        mAdapter.setDateOnlyActionIds(dateOnlyActionIds);

        if (getActivity() instanceof DailyTasks) {
            ((DailyTasks) getActivity()).setAdapterTasks(mAdapter);
        }
    }

    public void updateTasks(List<Task> tasks) {
        if (mAdapter != null) {
            mAdapter.setItems(tasks);
            mAdapter.notifyDataSetChanged();
        }
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
            Timestamp workInPlanTerm = startTime == null ? null : new Timestamp(startTime.getValue());
            Timestamp workInPlanDoneDate = endTime == null ? null : new Timestamp(endTime.getValue());
            String workInPlanOrderID = "";

            // Example of how you might set representative details
            String repName = "John";
            String repSurname = "Doe";
            String repPhone = "1234567890";
            String repEmail = "john.doe@example.com";

            //Task task = new Task(ID, workInPlanForCustomerName, workInPlanName, workInPlanNote, workInPlanTerm, workInPlanDoneDate, "0", workInPlanOrderID, repName, repSurname, repPhone, repEmail);
            //tasks.add(task);
        }
        return tasks;
    }

    private void updateAdapter(List<Task> tasks) {
        // Sort tasks based on workInPlanTerm
        Collections.sort(tasks, Comparator.comparing(Task::getWorkInPlanTerm));

        mAdapter.setItems(tasks);
        mAdapter.notifyDataSetChanged();
    }

    private void fetchActionInfo(){
        getActionsInfo(getContext(), result -> {
            if (result != null && result.isSuccess()) {
                List<CRMWork> crmWorkList = result.getData().getCrmWorkList();
                if (crmWorkList != null) {
                    for (CRMWork work : crmWorkList) {
                        if ("yyyy.mm.dd".equals(work.getCRMWorkFormat())) {
                            dateOnlyActionIds.add(work.getCRMWorkID());
                        }
                    }
                    mAdapter.setDateOnlyActionIds(dateOnlyActionIds);  // Pass the IDs to the adapter
                    Log.d(TAG, "Date only action IDs: " + dateOnlyActionIds);
                }
            }
            // After fetching the action info, fetch tasks
            scheduleCall(DailyTasks.TaskFilter.PENDING);
        });
    }

    public void refreshTasks(DailyTasks.TaskFilter filter) {
        scheduleCall(filter);
    }

    private void scheduleCall(DailyTasks.TaskFilter filter) {
        Log.d(TAG, "Starting scheduleCall with filter: " + filter);
        if (isApiCallInProgress) return;
        isApiCallInProgress = true;
        String currentEmployeeID = UserSessionManager.getEmployeeId(getContext());

        LocalDateTime startOfDay = selectedDate.atStartOfDay();
        LocalDateTime endOfDay = selectedDate.plusDays(1).atStartOfDay();

        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

        t1 = new Timestamp(startDate.getTime());
        t2 = new Timestamp(endDate.getTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime1 = dateFormat.format(t1);
        String formattedTime2 = dateFormat.format(t2);

        Log.d(TAG, "Formatted Time1: " + formattedTime1 + ", Time2: " + formattedTime2);

        DateTime startTime = new DateTime(startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        DateTime endTime = new DateTime(endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        // Clear previous items before fetching new data
        itemsFromGoogleCalendar.clear();
        itemsFromDB.clear();
        adapterItems.clear();

        if (UserSessionManager.userSignedIn) {
            Log.d(TAG, "Fetching events from Google Calendar");
            new FetchCalendarEventsTask(getContext(), startTime, endTime, calendarServiceSingleton,
                    new FetchCalendarEventsTask.OnCalendarEventsFetchedListener() {
                        @Override
                        public void onCalendarEventsFetched(List<Event> events) {
                            if (events != null) {
                                Log.d(TAG, "Fetched " + events.size() + " events from Google Calendar");
                                itemsFromGoogleCalendar = convertEventsToTasks(events);
                                adapterItems.addAll(itemsFromGoogleCalendar);
                            } else {
                                Toast.makeText(getContext(), "Failed to fetch events from Google Calendar", Toast.LENGTH_SHORT).show();
                            }
                            fetchDataFromDbAndCombine(adapterItems, filter, formattedTime1, formattedTime2);
                        }
                    }).execute();
        } else {
            fetchDataFromDbAndCombine(adapterItems, filter, formattedTime1, formattedTime2);
        }
    }

    private void fetchDataFromDbAndCombine(List<Task> adapterItems, DailyTasks.TaskFilter filter, String formattedTime1, String formattedTime2) {
        Context context = getContext();
        Log.d(TAG, "Fetching tasks from database");
        GetTasks.getWorkPlan(context, "", "", formattedTime1, formattedTime2, result -> {
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        List<ManagerReactionWorkInPlan.ManagerReactionInPlanHeader> headers = result.getData().getManagerReactionInPlanHeaderList();
                        for (ManagerReactionWorkInPlan.ManagerReactionInPlanHeader header : headers) {
                            for (ManagerReactionWorkInPlan.ManagerReactionWork work : header.getManagerReactionWork()) {
                                String reactionWorkManagerID = work.getReactionWorkManagerID() != null ? String.valueOf(work.getReactionWorkManagerID()) : "";

                                if (reactionWorkManagerID.equals(UserSessionManager.getEmployeeId(getContext()))) {
                                    String reactionHeaderID = header.getReactionHeaderID() != null ? header.getReactionHeaderID() : "";
                                    String reactionHeaderManagerID = header.getReactionManagerID() != null ? header.getReactionManagerID() : "";
                                    String workInPlanForCustomerID = work.getReactionWorkForCustomerID() != null ? String.valueOf(work.getReactionWorkForCustomerID()) : "";
                                    if (workInPlanForCustomerID.equals("null") || workInPlanForCustomerID.equals("0") || workInPlanForCustomerID.equals(""))
                                        workInPlanForCustomerID = header.getReactionForCustomerID();
                                    String workInPlanForCustomerOrder = header.getReactionByOrderNo() != null ? header.getReactionByOrderNo() : "";

                                    int workInPlanID = work.getReactionWorkID() != null ? work.getReactionWorkID() : 0;
                                    String managerName = work.getReactionWorkManageName() != null ? work.getReactionWorkManageName() : "";
                                    String reactionWorkActionID = work.getReactionWorkActionID() != null ? String.valueOf(work.getReactionWorkActionID()) : "";
                                    int tempActionID = Integer.parseInt(reactionWorkActionID);

                                    boolean isDateOnlyAction = dateOnlyActionIds.contains(tempActionID);

                                    String workInPlanName = work.getReactionWorkActionName() != null ? work.getReactionWorkActionName() : "";
                                    String workInPlanNote = work.getReactionWorkNote() != null ? work.getReactionWorkNote() : "";
                                    String workInPlanForCustomerName = work.getReactionWorkForCustomerName() != null ? work.getReactionWorkForCustomerName() : "";

                                    Timestamp workInPlanTerm = parseTimestamp(work.getReactionWorkTerm());
                                    Timestamp workInPlanDoneDate = parseTimestamp(work.getReactionWorkDoneDate());
                                    String workInPlanDone = work.getReactionWorkDone() != null ? work.getReactionWorkDone() : "";

                                    // Only include tasks whose term falls within the selected day
                                    if (workInPlanTerm != null && convertToLocalDate(workInPlanTerm).equals(selectedDate)) {
                                        Task task = new Task(reactionHeaderID, reactionHeaderManagerID, workInPlanForCustomerID, workInPlanForCustomerOrder, workInPlanID, reactionWorkManagerID, managerName, reactionWorkActionID, workInPlanName, workInPlanNote, workInPlanForCustomerName, workInPlanTerm, workInPlanDoneDate, workInPlanDone, isDateOnlyAction);
                                        itemsFromDB.add(task);
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "Fetched " + itemsFromDB.size() + " tasks from database");
                        adapterItems.addAll(itemsFromDB);
                        updateAdapterWithFilter(adapterItems, filter);
                        if (getActivity() instanceof DailyTasks) {
                            ((DailyTasks) getActivity()).setAdapterTasks(mAdapter);
                        }
                    } else {
                        Log.e("WorkPlan", "No data found in response or response is not successful.");
                    }
                    isApiCallInProgress = false;
                });
            }
        });
    }

    private LocalDate convertToLocalDate(Timestamp timestamp) {
        return timestamp.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        return timestamp.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private void updateAdapterWithFilter(List<Task> tasks, DailyTasks.TaskFilter filter) {
        Log.d(TAG, "Updating adapter with filter: " + filter);
        List<Task> filteredTasks = new ArrayList<>();

        List<Task> tasksWithoutTime = new ArrayList<>();
        List<Task> tasksWithTime = new ArrayList<>();

        for (Task task : tasks) {
            switch (filter) {
                case ALL:
                    if ("0".equals(task.getWorkInPlanDone()) ||
                            ("1".equals(task.getWorkInPlanDone()) && convertToLocalDateTime(task.getWorkInPlanDoneDate()).toLocalDate().equals(selectedDate))) {
                        if (task.isDateOnlyAction() || isTimeNotSet(task.getWorkInPlanTerm())) {
                            tasksWithoutTime.add(task);
                        } else {
                            tasksWithTime.add(task);
                        }
                    }
                    break;
                case COMPLETED:
                    if ("1".equals(task.getWorkInPlanDone()) && convertToLocalDateTime(task.getWorkInPlanDoneDate()).toLocalDate().equals(selectedDate)) {
                        if (task.isDateOnlyAction() || isTimeNotSet(task.getWorkInPlanTerm())) {
                            tasksWithoutTime.add(task);
                        } else {
                            tasksWithTime.add(task);
                        }
                    }
                    break;
                case PENDING:
                    if ("0".equals(task.getWorkInPlanDone())) {
                        if (task.isDateOnlyAction() || isTimeNotSet(task.getWorkInPlanTerm())) {
                            tasksWithoutTime.add(task);
                        } else {
                            tasksWithTime.add(task);
                        }
                    }
                    break;
            }
        }

        Collections.sort(tasksWithTime, Comparator.comparing(Task::getWorkInPlanTerm));

        List<Task> finalTasks = new ArrayList<>();
        finalTasks.addAll(tasksWithoutTime);
        finalTasks.addAll(tasksWithTime);

        mAdapter.setItems(finalTasks);
        mAdapter.notifyDataSetChanged();
    }

    private boolean isTimeNotSet(Timestamp timestamp) {
        if (timestamp == null) {
            return true;
        }
        LocalDateTime localDateTime = convertToLocalDateTime(timestamp);
        return localDateTime.toLocalTime().equals(LocalDateTime.MIN.toLocalTime());
    }

    public interface OnTasksRetrieved {
        void getResult(ApiResponseReactionPlan result);
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

    public interface OnActionsInfoRetrieved {
        void getResult(CRMWorkResponse result);
    }
}
