package com.orderprio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startSplash();
    }

    private void startSplash() {
        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), EnterActivity.class);
                startActivity(intent);
            }

        };
        countDownTimer.start();
    }


}
