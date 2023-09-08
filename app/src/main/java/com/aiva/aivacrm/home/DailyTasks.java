package com.aiva.aivacrm.home;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aiva.aivacrm.R;
import com.aiva.aivacrm.home.TasksTab;
import com.google.android.material.tabs.TabLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.AdapterTasks;
import model.Task;

public class DailyTasks extends AppCompatActivity {

    private TextView monthYearText;
    private LocalDate selectedDate;
    private Button pickDate;
    private TasksTab tasksTab;
    private TabLayout weekdays;
    private Fragment fragment;
    private RecyclerView recyclerView;
    private AdapterTasks adapterTasks;
    private List<Task> taskList = new ArrayList<>();
    private String t1, t2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tasks);

        initComponents();
        selectedDate = LocalDate.now();
        getTimestamps();
        setMonthView();
        setTabDate(selectedDate);


        fragment = TasksTab.newInstance(t1, t2);

        getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
                setMonthView();
                int dayOfWeek = selectedDate.getDayOfWeek().getValue();
                weekdays.getTabAt(dayOfWeek - 1).select();
            }
        });

    }


    private void initComponents() {
        pickDate = findViewById(R.id.pickDate);
        monthYearText = findViewById(R.id.monthYearTV);
        weekdays = findViewById(R.id.tab_layout);
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
                fragment = TasksTab.newInstance(t1, t2);
                getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void pickDate() {
        int mYear = selectedDate.getYear();
        int mMonth = selectedDate.getMonthValue() - 1; // Subtract 1 to match Calendar.MONTH indexing
        int mDay = selectedDate.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        setMonthView();
                        setTabDate(selectedDate);
                        getTimestamps();
                        getSupportFragmentManager().beginTransaction().detach(fragment).commit();
                        fragment = TasksTab.newInstance(t1, t2);
                        getSupportFragmentManager().beginTransaction().replace(R.id.tabFragment, fragment).commit();
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void getTimestamps() {
        t1 = selectedDate.atStartOfDay().toString();
        t2 = selectedDate.plusDays(1).atStartOfDay().toString();
    }
}
