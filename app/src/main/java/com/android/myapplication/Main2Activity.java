package com.android.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Main2Activity extends AppCompatActivity {
    LocalBroadCastManager bus = LocalBroadCastManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bus.subscribe(String.class, s -> System.out.println("String: Activity2::" + s));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.publish("Activity2");
    }
}
