package com.aiva.aivacrm.home;

import static data.GetTasks.getTasksData;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aiva.aivacrm.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import adapter.AdapterTasks;
import adapter.ItemAnimation;
import model.Task;

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
    List<Task> items = new ArrayList<>();
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
        }

        scheduleCall();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        setAdapter(view);
        view2= view;

        return view;
    }

    private void setAdapter(View view) {

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterTasks(this, items, animation_type, t1, t2);
        recyclerView.setAdapter(mAdapter);

    }

    private void scheduleCall(){
        getTasksData(
                new OnTasksRetrieved() {
                    @Override
                    public void getResult(List<Task> result) {
                        // The code in here runs much later in the future - the adapter
                        // will already have been set up, but will be empty.
                        List<Task> items2 = new ArrayList<>();

                        t1 = Timestamp.valueOf(time1);
                        t2 = Timestamp.valueOf(time2);
                        for(Task t:result){
                            if(t.AtlikData.after(t1)&&t.AtlikData.before(t2)) {
                                items2.add(t);
                            }
                        }

                        //sort items2 based on hours
                        for(int i=0;i<items2.size();i++){
                            for(int j=i+1;j<items2.size();j++){
                                if(items2.get(i).AtlikData.after(items2.get(j).AtlikData)){
                                    Task temp = items2.get(i);
                                    items2.set(i,items2.get(j));
                                    items2.set(j,temp);
                                }
                            }
                        }

                        AdapterTasks.setItems(items2);
                        setAdapter(view2);// add this method to the adapter
                    }
                }
        );
    }

    public interface OnTasksRetrieved {
        void getResult(List<Task> result);
    }
}