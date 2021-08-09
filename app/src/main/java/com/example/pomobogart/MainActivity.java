package com.example.pomobogart;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.pomobogart.model.Time;
import com.example.pomobogart.util.NotificationPublisher;

public class MainActivity extends AppCompatActivity {
    private ImageView start;
    private ImageView pause;
    private ImageView stop;
    private TextView timer;
    private ProgressBar timerDisplay;

    private static final long sampleWorkTime = 10_000;
    private static final long defaultWorkTime = 1_500_000;
    private static final int sampleBreakTime = 15_000;
    private static final int defaultBreakTime = 300_000;
    private static final long DEFAULT_WORK_TIME = defaultWorkTime;
    private static final int DEFAULT_TIME_INTERVAL = 1_000;
    private static final int DEFAULT_BREAK_TIME = defaultBreakTime;

    private CountDownTimer countDownWorkTimer;
    private CountDownTimer countDownBreakTimer;
    private int count;
    public static final String NOTIFICATION_CHANNEL_ID = "Pomobogart Channel Id";
    private final static String default_notification_channel_id = "default";

    private Time time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = new Time(DEFAULT_WORK_TIME, DEFAULT_BREAK_TIME);

        start = findViewById(R.id.activity_main_iv_start);
        stop = findViewById(R.id.activity_main_iv_stop);
        pause = findViewById(R.id.activity_main_iv_pause);
        timer = findViewById(R.id.activity_main_tv_timer);
        timerDisplay = findViewById(R.id.activity_main_pb_timer);


        initializeComponent();

        start.setOnClickListener(v -> {
            start.setEnabled(false);
            enableComponents();
            startTimer();
        });

        pause.setOnClickListener(v -> {
            start.setEnabled(true);
            pause.setEnabled(false);
            stop.setEnabled(true);
            pauseTimer();
        });

        stop.setOnClickListener(v -> {
            start.setEnabled(true);
            disableComponents();
            stopTimer();
        });

    }

    /**
     * Method to initialize the components when the application starts
     */
    private void initializeComponent() {
        disableComponents();
    }

    /**
     * Method to disable the 2 operation buttons (stop, pause)
     */
    private void disableComponents() {
        stop.setEnabled(false);
        pause.setEnabled(false);
    }


    /**
     * Method to enable the 2 operation buttons (stop, pause)
     */
    private void enableComponents() {
        stop.setEnabled(true);
        pause.setEnabled(true);
    }

    /**
     * This method will configure when will short breaks or long breaks be given
     */
    private void configureBreaks() {
        // Record how many times this method called
        count++;

        // If the count reaches to four then trigger long breaks; else give short breaks
        if (count == 4) {
            controlBreakTimer(time.getWorkingTime());
            count = 0;
        } else {
            controlBreakTimer(time.getBreakTime());
        }
    }

    private void playRingtone(boolean isOnBreak) {
        MediaPlayer ringtone = MediaPlayer.create(this, R.raw.pristine_609);
        ringtone.setOnCompletionListener(mp -> {
            timer.setText(R.string.default_work_time);
            if (isOnBreak) {
                configureBreaks();
            } else {
                time.setMilliSeconds(0);
            }
        });
        ringtone.start();
    }

    private void startTimer() {
        time.setMilliSeconds(0);
        controlWorkTimer(time.getMilliSeconds());
    }

    private void pauseTimer() { countDownWorkTimer.cancel(); }

    private void stopTimer() {
        if (countDownWorkTimer != null) {
            countDownWorkTimer.cancel();
        }

        if (countDownBreakTimer != null){
            countDownBreakTimer.cancel();
        }

        time.setMilliSeconds(0);
        timer.setText(R.string.default_work_time);
        timerDisplay.setMax(100);
        timerDisplay.setProgress(100);
    }

    private void controlWorkTimer(long milliSecond) {
        countDownWorkTimer =
                new CountDownTimer(milliSecond, DEFAULT_TIME_INTERVAL) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        time.setMilliSeconds(millisUntilFinished);
                        updateTimer(time.getSeconds(), getString(R.string.work_title));
                        timerDisplay.setMax((int) DEFAULT_WORK_TIME / 1000);
                        timerDisplay.setProgress((int) (millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        scheduleNotification(getNotification(getString(R.string.after_work_message)));
                        Handler handler = new Handler();
                        handler.postDelayed(() -> playRingtone(true), 1000);
                    }
                };
        countDownWorkTimer.start();
    }


    private void controlBreakTimer(long milliSecond) {
        countDownBreakTimer = new CountDownTimer(milliSecond, DEFAULT_TIME_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                time.setMilliSeconds(millisUntilFinished);
                updateTimer(time.getSeconds(), getString(R.string.break_title));
                timerDisplay.setMax((int) time.getWorkingTime() / 1000);
                timerDisplay.setProgress((int) (time.getSeconds()));
            }

            @Override
            public void onFinish() {
                playRingtone(false);
                scheduleNotification(getNotification(getString(R.string.after_break_message)));
                disableComponents();
                start.setEnabled(true);
                timer.setText(R.string.default_work_time);
                timerDisplay.setMax(100);
                timerDisplay.setProgress(100);

            }
        };
        countDownBreakTimer.start();
    }

    /**
     * Update the timer on the screen
      */
    private void updateTimer(int secondsLeft, String title) {
        int secondsRemaining = secondsLeft - time.getMinutes() * 60;

        String minutesString = Integer.toString(time.getMinutes());
        String secondString = Integer.toString(secondsRemaining);

        if (secondsRemaining <= 9) {
            secondString = "0" + secondString;
        }
        if (time.getMinutes() <= 9) {
            minutesString = "0" + minutesString;
        }
        timer.setText(String.format("%s:%s", minutesString, secondString));
        showNotification(title,
                String.format("%s:%s", minutesString, secondString));
    }

    private void scheduleNotification(Notification notification) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10, pendingIntent);

    }

    private Notification getNotification(String content) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle(getString(R.string.app_name)+":");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
    }

    // Show notification
    private void showNotification(String title, String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify");
        Intent ii = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "pomobogart";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Pomobogart Id",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }
}