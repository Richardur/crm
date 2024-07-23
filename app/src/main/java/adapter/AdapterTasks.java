package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aiva.aivacrm.R;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import model.Task;

public class AdapterTasks extends RecyclerView.Adapter<AdapterTasks.ViewHolder> {

    private Context context;
    private List<Task> taskList;
    private OnTaskItemClickListener listener;

    public AdapterTasks(Context context, List<Task> taskList, OnTaskItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // Method to set the new data
    public void setTaskList(List<Task> newTaskList) {
        taskList.clear(); // Clear the existing data
        taskList.addAll(newTaskList); // Add the new data
        notifyDataSetChanged(); // Notify the adapter of the data change
    }

    public void updateTasks(List<Task> newTaskList) {
        taskList.clear();
        taskList.addAll(newTaskList);
        notifyDataSetChanged();
    }

    public void setItems(List<Task> items) {
        setTaskList(items);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView workInPlanForCutomerName;
        private TextView workInPlanName;
        private TextView workInPlanNote;
        private TextView workInPlanTerm1, workInPlanTerm2;
        private TextView workInPlanDone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            workInPlanForCutomerName = itemView.findViewById(R.id.klientas);
            workInPlanName = itemView.findViewById(R.id.darbas);
            workInPlanNote = itemView.findViewById(R.id.komentaras);
            workInPlanTerm1 = itemView.findViewById(R.id.hours);
            workInPlanTerm2 = itemView.findViewById(R.id.minutes);
            workInPlanDone = itemView.findViewById(R.id.atlikta);
        }

        public void bind(final Task task, final OnTaskItemClickListener listener) {
            workInPlanForCutomerName.setText(task.getWorkInPlanForCustomerName());
            workInPlanName.setText(task.getWorkInPlanName());
            workInPlanNote.setText(task.getWorkInPlanNote());

            java.util.Date date = new java.util.Date(task.getWorkInPlanTerm().getTime());
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            int hours = localDateTime.getHour();
            String hoursString = String.valueOf(hours);
            int minutes = localDateTime.getMinute();
            String minutesString = String.valueOf(minutes);
            if (minutesString.length() == 1) {
                minutesString = "0" + minutesString;
            }

            workInPlanTerm1.setText(hoursString);
            workInPlanTerm2.setText(minutesString);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("AdapterTasks", "Task clicked for task ID: " + task.getWorkInPlanID());
                    listener.onTaskItemClick(task);
                }
            });

            workInPlanDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("AdapterTasks", "Checkbox clicked for task ID: " + task.getWorkInPlanID());
                    // Checkbox click handling code, if any
                }
            });
        }
    }

    public interface OnTaskItemClickListener {
        void onTaskItemClick(Task task);
    }
}
