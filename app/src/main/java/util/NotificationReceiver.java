package util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import util.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);

        NotificationCompat.Builder notificationBuilder = intent.getParcelableExtra(NOTIFICATION);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);

        notificationHelper.notify(notificationId, notificationBuilder);
    }
}
