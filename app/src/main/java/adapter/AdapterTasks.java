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
        if (task.isDateOnlyAction() || isTimeNotSet(task.getWorkInPlanTerm())) {
            return VIEW_TYPE_DATE_ONLY;
        } else {
            return VIEW_TYPE_WITH_TIME;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_DATE_ONLY) {
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
        if (holder.getItemViewType() == VIEW_TYPE_DATE_ONLY) {
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

            if ("1".equals(task.getWorkInPlanDone())) {
                atliktaIcon.setImageResource(R.drawable.ic_check_box);
            } else {
                atliktaIcon.setImageResource(R.drawable.ic_check_box_unchecked);
            }

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

            if (isTaskListActivity) {
                // TaskListActivity logic
                klientas.setVisibility(View.GONE);
                date.setVisibility(View.VISIBLE);
                date.setText(dateStr);
                darbas.setText(task.getWorkInPlanName());
                komentaras.setText(task.getWorkInPlanNote());

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
                // TaskTab logic
                klientas.setVisibility(View.VISIBLE);
                klientas.setText(task.getWorkInPlanForCustomerName());
                darbas.setText(task.getWorkInPlanName());
                komentaras.setText(task.getWorkInPlanNote());
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

                }

                if ("1".equals(task.getWorkInPlanDone())) {
                    atliktaIcon.setImageResource(R.drawable.ic_check_box);
                } else {
                    atliktaIcon.setImageResource(R.drawable.ic_check_box_unchecked);
                }
            }

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

    public interface OnTaskItemClickListener {
        void onTaskItemClick(Task task);
    }
}
