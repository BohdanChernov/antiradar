package com.antiradar.location;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.antiradar.MainActivity;
import com.google.gson.Gson;

public class LocationForegroundService extends Service implements LocationEventReceiver {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final int NOTIFICATION_ID = 1;
    public static final String LOCATION_EVENT_NAME = "com.antiradar.LOCATION_INFO";
    public static final String LOCATION_EVENT_DATA_NAME = "LocationData";
    public static final int LOCATION_START_TIME_INTERVAL = 1000;
    public static final String JS_LOCATION_LAT_KEY = "latitude";
    public static final String JS_LOCATION_LON_KEY = "longitude";
    public static final String JS_LOCATION_TIME_KEY = "timestamp";
    public static final String JS_LOCATION_EVENT_NAME = "location_received";

    private AlarmManager mAlarmManager;
    private BroadcastReceiver mEventReceiver;
    private PendingIntent mLocationBackgroundServicePendingIntent;
    private Gson mGson;

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mGson = new Gson();
        createEventReceiver();
        registerEventReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        createLocationPendingIntent();
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + LOCATION_START_TIME_INTERVAL, mLocationBackgroundServicePendingIntent);
        return START_NOT_STICKY;
    }

    @Override
    public void createEventReceiver() {
        mEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocationCoordinates locationCoordinates = mGson.fromJson(
                        intent.getStringExtra(LOCATION_EVENT_DATA_NAME), LocationCoordinates.class);
                /*
                TODO: do any kind of logic in here with the LocationCoordinates class
                e.g. like a request to an API, etc --> all on the native side
                */
            }
        };
    }

    @Override
    public void registerEventReceiver() {
        IntentFilter eventFilter = new IntentFilter();
        eventFilter.addAction(LOCATION_EVENT_NAME);
        registerReceiver(mEventReceiver, eventFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mEventReceiver);
        mAlarmManager.cancel(mLocationBackgroundServicePendingIntent);
        stopSelf();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        unregisterReceiver(mEventReceiver);
        mAlarmManager.cancel(mLocationBackgroundServicePendingIntent);
        stopSelf();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void createLocationPendingIntent() {
        Intent i = new Intent(getApplicationContext(), LocationBackgroundService.class);
        mLocationBackgroundServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
