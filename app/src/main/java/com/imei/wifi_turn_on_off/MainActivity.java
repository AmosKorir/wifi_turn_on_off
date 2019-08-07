package com.imei.wifi_turn_on_off;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
  private Button turnOnButton;
  private Button turnOffButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    turnOffButton = findViewById(R.id.offBtn);
    turnOnButton = findViewById(R.id.onBtn);

    turnOnButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        @SuppressLint("WifiManagerLeak") WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
      }
    });

    turnOffButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        @SuppressLint("WifiManagerLeak") WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);wifi.setWifiEnabled(false);
      }
    });
  }
}
