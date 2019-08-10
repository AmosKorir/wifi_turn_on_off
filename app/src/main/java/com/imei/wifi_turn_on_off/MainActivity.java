package com.imei.wifi_turn_on_off;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
  private static final int MY_PERMISSIONS_REQUEST_LOCATION = 907;
  private WifiManager.LocalOnlyHotspotReservation mReservation;
  private Button turnOnButton;
  private Button turnOffButton;
  private Button turnOnHotspotButton;
  private Button turnOffHotspotButton;
  private String TAG = "Wifi";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    turnOffButton = findViewById(R.id.offBtn);
    turnOnButton = findViewById(R.id.onBtn);
    turnOnHotspotButton = findViewById(R.id.hotspoton);
    turnOffHotspotButton = findViewById(R.id.hotspotoff);

    turnOnButton.setOnClickListener(v -> turnOnWifi());

    turnOffButton.setOnClickListener(v -> turnOffWifi());

    turnOnHotspotButton.setOnClickListener(v -> {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (checkLocationPermission()) {
          if (isLocationEnabled()) {
            turnOnHotspot();
          } else {
            redireTosetting();
          }
        } else {
          requestLocationPermission();
        }
      }
    });

    turnOffHotspotButton.setOnClickListener(v -> {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (checkLocationPermission()) {
          if (isLocationEnabled()) {
            turnOffHotspot();
          } else {
            redireTosetting();
          }
        } else {
          requestLocationPermission();
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

  //request location permission the location hotspot requires location permission
  private void requestLocationPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.ACCESS_FINE_LOCATION)) {
        //Explanation
        Toast.makeText(this, "rationale", Toast.LENGTH_SHORT).show();
      } else {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(this,
            new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
            MY_PERMISSIONS_REQUEST_LOCATION);
      }
    } else {
      // permission granted
    }
  }

  private boolean checkLocationPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return false;
    } else {
      return true;
    }
  }

  // check for  location feature, for local hotspot to work , location must be on

  public boolean isLocationEnabled() {
    int locationMode = 0;
    String locationProviders;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        locationMode =
            Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
      } catch (Settings.SettingNotFoundException e) {
        e.printStackTrace();
        return false;
      }

      return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    } else {
      locationProviders = Settings.Secure.getString(getContentResolver(),
          Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
      return !TextUtils.isEmpty(locationProviders);
    }
  }

  // redirect the user  to setting
  public void redireTosetting() {
    LocationManager locationManager =
        (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      AlertDialog alertDialog = new AlertDialog.Builder(this)
          .setTitle("Location is turned off")  // GPS not found
          .setMessage("Hot spot needs you location for it to work") // Want to enable?
          .setPositiveButton("yes",
              (dialogInterface, i) -> {
                dialogInterface.dismiss();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
              })
          .setNegativeButton("No", (dialog, which) -> finish())
          .setCancelable(false)
          .show();
    }
  }
}
