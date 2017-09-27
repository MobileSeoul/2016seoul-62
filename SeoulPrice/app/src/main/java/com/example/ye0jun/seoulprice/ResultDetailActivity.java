package com.example.ye0jun.seoulprice;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResultDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView title;
    TextView totalPrice;
    ListView detailItem;
    ResultDetailListviewAdapter adapter;
    int nTotalPrice;
    boolean bTouch;
    GoogleMap gGoogleMap;
    CartSaveDialog mCustomDialog;
    DbOpenHelper dbmanger;
    ArrayList<String> cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail);
        mCustomDialog = null;

        dbmanger = new DbOpenHelper(this);

        final Intent intent = getIntent();
        final HashMap<String, Integer> Shopping = (HashMap<String, Integer>)intent.getSerializableExtra("Shopping");
        HashMap<String, Integer> Price = (HashMap<String, Integer>)intent.getSerializableExtra("martPriceData");
        HashMap<String, AverageCounter> Average = (HashMap<String, AverageCounter>)intent.getSerializableExtra("Average");

        TextView transitionTextView = (TextView) findViewById(R.id.cartName);
        transitionTextView.setText(getIntent().getStringExtra("martName"));

        EnterSharedElementTextSizeHandler handler = new EnterSharedElementTextSizeHandler(this);
        handler.addTextViewSizeResource(transitionTextView, R.dimen.small_text_size, R.dimen.large_text_size);

        TextView martDistance = (TextView) findViewById(R.id.cartDate);
        martDistance.setText(getIntent().getStringExtra("martDistance"));

        TextView martAddress = (TextView) findViewById(R.id.detail_info_address);
        martAddress.setText(getIntent().getStringExtra("Address"));
        martAddress.measure(0, 0);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 105.25, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams a = (RelativeLayout.LayoutParams) martAddress.getLayoutParams();
        a.width = width - px;
        Log.d("ye0jun-width", martAddress.getMeasuredWidth() + "");
//        a.width = martAddress.getMeasuredWidth()-px;

        martAddress.setLayoutParams(a);
        martAddress.setSelected(true);
        martAddress.setSingleLine();
        martAddress.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        TextView todayDate = (TextView)findViewById(R.id.detail_date);
        final String sDate = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date());
        todayDate.setText(sDate);

        ImageView iv_Call = (ImageView) findViewById(R.id.detail_info_call);
        iv_Call.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                    return;
                }
                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+getIntent().getStringExtra("Number")));
                startActivity(in);

            }
        });

        final View.OnClickListener leftListener = new View.OnClickListener() {
            public void onClick(View v) {
                mCustomDialog.dismiss();
            }
        };

        final View.OnClickListener rightListener = new View.OnClickListener() {
            public void onClick(View v) {
                String temp = "";
                for (Map.Entry<String, Integer> ShoppingEntry : Shopping.entrySet()) {
                    String ShoppingKey = ShoppingEntry.getKey();
                    Integer ShoppingCount = ShoppingEntry.getValue();

                    temp += ShoppingKey + "/" + ShoppingCount + "-";
                }

                RelativeLayout dialogRootView = (RelativeLayout)v.getParent().getParent();
                EditText et_title = (EditText)dialogRootView.findViewById(R.id.dialog_input);
                String cart_title = et_title.getText().toString();
                dbmanger.insertCartData(sDate,cart_title,temp);
                mCustomDialog.dismiss();
                Toast.makeText(getApplicationContext(), "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();

            }
        };

        ImageView iv_Cart = (ImageView)findViewById(R.id.detail_info_cart);
        iv_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomDialog = new CartSaveDialog(ResultDetailActivity.this, leftListener,rightListener);
                mCustomDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        RelativeLayout rl_Dialog = (RelativeLayout)mCustomDialog.findViewById(R.id.dialog_root);
                        rl_Dialog.animate().translationY(-400).withLayer();

                    }
                });
                mCustomDialog.show();
            }
        });

        RelativeLayout averageInfo = (RelativeLayout)findViewById(R.id.detail_average_info);

        title = (TextView)findViewById(R.id.detail_title);
        totalPrice = (TextView)findViewById(R.id.detail_totalPrice);
        nTotalPrice = 0;

        adapter = new ResultDetailListviewAdapter();
        detailItem = (ListView)findViewById(R.id.detail_list);
        detailItem.setAdapter(adapter);

        for (Map.Entry<String, Integer> ShoppingEntry : Shopping.entrySet()) {
            String ShoppingKey = ShoppingEntry.getKey();
            Integer ShoppingCount = ShoppingEntry.getValue();

            if(Price.get(ShoppingKey) != null) {
                Log.d("ye0jun-log",ShoppingKey + Price.get(ShoppingKey));
                adapter.addItem(ShoppingKey, Price.get(ShoppingKey),ShoppingCount,false);
                nTotalPrice += Price.get(ShoppingKey);
            }
            else {
                averageInfo.setVisibility(View.VISIBLE);
                adapter.addItem(ShoppingKey, Average.get(ShoppingKey).getAveragePrice(), ShoppingCount,true);
                nTotalPrice += Average.get(ShoppingKey).getAveragePrice();
            }
        }

        setListViewHeightBasedOnChildren(detailItem);
        adapter.notifyDataSetChanged();

        totalPrice.setText(NumberFormat.getIntegerInstance(Locale.US).format(nTotalPrice) + "원");

        final ScrollView scrollView = (ScrollView)findViewById(R.id.detail_scroll);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                bTouch = true;
                return false;
            }
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                if(!bTouch)
                    scrollView.scrollTo(0,0);

                int scrollPivot = scrollView.findViewById(R.id.detail_title_height).getMeasuredHeight();

                if(scrollY > scrollPivot)
                    title.setText(getIntent().getStringExtra("martName"));
                else
                    title.setText("가격보기");

            }
        });

        final SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageView transparent = (ImageView)findViewById(R.id.imagetrans);
        transparent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        double martLocationX = getIntent().getDoubleExtra("martLocationX", 0.0f);
        double martLocationY = getIntent().getDoubleExtra("martLocationY", 0.0f);

        LatLng mart = new LatLng(martLocationX, martLocationY);
        Log.d("ye0jun-onMapReady", mart.toString());
        googleMap.addMarker(new MarkerOptions().position(mart).title(getIntent().getStringExtra("martName")));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mart, 17.0f));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+getIntent().getStringExtra("Number")));
                    startActivity(in);
                    Log.d("ye0jun-success", "권한획득");
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다

                    Log.d("ye0jun-success", "권한거부");
                }
                return;
        }
    }
}
