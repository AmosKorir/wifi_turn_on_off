package com.imei.wifi_turn_on_off;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
  private WifiManager.LocalOnlyHotspotReservation mReservation;
  private Button turnOnButton;
  private Button turnOffButton;
  private Button turnOnHotspotButton;
  private Button turnOffHotspotButton;
  private String TAG = getLocalClassName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    turnOffButton = findViewById(R.id.offBtn);
    turnOnButton = findViewById(R.id.onBtn);
    turnOnHotspotButton = findViewById(R.id.hotspoton);
    turnOffHotspotButton = findViewById(R.id.hotspotoff);

    turnOnButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        turnOnWifi();
      }
    });

    turnOffButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        turnOffWifi();
      }
    });

    turnOnHotspotButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          turnOnHotspot();
        }
      }
    });

    turnOffHotspotButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          turnOffHotspot();
        }
      }
    });
  }

  //turn on wifi
  private void turnOnWifi() {
    @SuppressLint("WifiManagerLeak") WifiManager wifi =
        (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    wifi.setWifiEnabled(true);
  }

  //turn off wifi
  private void turnOffWifi() {
    @SuppressLint("WifiManagerLeak") WifiManager wifi =
        (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    wifi.setWifiEnabled(false);
  }

  //start hotspot

  @RequiresApi(api = Build.VERSION_CODES.O)
  private void turnOnHotspot() {
    WifiManager manager =
        (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {

      @Override
      public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
        super.onStarted(reservation);
        Log.d(TAG, "Wifi Hotspot is on now");
        mReservation = reservation;
      }

      @Override
      public void onStopped() {
        super.onStopped();
        Log.d(TAG, "onStopped: ");
      }

      @Override
      public void onFailed(int reason) {
        super.onFailed(reason);
        Log.d(TAG, "onFailed: ");
      }
    }, new Handler());
  }

  @RequiresApi(api = Build.VERSION_CODES.O) private void turnOffHotspot() {
    if (mReservation != null) {
      mReservation.close();
    }
  }
}
