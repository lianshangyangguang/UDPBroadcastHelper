package com.gwell.view.udpbroadcasthelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        UDPBroadcastHelper helper = new UDPBroadcastHelper(this);
//        helper.receive(9988,new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                Log.d("zxy", "handleMessage: "+msg.what);
//                super.handleMessage(msg);
//            }
//        });
    }
}
