package com.example.pomobogart.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import com.example.pomobogart.R;

import static com.example.pomobogart.MainActivity.NOTIFICATION_CHANNEL_ID;

public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                            "NOTIFICATION_CHANNEL_NAME", importance);
            assert notificationManager != null;

            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[] {1000, 1000, 1000, 1000, 1000});
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.getPackageName() + "/" + R.raw.pristine_609), audioAttributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        assert notificationManager != null;
        notificationManager.notify(id, notification);
    }
}
