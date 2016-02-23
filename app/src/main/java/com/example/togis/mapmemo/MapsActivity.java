package com.example.togis.mapmemo;

import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.*;
import android.location.Location;
import android.location.LocationManager;
import android.database.*;
import android.database.sqlite.*;
import android.view.*;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationManager mLocationManager;
    Button entry;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //マップ表示
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //Entryボタンの定義
        entry = (Button) findViewById(R.id.entry);
        entry.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v){
                //Entry押したらプレイス登録画面へ遷移
                Intent it = new Intent(getApplicationContext(),entry.class);
                it.putExtra("Title","プレイス登録");
                startActivity(it);
            }
        });
        //位置取得機能の準備
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
     }

    //MAP描画後の処理
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //位置取得時のおまじないチェック
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //現在地の青丸を表示する設定
        mMap.setMyLocationEnabled(true);
        //渋滞情報を表示する設定
        mMap.setTrafficEnabled(true);
        // 直近の位置情報取得
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng nowLoc = new LatLng(location.getLatitude(),  location.getLongitude());
        //現在地をトーストで表示
        String msg = "Lat=" + nowLoc.latitude
                + "\nLng=" + nowLoc.longitude;
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        //カメラ位置を現在地にズーム
        mMap.moveCamera(CameraUpdateFactory.newLatLng(nowLoc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(5));

        //DB準備
        String str = "data/data/" + getPackageName() + "/Sample.db";
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(str,null);
        //SQL準備
        String qry0 = "Drop table IF exists product";
        String qry1 = "CREATE TABLE product" +
                "(id INTEGER PRIMARY KEY," +
                " latitude DOUBLE," +
                " longitude DOUBLE,"+
                " name String,"+
                " comment String)";
        //とりえあず固定値で適当にDB登録
        String qry2 = "Insert into product(latitude,longitude,name,comment) VALUES (30.4113,134,'プリん','いいね')";
        String qry3 = "Insert into product(latitude,longitude,name,comment) VALUES (40.4113,120,'あめ','お高め')";
        String qry4 = "SELECT * FROM product";
        //DROP＆CREATEは必要なときのみ実行
        //db.execSQL(qry0);
        //db.execSQL(qry1);
        db.execSQL(qry2);
        db.execSQL(qry3);

        //登録済のプレイスを全件取得
        Cursor cr = db.rawQuery(qry4, null);
        LatLng mlatlng;
        //件数分ループして情報取得
        while(cr.moveToNext()){
            //座標情報
            double x = cr.getDouble(1);
            double y = cr.getDouble(2);
            mlatlng =  new LatLng(x, y);
            //名称情報
            String name = cr.getString(3);
            //取得した情報をもとに、地図にマーカーを配置
            mMap.addMarker(new MarkerOptions().position(mlatlng).title(name));
        }
    }
}
