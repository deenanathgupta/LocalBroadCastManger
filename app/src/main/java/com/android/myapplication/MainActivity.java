package com.android.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    LocalBroadCastManager bus = LocalBroadCastManager.getInstance();
    LocalConsumer l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        l = new LocalConsumer() {
            @Override
            public void accept(String event, Object data) {
                Log.d("DEENA", "accept: "+event);
                if (event.equals(LocalBroadCastManager.EVENT1)) {

                }
            }
        };

        bus.subscribe(LocalBroadCastManager.EVENT1, l);
        bus.subscribe(LocalBroadCastManager.EVENT2, l);
        bus.publish(LocalBroadCastManager.EVENT1);
    }

    public void click(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unsubscribe(l);
    }
}

interface LocalConsumer {
    void accept(String event, Object data);
}
