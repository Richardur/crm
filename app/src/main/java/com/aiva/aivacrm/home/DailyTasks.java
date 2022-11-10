package com.aiva.aivacrm.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.content.DialogInterface;
import com.aiva.aivacrm.R;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.AdapterTasks;
import adapter.ItemAnimation;
import model.Task;

public class DailyTasks extends AppCompatActivity {

    private TextView monthYearText;
    private LocalDate selectedDate;
    private TabLayout weekdays;
    private Button pickDate;

    List<Task> items = new ArrayList<>();
    List<Integer> date = new ArrayList<>(); //date list
    private RecyclerView recyclerView;
    private AdapterTasks mAdapter;
    private int animation_type = ItemAnimation.FADE_IN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tasks);

        initComponents();
        selectedDate = LocalDate.now();
        //convert selected date to list (y,m,d)

        date.add(selectedDate.getYear());
        date.add(selectedDate.getMonthValue());
        date.add(selectedDate.getDayOfMonth());

        setMonthView();
        setTabDate(selectedDate);

        //pick date
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickDate();
                setMonthView();
                //get day of week of selected date
                int dayOfWeek = selectedDate.getDayOfWeek().getValue();
                weekdays.getTabAt(dayOfWeek-1).select();

            }
        });
    }

    private void initComponents(){
        pickDate = findViewById(R.id.pickDate);
        monthYearText = findViewById(R.id.monthYearTV);
        weekdays = findViewById(R.id.tab_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;


        setAdapter(date);
    }

    private void setAdapter(List<Integer> a) {


        mAdapter = new AdapterTasks(this, items, animation_type, a);
        recyclerView.setAdapter(mAdapter);

    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
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
        dayOfWeek = selectedDate.getDayOfWeek().getValue();
        weekdays.getTabAt(dayOfWeek-1).select();


    }
    //on click of PickDate create a calendar popup  to pick date and set the date    to the selected date   and set the month view and tab date

    private void pickDate(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        setMonthView();
                        setTabDate(selectedDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();


    }



}