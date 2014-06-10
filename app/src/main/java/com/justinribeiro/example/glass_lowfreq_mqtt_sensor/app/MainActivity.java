package com.justinribeiro.example.glass_lowfreq_mqtt_sensor.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopService(new Intent(this, LiveCardService.class));
        finish();
    }
}
