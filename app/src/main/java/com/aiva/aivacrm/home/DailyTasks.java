package com.aiva.aivacrm.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.aiva.aivacrm.R;
import com.google.android.material.tabs.TabLayout;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import adapter.AdapterTasks;
import model.Task;
import network.GoogleCalendarServiceSingleton;
import network.UserSessionManager;

public class DailyTasks extends AppCompatActivity {

    private TextView monthYearText;
    private LocalDate selectedDate;
    private Button pickDate;
    private TabLayout weekdays;
    private Fragment fragment;
    private AdapterTasks adapterTasks;
    private List<Task> allTasks = new ArrayList<>();
    private List<Task> filteredTasks = new ArrayList<>();
    private String t1, t2;
    private GoogleCalendarServiceSingleton calendarServiceSingleton;
    private boolean tasksInitialized = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tasks);

        initComponents();
        selectedDate = LocalDate.now();
        getTimestamps();
        setMonthView();
        setTabDate(selectedDate);

        calendarServiceSingleton = new GoogleCalendarServiceSingleton(this);

        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(DailyTasks.this, view);
            popup.inflate(R.menu.menu_options);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_show_all:
                        // Show all tasks
                        filterTasks(TaskFilter.ALL);
                        return true;
                    case R.id.action_show_completed:
                        // Show completed tasks
                        filterTasks(TaskFilter.COMPLETED);
                        return true;
                    case R.id.action_show_pending:
                        // Show pending tasks
                        filterTasks(TaskFilter.PENDING);
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        });

        fragment = TasksTab.newInstance(t1, t2, selectedDate);
        getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();

        pickDate.setOnClickListener(view -> {
            pickDate();
            setMonthView();
            int dayOfWeek = selectedDate.getDayOfWeek().getValue();
            weekdays.getTabAt(dayOfWeek - 1).select();
        });
    }

    public void setAdapterTasks(AdapterTasks adapterTasks) {
        this.adapterTasks = adapterTasks;
        // Ensure allTasks is initially populated only once
        if (!tasksInitialized && adapterTasks != null) {
            allTasks = adapterTasks.getTasks();
            tasksInitialized = true;
            filterTasks(TaskFilter.PENDING); // Initial filter to show pending tasks
        }
    }

    private void filterTasks(TaskFilter filter) {
        TasksTab tasksTabFragment = (TasksTab) getSupportFragmentManager().findFragmentById(R.id.tabFragment);
        if (tasksTabFragment != null) {
            tasksTabFragment.refreshTasks(filter); // Trigger the API call to refresh tasks
        }
        if (allTasks == null || allTasks.isEmpty()) {
            Log.e(TAG, "Task list is empty or not initialized");
            return;
        }

        filteredTasks.clear();
        for (Task task : allTasks) {
            switch (filter) {
                case ALL:
                    filteredTasks.add(task);
                    break;
                case COMPLETED:
                    if ("1".equals(task.getWorkInPlanDone())) {
                        filteredTasks.add(task);
                    }
                    break;
                case PENDING:
                    if ("0".equals(task.getWorkInPlanDone())) {
                        filteredTasks.add(task);
                    }
                    break;
            }
        }

        Log.d(TAG, "Filtered tasks count: " + filteredTasks.size());
        if (filteredTasks.isEmpty()) {
            Log.w(TAG, "No tasks found for the selected filter");
        } else {
            for (Task t : filteredTasks) {
                Log.d(TAG, "Filtered task: " + t.toString());
            }
        }

        if (adapterTasks != null) {
            adapterTasks.updateTasks(filteredTasks);
        }
    }

    enum TaskFilter {
        ALL,
        COMPLETED,
        PENDING
    }

    private void initComponents() {
        pickDate = findViewById(R.id.pickDate);
        monthYearText = findViewById(R.id.monthYearTV);
        weekdays = findViewById(R.id.tab_layout);
        String employeeName = UserSessionManager.getEmployeeName(this);
        TextView employeeNameTV = findViewById(R.id.employeeNameTV);
        employeeNameTV.setText(employeeName);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
        return date.format(formatter);
    }

    private void setTabDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd");
        int dayOfWeek = selectedDate.getDayOfWeek().getValue();
        String s = "" + dayOfWeek;

        String s1 = this.getResources().getString(R.string.day1);
        String s2 = this.getResources().getString(R.string.day2);
        String s3 = this.getResources().getString(R.string.day3);
        String s4 = this.getResources().getString(R.string.day4);
        String s5 = this.getResources().getString(R.string.day5);
        String s6 = this.getResources().getString(R.string.day6);
        String s7 = this.getResources().getString(R.string.day7);
        String[] st = {s1, s2, s3, s4, s5, s6, s7};

        LocalDate tempDate = date;

        for (int i = dayOfWeek; i <= 7; i++) {
            TabLayout.Tab weekday = weekdays.getTabAt(i - 1);
            weekday.setText(st[i - 1] + "\n" + tempDate.format(formatter));
            tempDate = tempDate.plusDays(1);
        }
        tempDate = date;
        for (int i = dayOfWeek; i > 0; i--) {
            TabLayout.Tab weekday = weekdays.getTabAt(i - 1);
            weekday.setText(st[i - 1] + "\n" + tempDate.format(formatter));
            tempDate = tempDate.minusDays(1);
        }
        dayOfWeek = selectedDate.getDayOfWeek().getValue();
        weekdays.getTabAt(dayOfWeek - 1).select();

        weekdays.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int prevDayOfWeek = selectedDate.getDayOfWeek().getValue();
                int selDayOfWeek = tab.getPosition() + 1;
                if (selDayOfWeek > prevDayOfWeek) {
                    selectedDate = selectedDate.plusDays(selDayOfWeek - prevDayOfWeek);
                } else if (selDayOfWeek < prevDayOfWeek) {
                    selectedDate = selectedDate.minusDays(prevDayOfWeek - selDayOfWeek);
                }
                setMonthView();
                getTimestamps();
                getSupportFragmentManager().beginTransaction().detach(fragment).commit();
                fragment = TasksTab.newInstance(t1, t2, selectedDate);
                getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void pickDate() {
        int mYear = selectedDate.getYear();
        int mMonth = selectedDate.getMonthValue() - 1; // Subtract 1 to match Calendar.MONTH indexing
        int mDay = selectedDate.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    setMonthView();
                    setTabDate(selectedDate);
                    getSupportFragmentManager().beginTransaction().detach(fragment).commit();
                    fragment = TasksTab.newInstance(t1, t2, selectedDate);
                    getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void getTimestamps() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        t1 = selectedDate.atStartOfDay().toString().format(String.valueOf(formatter));
        t2 = selectedDate.plusDays(1).atStartOfDay().toString().format(String.valueOf(formatter));
    }
}
