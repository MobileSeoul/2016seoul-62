package com.example.ye0jun.seoulprice;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    DbOpenHelper dbmanger;
    ArrayList<String> address;
    ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        splash = (ImageView)findViewById(R.id.splash);
        Glide.with(this).load(R.drawable.splash).into(splash);

        dbmanger = new DbOpenHelper(this);
        address = dbmanger.selectAllAdrress();

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(address) , 1500); // 3초 후에 hd Handler 실행
    }

    private class splashhandler implements Runnable{
        ArrayList<String> address;

        public splashhandler(ArrayList<String> address){ this.address = address; }
        public void run() {
            if(this.address.size() == 0) {
                startActivity(new Intent(getApplication(), JoinActivity.class)); // 로딩이 끝난후 이동할 Activity
                SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
            }
            else{
                startActivity(new Intent(getApplication(), MainActivity.class)); // 로딩이 끝난후 이동할 Activity
                SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
            }
        }
    }
}
