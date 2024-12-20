package com.aiva.aivacrm.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.aiva.aivacrm.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import network.UserSessionManager;

public class DailyTasks extends AppCompatActivity {

    private TextView monthYearText;
    private LocalDate selectedDate;
    private Button pickDate;
    private TabLayout weekdays;
    private Fragment fragment;
    private String t1, t2;
    private AssignmentFilter assignmentFilter = AssignmentFilter.ASSIGNED_TO_ME; // Default
    private StatusFilter statusFilter = StatusFilter.ALL_STATUS; // Default

    private MaterialButton buttonToday;
    private MaterialButton buttonStatusFilter;
    private MaterialButton buttonAssignmentFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "lt"); // Default to "lt" if not set
        setLocale(language);


        setContentView(R.layout.activity_daily_tasks);

        initComponents();
        selectedDate = LocalDate.now();
        getTimestamps();
        setMonthView();
        setTabDate(selectedDate);


        ImageButton menuButton = findViewById(R.id.menu_button);

        buttonToday = findViewById(R.id.button_today);
        buttonStatusFilter = findViewById(R.id.button_status_filter);
        buttonAssignmentFilter = findViewById(R.id.button_assignment_filter);

        // Set initial text for the filter buttons
        buttonAssignmentFilter.setText(getAssignmentFilterText(assignmentFilter));
        buttonStatusFilter.setText(getStatusFilterText(statusFilter));

        // Set click listeners for the buttons
        buttonToday.setOnClickListener(v -> {
            selectedDate = LocalDate.now();
            setMonthView();
            setTabDate(selectedDate);
            // Update the fragment
            getTimestamps();
            getSupportFragmentManager().beginTransaction().detach(fragment).commit();
            fragment = TasksTab.newInstance(t1, t2, selectedDate, assignmentFilter, statusFilter);
            getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();
        });

        buttonStatusFilter.setOnClickListener(v -> showStatusFilterPopup());

        buttonAssignmentFilter.setOnClickListener(v -> showAssignmentFilterPopup());

        menuButton.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(DailyTasks.this, view);
            popup.inflate(R.menu.sign_out_options); // Keep this for sign-out and language options
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_sign_out:
                        signOut();
                        return true;
                    case R.id.action_language_switch:
                        switchLanguage();
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        });

        fragment = TasksTab.newInstance(t1, t2, selectedDate, assignmentFilter, statusFilter);
        getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();

        pickDate.setOnClickListener(view -> {
            pickDate();
            setMonthView();
            int dayOfWeek = selectedDate.getDayOfWeek().getValue();
            weekdays.getTabAt(dayOfWeek - 1).select();
        });
    }

    // Helper methods to get filter text
    private String getAssignmentFilterText(AssignmentFilter filter) {
        switch (filter) {
            case ASSIGNED_TO_ME:
                return getString(R.string.assigned_to_me);
            case ASSIGNED_BY_ME:
                return getString(R.string.assigned_by_me);
            case ALL_ASSIGNMENT:
                return getString(R.string.all_assignments);
            default:
                return "";
        }
    }
    private String getStatusFilterText(StatusFilter filter) {
        switch (filter) {
            case ALL_STATUS:
                return getString(R.string.all_status);
            case COMPLETED:
                return getString(R.string.completed);
            case PENDING:
                return getString(R.string.pending);
            default:
                return "";
        }
    }



    
    // Update showAssignmentFilterPopup method
    private void showAssignmentFilterPopup() {
        PopupMenu popup = new PopupMenu(DailyTasks.this, buttonAssignmentFilter);
        popup.getMenu().add(getString(R.string.assigned_to_me));
        popup.getMenu().add(getString(R.string.assigned_by_me));
        popup.getMenu().add(getString(R.string.all_assignments));

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals(getString(R.string.assigned_to_me))) {
                assignmentFilter = AssignmentFilter.ASSIGNED_TO_ME;
            } else if (title.equals(getString(R.string.assigned_by_me))) {
                assignmentFilter = AssignmentFilter.ASSIGNED_BY_ME;
            } else {
                assignmentFilter = AssignmentFilter.ALL_ASSIGNMENT;
            }

            // Update the button text
            buttonAssignmentFilter.setText(title);

            // Update the fragment
            TasksTab tasksTabFragment = (TasksTab) getSupportFragmentManager().findFragmentById(R.id.tabFragment);
            if (tasksTabFragment != null) {
                tasksTabFragment.setAssignmentFilter(assignmentFilter);
                tasksTabFragment.refreshTasks();
            }

            return true;
        });

        popup.show();
    }


    // Update showStatusFilterPopup method
    private void showStatusFilterPopup() {
        PopupMenu popup = new PopupMenu(DailyTasks.this, buttonStatusFilter);
        popup.getMenu().add(getString(R.string.all_status));
        popup.getMenu().add(getString(R.string.completed));
        popup.getMenu().add(getString(R.string.pending));

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals(getString(R.string.all_status))) {
                statusFilter = StatusFilter.ALL_STATUS;
            } else if (title.equals(getString(R.string.completed))) {
                statusFilter = StatusFilter.COMPLETED;
            } else if (title.equals(getString(R.string.pending))) {
                statusFilter = StatusFilter.PENDING;
            }

            // Update the button text
            buttonStatusFilter.setText(title);

            // Update the fragment
            TasksTab tasksTabFragment = (TasksTab) getSupportFragmentManager().findFragmentById(R.id.tabFragment);
            if (tasksTabFragment != null) {
                tasksTabFragment.setStatusFilter(statusFilter);
                tasksTabFragment.refreshTasks();
            }

            return true;
        });
        popup.show();
    }

    private void signOut() {
        UserSessionManager.clearSession(this, true);
        Intent intent = new Intent(DailyTasks.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void switchLanguage() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String currentLanguage = prefs.getString("My_Lang", "lt");
        String newLanguage = currentLanguage.equals("en") ? "lt" : "en";

        setLocale(newLanguage);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("My_Lang", newLanguage);
        editor.apply();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public enum AssignmentFilter {
        ALL_ASSIGNMENT,
        ASSIGNED_TO_ME,
        ASSIGNED_BY_ME
    }

    public enum StatusFilter {
        ALL_STATUS,
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
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "lt"); // Default to "lt" if not set
        Locale locale = language.equals("en") ? Locale.ENGLISH : Locale.forLanguageTag(language);

        DateTimeFormatter formatter;
        if (language.equals("lt")) {
            formatter = DateTimeFormatter.ofPattern("MMMM d yyyy", locale);
        } else {
            formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", locale);
        }

        String formattedDate = date.format(formatter);
        String[] parts = formattedDate.split(" ");
        String day = parts[1];
        if (day.startsWith("0")) {
            day = day.substring(1);
        }
        String month = parts[0].substring(0, 1).toUpperCase(locale) + parts[0].substring(1);

        if (language.equals("lt")) {
            return month + " " + day + "d., " + parts[2];
        } else {
            return month + " " + day + " " + parts[2];
        }
    }

    private void setTabDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd");
        int dayOfWeek = selectedDate.getDayOfWeek().getValue();

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
            if (weekday != null) {
                weekday.setText(st[i - 1] + "\n" + tempDate.format(formatter));
            }
            tempDate = tempDate.plusDays(1);
        }
        tempDate = date;
        for (int i = dayOfWeek; i > 0; i--) {
            TabLayout.Tab weekday = weekdays.getTabAt(i - 1);
            if (weekday != null) {
                weekday.setText(st[i - 1] + "\n" + tempDate.format(formatter));
            }
            tempDate = tempDate.minusDays(1);
        }
        dayOfWeek = selectedDate.getDayOfWeek().getValue();
        if (weekdays.getTabAt(dayOfWeek - 1) != null) {
            weekdays.getTabAt(dayOfWeek - 1).select();
        }

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
                fragment = TasksTab.newInstance(t1, t2, selectedDate, assignmentFilter, statusFilter);
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
        int mMonth = selectedDate.getMonthValue() - 1;
        int mDay = selectedDate.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CustomDatePickerDialogTheme,
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    setMonthView();
                    setTabDate(selectedDate);
                    getSupportFragmentManager().beginTransaction().detach(fragment).commit();
                    fragment = TasksTab.newInstance(t1, t2, selectedDate, assignmentFilter, statusFilter);
                    getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void getTimestamps() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        t1 = selectedDate.atStartOfDay().format(formatter);
        t2 = selectedDate.plusDays(1).atStartOfDay().format(formatter);
    }

}
