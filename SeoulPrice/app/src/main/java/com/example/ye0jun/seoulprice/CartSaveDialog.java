package com.example.ye0jun.seoulprice;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ye0jun on 2016. 10. 29..
 */

public class CartSaveDialog extends Dialog{

    private TextView tv_Date;
    private EditText et_Name;
    private Button mLeftButton;
    private Button mRightButton;
    private String Date;
    private String Name;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_cart);

        tv_Date = (TextView) findViewById(R.id.dialog_date);
        et_Name = (EditText) findViewById(R.id.dialog_input);
        et_Name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_Name.setSingleLine();
        et_Name.requestFocus();

        mLeftButton = (Button) findViewById(R.id.btn_left);
        mRightButton = (Button) findViewById(R.id.btn_right);

        // 제목과 내용을 생성자에서 셋팅한다.
        tv_Date.setText(getCurrentDate());

        final RelativeLayout rl_Dialog = (RelativeLayout)findViewById(R.id.dialog_root);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        et_Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    rl_Dialog.animate().translationY(-400).withLayer();
                else
                    rl_Dialog.animate().translationY(0).withLayer();

            }
        });

        et_Name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i){
                    case EditorInfo.IME_ACTION_DONE:
                        rl_Dialog.animate().translationY(0).withLayer();
                        et_Name.clearFocus();
                        InputMethodManager inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        break;
                }
                return false;
            }
        });


        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public CartSaveDialog(Context context, View.OnClickListener leftListener,
                        View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }

    public String getCurrentDate() {
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date());
        return currentDateandTime;
    }
}
