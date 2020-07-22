package com.antiradar.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.core.content.ContextCompat;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.antiradar.location.LocationForegroundService.LOCATION_EVENT_DATA_NAME;

public class LocationModule extends ReactContextBaseJavaModule implements LocationEventReceiver, JSEventSender {
    private static final String MODULE_NAME = "LocationManager";
    private static final String CONST_JS_LOCATION_EVENT_NAME = "JS_LOCATION_EVENT_NAME";
    private static final String CONST_JS_LOCATION_LAT = "JS_LOCATION_LAT_KEY";
    private static final String CONST_JS_LOCATION_LON = "JS_LOCATION_LON_KEY";
    private static final String CONST_JS_LOCATION_TIME = "JS_LOCATION_TIME_KEY";
    public static final int LOCATION_UPDATE_INTERVAL = 1000;

    private Context mContext;
    private Intent mForegroundServiceIntent;
    private BroadcastReceiver mEventReceiver;
    private Gson mGson;
    private boolean isWorking = false;
    private AlarmManager mAlarmManager;
    private Intent alarmService;
    private PendingIntent mLocationBackgroundServicePendingIntent;

    LocationModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        mForegroundServiceIntent = new Intent(mContext, LocationForegroundService.class);
        mGson = new Gson();
        createEventReceiver();
        registerEventReceiver();

        alarmService = new Intent(mContext.getApplicationContext(), LocationBackgroundService.class);
        mLocationBackgroundServicePendingIntent = PendingIntent
                .getService(mContext.getApplicationContext(),
                        1,
                        alarmService,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager = (AlarmManager) mContext
                .getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
    }

    @ReactMethod
    public void startBackgroundLocation() {
        isWorking = true;
        ContextCompat.startForegroundService(mContext, mForegroundServiceIntent);
    }

    @ReactMethod
    public void stopBackgroundLocation() {
//        mContext.unregisterReceiver(mEventReceiver);
        isWorking = false;
        mAlarmManager.cancel(mLocationBackgroundServicePendingIntent);
        mContext.stopService(mForegroundServiceIntent);
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(CONST_JS_LOCATION_EVENT_NAME, LocationForegroundService.JS_LOCATION_EVENT_NAME);
        constants.put(CONST_JS_LOCATION_LAT, LocationForegroundService.JS_LOCATION_LAT_KEY);
        constants.put(CONST_JS_LOCATION_LON, LocationForegroundService.JS_LOCATION_LON_KEY);
        constants.put(CONST_JS_LOCATION_TIME, LocationForegroundService.JS_LOCATION_TIME_KEY);
        return constants;
    }

    @Nonnull
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void createEventReceiver() {
        mEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LocationCoordinates locationCoordinates = mGson.fromJson(
                        intent.getStringExtra(LOCATION_EVENT_DATA_NAME), LocationCoordinates.class);
                WritableMap eventData = Arguments.createMap();
                eventData.putDouble(
                        LocationForegroundService.JS_LOCATION_LAT_KEY,
                        locationCoordinates.getLatitude());
                eventData.putDouble(
                        LocationForegroundService.JS_LOCATION_LON_KEY,
                        locationCoordinates.getLongitude());
                eventData.putDouble(
                        LocationForegroundService.JS_LOCATION_TIME_KEY,

                        locationCoordinates.getTimestamp());
                sendEventToJS(getReactApplicationContext(),
                        LocationForegroundService.JS_LOCATION_EVENT_NAME, eventData);

                if (isWorking == true) {
                    mAlarmManager
                            .set(AlarmManager.RTC_WAKEUP,
                                    System.currentTimeMillis() + LOCATION_UPDATE_INTERVAL,
                                    mLocationBackgroundServicePendingIntent);
                }
            }
        };
    }

    @Override
    public void registerEventReceiver() {
        IntentFilter eventFilter = new IntentFilter(LocationForegroundService.LOCATION_EVENT_NAME);
        mContext.registerReceiver(mEventReceiver, eventFilter);
    }

    @Override
    public void sendEventToJS(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
