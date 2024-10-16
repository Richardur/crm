package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aiva.aivacrm.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import model.Task;

public class AdapterTasks extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DATE_ONLY = 0;
    private static final int VIEW_TYPE_WITH_TIME = 1;

    private Context context;
    private List<Task> taskList;
    private OnTaskItemClickListener listener;
    private List<Integer> dateOnlyActionIds;
    private boolean showFullDateTime;
    private boolean isTaskListActivity;

    public AdapterTasks(Context context, List<Task> taskList, OnTaskItemClickListener listener, boolean showFullDateTime, boolean isTaskListActivity) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
        this.dateOnlyActionIds = new ArrayList<>();
        this.showFullDateTime = showFullDateTime;
        this.isTaskListActivity = isTaskListActivity;
    }

    public void setDateOnlyActionIds(List<Integer> dateOnlyActionIds) {
        this.dateOnlyActionIds = dateOnlyActionIds;
    }

    @Override
    public int getItemViewType(int position) {
        Task task = taskList.get(position);
        if (isTaskListActivity) {
            return VIEW_TYPE_WITH_TIME;
        } else if (task.isDateOnlyAction() || isTimeNotSet(task.getWorkInPlanTerm())) {
            return VIEW_TYPE_DATE_ONLY;
        } else {
            return VIEW_TYPE_WITH_TIME;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_DATE_ONLY && !isTaskListActivity) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new DateOnlyViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_with_date, parent, false);
            return new WithTimeViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Task task = taskList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_DATE_ONLY && !isTaskListActivity) {
            ((DateOnlyViewHolder) holder).bind(task, listener);
        } else {
            ((WithTimeViewHolder) holder).bind(task, listener, showFullDateTime, isTaskListActivity);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTaskList(List<Task> newTaskList) {
        taskList.clear();
        taskList.addAll(newTaskList);
        notifyDataSetChanged();
    }

    public void updateTasks(List<Task> newTaskList) {
        taskList.clear();
        taskList.addAll(newTaskList);
        notifyDataSetChanged();
    }

    public void setItems(List<Task> items) {
        // Sort the tasks, putting non-date-only (timed) tasks first, and date-only tasks at the bottom
        Collections.sort(items, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                boolean isTask1DateOnly = task1.isDateOnlyAction();
                boolean isTask2DateOnly = task2.isDateOnlyAction();

                // If task1 is date-only and task2 is not, task2 should come first
                if (isTask1DateOnly && !isTask2DateOnly) {
                    return 1; // Task2 (not date-only) comes before Task1 (date-only)
                }
                // If task2 is date-only and task1 is not, task1 should come first
                else if (!isTask1DateOnly && isTask2DateOnly) {
                    return -1; // Task1 (not date-only) comes before Task2 (date-only)
                }
                // If both tasks are the same type (both date-only or both not), retain their order
                else {
                    return 0; // Keep the original order
                }
            }
        });

        // Update the list in the adapter
        setTaskList(items);
        notifyDataSetChanged();
    }

    public List<Task> getTasks() {
        return taskList;
    }

    public class DateOnlyViewHolder extends RecyclerView.ViewHolder {

        private TextView klientas;
        private TextView darbas;
        private TextView komentaras;
        private ImageView atliktaIcon;
        private ImageView noTimeIcon;

        public DateOnlyViewHolder(@NonNull View itemView) {
            super(itemView);
            klientas = itemView.findViewById(R.id.klientas);
            darbas = itemView.findViewById(R.id.darbas);
            komentaras = itemView.findViewById(R.id.komentaras);
            noTimeIcon = itemView.findViewById(R.id.no_time_icon);
            atliktaIcon = itemView.findViewById(R.id.atlikta);
        }

        public void bind(final Task task, final OnTaskItemClickListener listener) {
            klientas.setText(task.getWorkInPlanForCustomerName());
            darbas.setText(task.getWorkInPlanName());
            komentaras.setText(task.getWorkInPlanNote());
            noTimeIcon.setVisibility(View.VISIBLE);

            setCompletionIcon(atliktaIcon, task.getWorkInPlanDone());

            itemView.setOnClickListener(v -> listener.onTaskItemClick(task));
        }
    }

    public class WithTimeViewHolder extends RecyclerView.ViewHolder {
        private TextView klientas;
        private TextView darbas;
        private TextView komentaras;
        private TextView date;
        private TextView hours;
        private TextView minutes;
        private ImageView atliktaIcon;
        private LinearLayout timeLayout;

        public WithTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            klientas = itemView.findViewById(R.id.klientas);
            darbas = itemView.findViewById(R.id.darbas);
            komentaras = itemView.findViewById(R.id.komentaras);
            date = itemView.findViewById(R.id.date);
            hours = itemView.findViewById(R.id.hours);
            minutes = itemView.findViewById(R.id.minutes);
            atliktaIcon = itemView.findViewById(R.id.atlikta_icon);
            timeLayout = itemView.findViewById(R.id.time_layout);
        }

        public void bind(final Task task, final OnTaskItemClickListener listener, boolean showFullDateTime, boolean isTaskListActivity) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String dateStr = dateFormat.format(task.getWorkInPlanTerm());
            String timeStr = timeFormat.format(task.getWorkInPlanTerm());

            date.setVisibility(View.VISIBLE);
            date.setText(dateStr);
            darbas.setText(task.getWorkInPlanName());
            komentaras.setText(task.getWorkInPlanNote());

            if (isTaskListActivity) {
                klientas.setVisibility(View.GONE);

                if (task.getWorkInPlanTerm() != null && !isTimeNotSet(task.getWorkInPlanTerm())) {
                    timeLayout.setVisibility(View.VISIBLE);
                    String[] timeParts = timeStr.split(":");
                    if (timeParts.length == 2) {
                        hours.setText(timeParts[0]);
                        minutes.setText(timeParts[1]);
                    } else {
                        Log.e("AdapterTasks", "Unexpected time format: " + timeStr);
                    }
                } else {
                    timeLayout.setVisibility(View.GONE);
                }
            } else {
                klientas.setVisibility(View.VISIBLE);
                klientas.setText(task.getWorkInPlanForCustomerName());
                date.setVisibility(View.GONE);

                if (task.getWorkInPlanTerm() != null && !isTimeNotSet(task.getWorkInPlanTerm())) {
                    timeLayout.setVisibility(View.VISIBLE);
                    String[] timeParts = timeStr.split(":");
                    if (timeParts.length == 2) {
                        hours.setText(timeParts[0]);
                        minutes.setText(timeParts[1]);
                    } else {
                        Log.e("AdapterTasks", "Unexpected time format: " + timeStr);
                    }
                } else {
                    timeLayout.setVisibility(View.GONE);
                }
            }

            setCompletionIcon(atliktaIcon, task.getWorkInPlanDone());

            itemView.setOnClickListener(v -> listener.onTaskItemClick(task));
        }
    }

    private boolean isTimeNotSet(Timestamp timestamp) {
        if (timestamp == null) {
            return true;
        }
        LocalDateTime localDateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.toLocalTime().equals(LocalDateTime.MIN.toLocalTime());
    }

    private void setCompletionIcon(ImageView icon, String done) {
        if ("1".equals(done)) {
            icon.setImageResource(R.drawable.ic_check_box);
        } else {
            icon.setImageResource(R.drawable.ic_check_box_outline);
        }
    }

    public interface OnTaskItemClickListener {
        void onTaskItemClick(Task task);
    }
}
