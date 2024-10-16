package util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.aiva.aivacrm.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "aiva_crm_channel";
    private static final String CHANNEL_NAME = "Aiva CRM Notifications";
    private static final String CHANNEL_DESCRIPTION = "Aiva CRM App Notifications";

    private Context mContext;
    private NotificationManager mNotificationManager;

    public NotificationHelper(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder getNotification(String title, String message) {
        return new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
    }

    public void notify(int id, NotificationCompat.Builder builder) {
        mNotificationManager.notify(id, builder.build());
    }
}
