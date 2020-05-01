package com.android.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class Main2Activity extends AppCompatActivity {
    LocalBroadCastManager bus = LocalBroadCastManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bus.subscribe(LocalBroadCastManager.EVENT2, new LocalConsumer() {
            @Override
            public void accept(String event, Object data) {
                Log.d("DEENA", "accept: Activity2"+event);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.publish(LocalBroadCastManager.EVENT2);
    }
}
