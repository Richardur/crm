package com.aiva.aivacrm.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.aiva.aivacrm.R;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DailyTasks extends AppCompatActivity {

    private TextView monthYearText;
    private LocalDate selectedDate;
    private TabLayout weekdays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tasks);

        initMonthView();
        selectedDate = LocalDate.now();
        setMonthView();
        setTabDate(selectedDate);
    }

    private void initMonthView(){
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    private void setTabDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd");

        int dayOfWeek = selectedDate.getDayOfWeek().getValue();
        String s = ""+dayOfWeek;

        String s1 = this.getResources().getString(R.string.day1);
        String s2 = this.getResources().getString(R.string.day2);
        String s3 = this.getResources().getString(R.string.day3);
        String s4 = this.getResources().getString(R.string.day4);
        String s5 = this.getResources().getString(R.string.day5);
        String s6 = this.getResources().getString(R.string.day6);
        String s7 = this.getResources().getString(R.string.day7);
        String[] st = {s1,s2,s3,s4,s5,s6,s7};

        //monthYearText.setText(s1);
        weekdays = findViewById(R.id.tab_layout);
        LocalDate tempDate = date;

        for(int i = dayOfWeek; i<=7; i++){
            TabLayout.Tab weekday = weekdays.getTabAt(i-1);
            weekday.setText(st[i - 1] + "\n" + tempDate.format(formatter));
            tempDate = tempDate.plusDays(1);
        }
        tempDate = date;
        for(int i = dayOfWeek; i>0; i--){
            TabLayout.Tab weekday = weekdays.getTabAt(i-1);
            weekday.setText(st[i - 1] + "\n" + tempDate.format(formatter));
            tempDate = tempDate.minusDays(1);
        }

    }

}