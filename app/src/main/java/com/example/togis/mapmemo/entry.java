package com.example.togis.mapmemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.SpannableStringBuilder;

public class entry extends AppCompatActivity {

    EditText name;
    EditText comment;
    Button entry;
    Location location;
    private LocationManager mLocationManager;

    protected void onCreate(Bundle savedInstanceState) {
        //画面表示
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        //入力欄とボタンの定義
        name = (EditText) findViewById(R.id.name_entry);
        comment = (EditText) findViewById(R.id.comment_entry);
        entry = (Button) findViewById(R.id.entry);
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
        // 直近の位置情報取得
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //登録ボタン押したときの処理
        entry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //入力エリアからの文字列取得
                SpannableStringBuilder tmp;
                tmp = (SpannableStringBuilder) name.getText();
                String nameSt = tmp.toString();
                tmp = (SpannableStringBuilder) comment.getText();
                String commentSt = tmp.toString();
                //DB準備
                String str = "data/data/" + getPackageName() + "/Sample.db";
                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(str, null);
                //SQL準備
                String qry = "Insert into product(latitude,longitude,name,comment) VALUES ("+ location.getLatitude() + "," +  location.getLongitude() +",'" + nameSt +"','" + commentSt + "');";
                //プレイス登録
                db.execSQL(qry);
                Toast.makeText(getBaseContext(), "登録完了", Toast.LENGTH_LONG).show();

                //地図に戻る
                Intent it = new Intent(getApplicationContext(),MapsActivity.class);
                it.putExtra("Title","プレイスビュー");
                startActivity(it);
            }
        });
    }
}

// GitHUB をテストするためにコメントを書いてファイルを更新してみた　
