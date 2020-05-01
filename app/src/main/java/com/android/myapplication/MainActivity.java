package com.android.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    LocalBroadCastManager bus = LocalBroadCastManager.getInstance();
    Consumer<String> consumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        consumer = s -> System.out.println("String: Activity1 ::" + s);
        bus.subscribe(String.class, consumer);
        bus.publish("Activity1");


    }

    public void click(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unsubscribe(consumer);
    }
}
