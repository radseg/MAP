package com.example.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView txvLoc, txvSetting;
    Button btn,btn_check;
    LocationManager mgr;
    boolean isGPSEnable;
    static final int Min_Time = 5000;
    static final float Min_Dist = 0;
    boolean isGPSEnabled;      //GPS定位是否可用
    boolean isNetworkEnabled;  //網路定位是否可用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txvLoc = findViewById(R.id.textView);
        txvSetting = findViewById(R.id.textView2);
        btn = findViewById(R.id.button);
        btn_check = findViewById(R.id.button2);

        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        //先取得系統定位管理員的資料
        String provider = mgr.getBestProvider(new Criteria(), true);
        //第一個參數是設定所要求的規格條件，第二個是設定是否指傳回系統中已啟用的提供者

        isGPSEnable = mgr.isProviderEnabled("gps");
        CheckPermission();

    }


    @Override
    protected void onResume() {
        super.onResume();
        txvLoc.setText("尚未取得定位資訊");
        enableLocationUpdates(true);
        String str = "GPS定位" + (isGPSEnable?"開啟":"關閉");
        str += "\n網路定位" + (isNetworkEnabled?"開啟":"關閉");
        txvSetting.setText(str);
    }


    @Override
    protected  void onPause(){
        super.onPause();
        enableLocationUpdates(false);
    }






    public void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        }

    }




    @Override
    public void onLocationChanged(@NonNull Location loc) {
        String str = "定位提供者:" + loc.getProvider();
        str += String.format("\n緯度:%.5\n經度:%.5f\n高度:%.2f公尺",
        loc.getAltitude(),//取得高度(m)
        loc.getLongitude(),//取得緯度
        loc.getLongitude(),//取得經度
        loc.getSpeed());//取得速度(公尺/秒)
        txvLoc.setText(str);
    }



    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    private void enableLocationUpdates(boolean isTurnOn) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {  // 使用者已經允許定位權限
            if (isTurnOn) {
                //檢查 GPS 與網路定位是否可用
                isGPSEnabled = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // 無提供者, 顯示提示訊息
                    Toast.makeText(this, "請確認已開啟定位功能!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "取得定位資訊中...", Toast.LENGTH_LONG).show();
                    if (isGPSEnabled)
                        mgr.requestLocationUpdates(   //向 GPS 定位提供者註冊位置事件監聽器
                                LocationManager.GPS_PROVIDER, Min_Time, Min_Dist, this);
                    if (isNetworkEnabled)
                        mgr.requestLocationUpdates(   //向網路定位提供者註冊位置事件監聽器
                                LocationManager.NETWORK_PROVIDER, Min_Time, Min_Dist, this);
                }
            }
            else {
                mgr.removeUpdates(this);    //停止監聽位置事件
            }
        }
    }



    public void setup(View V){
        Intent it = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //手機定位功能鈕的按鈕事件方法
        startActivity(it);
    }

    public void Check(View V){
        CheckPermission();
    }


}