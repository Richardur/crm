package util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.aiva.aivacrm.R;

public class NotificationWorker extends Worker {

    public static final String CHANNEL_ID = "TASK_REMINDER_CHANNEL";
    public static final String TASK_NAME_KEY = "TASK_NAME";
    public static final String TASK_ID_KEY = "TASK_ID";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get task details from input data
        String taskName = getInputData().getString(TASK_NAME_KEY);
        int taskId = getInputData().getInt(TASK_ID_KEY, -1);

        if (taskName == null || taskId == -1) {
            return Result.failure();
        }

        // Create notification channel
        createNotificationChannel();

        // Build and display the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your own icon
                .setContentTitle("Task Reminder")
                .setContentText("You have an upcoming task: " + taskName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Use taskId as notification ID to uniquely identify each notification
        notificationManager.notify(taskId, builder.build());

        return Result.success();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ (Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Reminder";
            String description = "Channel for task reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager =
                    getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
