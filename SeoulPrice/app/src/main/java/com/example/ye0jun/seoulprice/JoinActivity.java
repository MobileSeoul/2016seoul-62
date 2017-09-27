package com.example.ye0jun.seoulprice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class JoinActivity extends AppCompatActivity {
    Animator anim;
    ImageView reveal;
    FloatingActionButton floatingActionButton_second;
    FloatingActionButton floatingActionButton_first;
    RelativeLayout SecondView;
    EditText et_Join;
    WebView adressView;

    Intent intent;
    ViewPager viewPager;
    Animation animFadein;
    Animation animSlideY;
    Animation animSlideYBack;
    DbOpenHelper dbmanger;
    ArrayList<String> address;

    boolean bCorrectAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        bCorrectAddress = false;

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dbmanger = new DbOpenHelper(this);
        address = dbmanger.selectAllAdrress();

        SecondView = (RelativeLayout)findViewById(R.id.join_second_view);
        if(getIntent().getStringExtra("edit") != null && getIntent().getStringExtra("edit").equals("true")){
            SecondView.setVisibility(View.VISIBLE);
        }

        intent = new Intent(this, MainActivity.class);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
        animSlideY = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slidey);
        animSlideYBack = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideyback);
        animSlideYBack.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                adressView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        String KeyWord = "Android";

        adressView = (WebView)findViewById(R.id.join_address);
        adressView.getSettings().setJavaScriptEnabled(true);
        adressView.addJavascriptInterface(new JSInterface(),KeyWord);

        et_Join = (EditText)findViewById(R.id.et_join_search);
        et_Join.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        et_Join.setSingleLine();
        et_Join.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i){
                    case EditorInfo.IME_ACTION_SEARCH:
                        adressView.setVisibility(View.VISIBLE);
                        adressView.setAnimation(animSlideY);
                        adressView.loadUrl("http://52.78.5.40:3000/main/hello?search=" + textView.getText());
                        break;
                }
                return false;
            }
        });

        floatingActionButton_first = (FloatingActionButton)findViewById(R.id.actionButton1);
        floatingActionButton_second = (FloatingActionButton)findViewById(R.id.actionButton);

        reveal = (ImageView)findViewById(R.id.view_reveal);


        floatingActionButton_first.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecondView.setVisibility(View.VISIBLE);
                SecondView.setAnimation(animFadein);
            }
        });

        floatingActionButton_second.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bCorrectAddress){
                    if(address.size() == 0)
                        dbmanger.insertAddressData(et_Join.getText().toString());
                    else
                        dbmanger.updateAddressData(et_Join.getText().toString());

                    show(reveal);
                }
                else{
                    Toast.makeText(getApplicationContext(), "정확한 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getStringExtra("edit") != null && getIntent().getStringExtra("edit").equals("true") && !et_Join.isSelected()){
            startActivity(new Intent(getApplication(), MainActivity.class)); // 로딩이 끝난후 이동할 Activity
            finish();
        }
    }

    private void show(final View view) {
        // get the center for the clipping circle
        int cx = (floatingActionButton_second.getLeft() + floatingActionButton_second.getRight()) / 2;
        int cy = (floatingActionButton_second.getTop() + floatingActionButton_second.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                0, finalRadius);
        anim.setDuration(500);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startActivity(intent);
                finish();
            }
        });

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    public class JSInterface {
        @JavascriptInterface
        public void call(final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adressView.startAnimation(animSlideYBack);
                            et_Join.setText(message);
                            bCorrectAddress = true;
                        }
                    });
                }
            }).start();
        }
    }
}
