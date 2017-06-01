package com.gwell.view.app_;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gwell.view.library.UDPBroadcastHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UDPBroadcastHelper helper = new UDPBroadcastHelper(this);
        helper.receive(9988, new UDPBroadcastHelper.OnReceive() {
            @Override
            public void onReceive(int state, Bundle data) {
                Log.d("zxy", "onReceive: ");
            }
        });
    }
}
