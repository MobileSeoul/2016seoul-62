package com.example.ye0jun.seoulprice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    HashMap<String, Integer> Shopping = new HashMap<String, Integer>();
    HashMap<String, martInfo> martLocation = new HashMap<String, martInfo>();
    HashMap<String, JsonArray> martPriceData = new HashMap<>();
    HashMap<String, AverageCounter> martPriceAverage = new HashMap<>();
    String baseUrl = "http://openapi.seoul.go.kr:8088/";
    String apiKey = "6e4874596279653039364a58427969";
    String etc = "/json/ListNecessariesPricesService/1/1000/";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Animation animation;
    double scale;
    int currentCount;
    FrameLayout resultButton = null;
    int fragmentState;
    int dataLength;
    String itemNames[] = {"목정이가 좋아하는 오이","무","배","배추","사과","상추","애호박","양파","오이","호박","조기","고등어","동태","명태","오징어",
    "달걀","닭고기","삼겹살","쇠고기"};

    final static int REQUEST_LOCATION = 199;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        scale = getResources().getDisplayMetrics().density;
        currentCount = 0;
        dataLength = 0;
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothic.otf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NanumBarunGothicBold.otf"))
                .addItalic(Typekit.createFromAsset(this, "fonts/NanumBarunGothicLight.otf"));

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        seoulMapSetting();
        AndroidNetworking.initialize(getApplicationContext());
        for (Map.Entry<String, martInfo> entry : getMartInformation().entrySet()) {
            String key = entry.getKey();
            MainActivity.martInfo MartInfo = entry.getValue();
            getData(key, MartInfo.makeSpecificUrl(),MartInfo.makeSpecificUrlPrev());
            dataLength++;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SearchFragment search = new SearchFragment();
        fragmentTransaction.add(R.id.fragment_container, search,"search");
        fragmentTransaction.commit();
        fragmentState = 1;
    }
//5549913
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if(fragmentState == 1) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
        else
            super.onBackPressed();
    }

    public void getData(final String key, String url, final String prevUrl) {
        AndroidNetworking.get(url)
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        JsonParser parser = new JsonParser();

                        JsonElement jsonElement = parser.parse(response).getAsJsonObject().get("ListNecessariesPricesService");
                        if (jsonElement != null) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            JsonArray data = jsonObject.get("row").getAsJsonArray();
                            if(key.contentEquals("구로시장")) {
                                for(int i=0; i<data.size(); i++){
                                    if(data.get(0).getAsJsonObject().get("M_SEQ").getAsInt() == 108)
                                        data.remove(i);
                                }
                            }

                            martPriceData.put(key, data);
                            for (int i = 0; i < data.size(); i++) {
                                String _itemName = data.get(i).getAsJsonObject().get("A_NAME").toString();
                                int check = data.get(i).getAsJsonObject().get("A_SEQ").getAsInt();
                                for (String _specificItemName : itemNames) {
                                    if (_itemName.contains(_specificItemName)) {
                                        if(_specificItemName.equals("배") && check != 306)
                                            continue;
                                        if(_specificItemName.equals("호박") && check == 312)
                                            continue;

                                        int _itemPrice = data.get(i).getAsJsonObject().get("A_PRICE").getAsInt();
                                        AverageCounter tempCounter = martPriceAverage.get(_specificItemName);
                                        if (tempCounter == null)
                                            tempCounter = new AverageCounter();

                                        tempCounter.addPrice(_itemPrice);
                                        martPriceAverage.put(_specificItemName, tempCounter);
                                        break;
                                    }
                                }
                            }
                        } else {
                            getData(key, prevUrl, null);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    public void changeState(String state) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (state) {
            case "result":
                ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                ft.replace(R.id.fragment_container, new SearchResultFragment(), "result");
                ft.addToBackStack(null);
                ft.commit();
                setFragmentState(2);
                break;
            case "nearby":
                ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                ft.replace(R.id.fragment_container, new NearbyFragment(), "nearby");
                ft.addToBackStack(null);
                ft.commit();
                break;
            case "oneeye":
                ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                ft.replace(R.id.fragment_container, new OneEyeFragment(), "oneeye");
                ft.addToBackStack(null);
                ft.commit();
                break;
            case "recent":
                ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                ft.replace(R.id.fragment_container, new RecentCartFragment(), "recent");
                ft.addToBackStack(null);
                ft.commit();
                break;
            case "appinfo":
                ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                ft.replace(R.id.fragment_container, new AppInfoFragment(), "appinfo");
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
    }

    public void setFragmentState(int n){
        fragmentState = n;
    }

    public void gPlusButtonClick(View globalView, View itemView) {
        String sButtonTag = itemView.getTag() + "";
        String sMinusButtonId = sButtonTag.substring(0, sButtonTag.indexOf('/'));
        String sItemName = sButtonTag.substring(sButtonTag.indexOf('/') + 1, sButtonTag.length());
        int nMinusButtonId = getResources().getIdentifier(sMinusButtonId, "id", getPackageName());
        SearchFragment fragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        Button minus = (Button) globalView.findViewById(nMinusButtonId);
        Button plus = (Button) itemView;
        resultButton = (FrameLayout)fragment.getView().findViewById(R.id.showResult);
//        resultButton.setLayoutParams(new FrameLayout.LayoutParams(10,10));

        final int animationMargin = 59 * (int)scale;
        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) resultButton.getLayoutParams();
                params.bottomMargin = (-59*(int)scale) +  (int)(animationMargin * interpolatedTime);
                resultButton.setLayoutParams(params);
            }
        };
        a.setDuration(500); // in ms


        if (Shopping.get(sItemName) == null)
            Shopping.put(sItemName, 1);
        else
            Shopping.put(sItemName, Shopping.get(sItemName) + 1);

        String sPlusState = plus.getText().toString();

        if (sPlusState.equals("+")) {
            plus.setText("1");
            plus.setBackgroundResource(R.drawable.add_button_full);
            plus.setTextColor(Color.parseColor("#ffffff"));
        } else {
            String tmp = (Integer.parseInt(plus.getText().toString()) + 1) + "";
            plus.setText(tmp);
        }
        if(currentCount == 0)
            resultButton.startAnimation(a);
        currentCount += 1;
        minus.setVisibility(View.VISIBLE);
    }

    public void gMinusButtonClick(View globalView, View itemView) {
        String sButtonTag = itemView.getTag() + "";
        String sPlusButtonId = sButtonTag.substring(0, sButtonTag.indexOf('/'));
        String sItemName = sButtonTag.substring(sButtonTag.indexOf('/') + 1, sButtonTag.length());
        int nPlusButtonId = getResources().getIdentifier(sPlusButtonId, "id", getPackageName());

        Button plus = (Button) globalView.findViewById(nPlusButtonId);
        Button minus = (Button) itemView;

        SearchFragment fragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag("search");
        resultButton = (FrameLayout)fragment.getView().findViewById(R.id.showResult);
        final int animationMargin = -59 * (int)scale;
        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) resultButton.getLayoutParams();
                params.bottomMargin = (int)(animationMargin * interpolatedTime);
                resultButton.setLayoutParams(params);
            }
        };
        a.setDuration(500); // in ms

        if (Integer.parseInt(plus.getText().toString()) > 0) {
            String tmp = (Integer.parseInt(plus.getText().toString()) - 1) + "";
            plus.setText(tmp);
            Shopping.put(sItemName, Shopping.get(sItemName) - 1);
        }

        if (Integer.parseInt(plus.getText().toString()) == 0) {
            Shopping.remove(sItemName);
            plus.setText("+");
            minus.setVisibility(View.INVISIBLE);
            plus.setBackgroundResource(R.drawable.add_button_empty);
            plus.setTextColor(Color.parseColor("#0a91c1"));
        }
        currentCount -= 1;
        if(currentCount == 0)
            resultButton.startAnimation(a);
    }

    public String getUrl(String placeName, String itemName, String Date, String placeType, String Gu) {
        return baseUrl + apiKey + etc + placeName + "/" + itemName + "/" + Date + "/" + placeType + "/" + Gu;
    }

    public String getCurrentDate() {
        String currentDateandTime = new SimpleDateFormat("yyyy-MM", Locale.KOREA).format(new Date());
        return currentDateandTime;
    }

    public String getPrevDate() {
        String prevDate = "";
        String Year = new SimpleDateFormat("yyyy", Locale.KOREA).format(new Date());
        String prevMonth = new SimpleDateFormat("MM", Locale.KOREA).format(new Date());
        int prev = Integer.parseInt(prevMonth.substring(0,prevMonth.length()));
        if(prev < 11)
            prevDate = Year + "-" + "0" + (prev-1);
        else
            prevDate = Year + "-" + (prev-1);

        return prevDate;
    }


    public void putShoppingItem(String ItemName,int count){ Shopping.put(ItemName,count); }

    public HashMap<String, martInfo> getMartInformation() {
        return martLocation;
    }

    public HashMap<String, Integer> getShoppingInformation() {
        return Shopping;
    }

    public HashMap<String, AverageCounter> getMartPriceAverage() { return martPriceAverage; }

    public HashMap<String, JsonArray> getMartPriceData() {
        return martPriceData;
    }

    public void seoulMapSetting() {
        martLocation.put("홈플러스 중계점", new martInfo(new LatLng(37.6398053, 127.0664643), "홈플러스중계점","서울특별시 노원구 동일로204가길 12 홈플러스중계점","02-986-2080"));
        martLocation.put("이마트 목동점", new martInfo(new LatLng(37.5255625, 126.8687439), "이마트목동점","서울특별시 양천구 오목로 299 이마트목동점","02-6923-1234"));
        martLocation.put("하나로마트 목동점", new martInfo(new LatLng(37.5291487, 126.8738196), "목동점(하나로마트)","서울특별시 양천구 목동 917-6","02-6678-9650"));
        martLocation.put("목3동시장", new martInfo(new LatLng(37.5482298, 126.858065), "목3동시장","서울특별시 양천구 목동중앙북로 29","02-2644-3238"));
        martLocation.put("신영시장", new martInfo(new LatLng(37.5319254, 126.8342956), "신영시장","서울특별시 양천구 곰달래로13길 39-1","02-2604-3319"));
        martLocation.put("남구로시장", new martInfo(new LatLng(37.4902923, 126.877185), "남구로시장","서울특별시 구로구 구로동로26길 33","02-855-8509"));
        martLocation.put("마천중앙시장", new martInfo(new LatLng(37.498092, 127.1417758), "마천중앙시장","서울특별시 송파구 마천로45길 23","02-431-2808"));
        martLocation.put("방이시장", new martInfo(new LatLng(37.5105441, 127.1140189), "방이시장","서울특별시 송파구 백제고분로48길 41","070-8953-3811"));
        martLocation.put("광장시장", new martInfo(new LatLng(37.5700888, 126.9971467), "광장시장","서울특별시 종로구 창경궁로 88","02-2267-0291"));

        martLocation.put("롯데백화점 본점", new martInfo(new LatLng(37.5644044, 126.9795366), "롯데백화점","서울특별시 중구 남대문로 81 롯데백화점본점","02-771-2500"));
        martLocation.put("신세계백화점 본점", new martInfo(new LatLng(37.5602856, 126.9785863), "신세계백화점","서울특별시 중구 소공로 63 신세계백화점본점","1588-1234"));
        martLocation.put("통인시장", new martInfo(new LatLng(37.5807168, 126.9677935), "통인시장","서울특별시 종로구 자하문로15길 18","02-722-0911"));
        martLocation.put("남대문시장", new martInfo(new LatLng(37.5591786, 126.9754805), "남대문시장","서울특별시 중구 남대문시장4길 21","02-753-2805"));
        martLocation.put("서울중앙시장", new martInfo(new LatLng(37.5667483, 127.011036), "서울중앙시장","서울특별시 중구 퇴계로85길 22","02-2232-9559"));

        martLocation.put("이마트 청계천점", new martInfo(new LatLng(37.5709931, 127.0206267), "이마트청계점","서울특별시 중구 청계천로 400 이마트청계천점","02-2290-1234"));
        martLocation.put("롯데마트 서울역점", new martInfo(new LatLng(37.5557821, 126.9683167), "롯데마트서울역점","서울특별시 중구 청파로 426","02-390-2500"));
        martLocation.put("이마트 용산점", new martInfo(new LatLng(37.5294558, 126.9633387), "이마트용산점","서울특별시 용산구 한강대로23길 55 이마트용산점","02-2012-1234"));
        martLocation.put("하나로마트 용산점", new martInfo(new LatLng(37.533122, 126.9625053), "농협하나로마트용산점","서울특별시 용산구 청파로 122 농협(농산물백화점)","02-2077-2800"));
        martLocation.put("용문시장", new martInfo(new LatLng(37.5359271, 126.9580612), "용문시장","서울특별시 용산구 효창원로42길 30","02-717-3324"));
        martLocation.put("후암시장", new martInfo(new LatLng(37.5502034, 126.9742537), "후암시장","서울특별시 용산구 후암동 103","02-754-5114"));
        martLocation.put("이마트 성수점", new martInfo(new LatLng(37.5421117, 127.0524901), "이마트성수점","서울특별시 성동구 뚝섬로 379 이마트성수점","02-3408-1234"));
        martLocation.put("뚝도시장", new martInfo(new LatLng(37.5383729, 127.051552), "뚝도시장","서울특별시 성동구 성수이로 32-15","02-464-4426"));
        martLocation.put("이마트 왕십리점", new martInfo(new LatLng(37.5617309, 127.0361917), "이마트왕십리점","서울특별시 성동구 왕십리광장로 17 이마트왕십리점","02-2200-1234"));
        martLocation.put("금남시장", new martInfo(new LatLng(37.5483989, 127.0203326), "금남시장","서울특별시 성동구 독서당로 303-7","02-2252-5302"));
        martLocation.put("이마트 자양점", new martInfo(new LatLng(37.5384947, 127.0712334), "이마트자양점","서울특별시 광진구 아차산로 272 이마트자양점","02-2024-1234"));

        martLocation.put("노룬산골목시장", new martInfo(new LatLng(37.5369552, 127.0625306), "노룬산골목시장","서울특별시 광진구 자양동 44-22","02-465-2103"));
        martLocation.put("자양골목시장", new martInfo(new LatLng(37.5337596, 127.0728226), "자양골목시장","서울특별시 광진구 자양1동 631-18","02-458-1624"));
        martLocation.put("롯데마트 강변점", new martInfo(new LatLng(37.535764, 127.0935453), "롯데마트강변점","서울특별시 광진구 광나루로56길 85 테크노마트","02-3424-2500"));
        martLocation.put("롯데백화점 청량리점", new martInfo(new LatLng(37.5803856, 127.0457327), "롯데백화점청량리점","서울특별시 동대문구 왕산로 205 청량리역","02-3707-2500"));
        martLocation.put("청량리종합시장", new martInfo(new LatLng(37.5792997, 127.032088), "청량리종합시장","서울특별시 동대문구 경동시장로 3-1","02-962-7100"));
        martLocation.put("경동시장", new martInfo(new LatLng(37.5793677, 127.0366356), "경동시장","서울특별시 동대문구 왕산로 147","02-967-8721"));
        martLocation.put("홈플러스 동대문점", new martInfo(new LatLng(37.5745349, 127.0365108), "홈플러스동대문점","서울특별시 동대문구 천호대로 133 홈플러스동대문점","02-2173-8000"));
        martLocation.put("우림시장", new martInfo(new LatLng(37.5971166, 127.0960738), "우림시장","서울특별시 관악구 난곡로26길 4","02-864-4488"));
        martLocation.put("동원시장", new martInfo(new LatLng(37.5898118, 127.0876859), "동원시장","서울특별시 중랑구 면목로74길 45 동원시장","02-496-7332"));
        martLocation.put("홈플러스 면목점", new martInfo(new LatLng(37.5981379, 127.0703215), "홈플러스면목점","서울특별시 중랑구 면목천로 33 홈플러스","02-437-2080"));
        martLocation.put("이마트 상봉점", new martInfo(new LatLng(37.596478, 127.0914073), "이마트상봉점","서울특별시 중랑구 상봉로 118 이마트상봉점","02-490-1234"));
        martLocation.put("장위골목시장", new martInfo(new LatLng(37.6102808, 127.0491058), "장위골목시장","서울특별시 성북구 장월로12길 41-1","02-919-6880"));
        martLocation.put("이마트 미아점", new martInfo(new LatLng(37.610891, 127.0276353), "이마트미아점","서울특별시 성북구 도봉로 17 이마트미아점","02-944-1234"));

        martLocation.put("돈암제일시장", new martInfo(new LatLng(37.5917268, 127.0137486), "돈암제일시장","서울특별시 성북구 동소문로18길 12-3","02-926-5133"));
        martLocation.put("현대백화점 미아점", new martInfo(new LatLng(37.6083917, 127.0265525), "현대백화점미아점","서울특별시 성북구 동소문로 315 현대백화점미아점","02-2117-2233"));
        martLocation.put("방학동도깨비시장", new martInfo(new LatLng(37.6644683, 127.0318559), "방학동도깨비시장","서울특별시 도봉구 도당로13다길 29","02-954-1225"));
        martLocation.put("이마트 창동점", new martInfo(new LatLng(37.6516821, 127.0447443), "이마트창동점","서울특별시 도봉구 노해로65길 4 창동E-MART","02-901-1234"));
        martLocation.put("홈플러스 방학점", new martInfo(new LatLng(37.6648017, 127.0415216), "홈플러스방학점","서울특별시 도봉구 도봉로 678 홈플러스방학점","02-912-2080"));
        martLocation.put("신창시장", new martInfo(new LatLng(37.6397214, 127.0349396), "신창시장","서울특별시 도봉구 덕릉로5길 9","02-990-0040"));
        martLocation.put("공릉동도깨비시장", new martInfo(new LatLng(37.6226586, 127.0674684), "공릉동도깨비시장","서울특별시 노원구 동일로180길 37 공릉상가","02-976-4143"));
        martLocation.put("상계중앙시장", new martInfo(new LatLng(37.6592473, 127.0679081), "상계중앙시장","서울특별시 노원구 상계로27길 32","02-936-1161"));
        martLocation.put("롯데백화점 노원점", new martInfo(new LatLng(37.6549977, 127.0589091), "롯데백화점노원점","서울특별시 노원구 동일로 1414 롯데백화점노원점","02-950-2500"));
        martLocation.put("인왕시장", new martInfo(new LatLng(37.5907534, 126.9408947), "인왕시장","서울특별시 서대문구 통일로40안길 13-5","02-391-1307"));
        martLocation.put("현대백화점 신촌점", new martInfo(new LatLng(37.5560909, 126.9336741), "현대백화점신촌점","서울특별시 서대문구 신촌로 83 현대백화점신촌점","02-3145-2233"));
        martLocation.put("롯데슈퍼 창천점", new martInfo(new LatLng(37.5560905, 126.927108), "롯데슈퍼","서울특별시 서대문구 신촌로 13 아륭빌딩","02-338-5601"));
        martLocation.put("영천시장", new martInfo(new LatLng(37.5704445, 126.9589608), "영천시장","서울특별시 서대문구 영천동 268","02-364-1926"));
        martLocation.put("그랜드마트 신촌점", new martInfo(new LatLng(37.5549521, 126.9337853), "그랜드마트신촌점","서울특별시 마포구 신촌로 94 그랜드플라자","02-326-0101"));

        martLocation.put("마포농수산물시장", new martInfo(new LatLng(37.5652002, 126.8963164), "마포농수산물시장","서울특별시 마포구 성산2동 533-1","02-300-5060"));
        martLocation.put("홈플러스 월드컵점", new martInfo(new LatLng(37.5693941, 126.8962052), "홈플러스월드컵점","서울특별시 마포구 월드컵로 240 월드컵주경기장","02-312-2080"));
        martLocation.put("망원시장", new martInfo(new LatLng(37.5567393, 126.9040543), "망원시장","서울특별시 마포구 포은로8길 14","02-335-3591"));
        martLocation.put("홈플러스 강서점", new martInfo(new LatLng(37.559304, 126.8483683), "홈플러스등촌점","서울특별시 강서구 공항대로41길 56 진아빌딩","02-2658-8544"));
        martLocation.put("송화시장", new martInfo(new LatLng(37.548038, 126.8331363), "송화시장","서울특별시 강서구 강서로 263-29","02-2696-5225"));
        martLocation.put("이마트 가양점", new martInfo(new LatLng(37.5579227, 126.8600702), "이마트가양점","서울특별시 강서구 양천로 559 이마트가양점","02-2101-1234"));
        martLocation.put("화곡본동시장", new martInfo(new LatLng(37.5430908, 126.8415168), "화곡본동시장","서울특별시 강서구 까치산로 35","02-2698-0779"));
        martLocation.put("이마트 신도림점", new martInfo(new LatLng(37.5065663, 126.8882651), "이마트신도림점","서울특별시 구로구 구로동 3-25","02-6715-1234"));
        martLocation.put("AK Plaza", new martInfo(new LatLng(37.5006463, 126.8804488), "애경백화점","서울특별시 구로구 구로중앙로 152 AK플라자구로본점","1661-1114"));
        martLocation.put("구로시장", new martInfo(new LatLng(37.4889871, 126.8832643), "구로시장","서울특별시 구로구 구로4동 736-1","02-114"));
        //구분필요 남구로시장과 구로시장
        martLocation.put("고척근린시장", new martInfo(new LatLng(37.5022626, 126.8478229), "고척근린시장","서울특별시 구로구 경인로33길 98-3","02-2060-7504"));
        martLocation.put("남문시장", new martInfo(new LatLng(37.4724714, 126.8999222), "남문시장","서울특별시 금천구 시흥대로144길 17","02-868-9727"));
        martLocation.put("홈플러스 금천점", new martInfo(new LatLng(37.4686287, 126.8947082), "홈플러스독산점","서울특별시 금천구 시흥대로 391 삼성홈플러스","02-890-8114"));

        martLocation.put("대림중앙시장", new martInfo(new LatLng(37.4914315, 126.8974295), "대림중앙시장","서울특별시 영등포구 도림천로11길 26-1","02-114"));
        martLocation.put("영등포전통시장", new martInfo(new LatLng(37.5198269, 126.9060953), "영등포전통시장","서울특별시 영등포구 영등포로 233","02-2634-1308"));
        martLocation.put("홈플러스 영등포점", new martInfo(new LatLng(37.5181781, 126.8937069), "홈플러스영등포점","서울특별시 영등포구 당산로 42 홈플러스영등포점","02-2165-8000"));
        martLocation.put("이마트 여의도점", new martInfo(new LatLng(37.5185398, 126.924315), "이마트여의도점","서울특별시 영등포구 여의동로3길 10 여의도자이","02-2280-1234"));
        martLocation.put("관악신사시장", new martInfo(new LatLng(37.4861269, 126.9150113), "관악신사시장","서울특별시 관악구 신사동 503","02-855-1994"));
        martLocation.put("태평백화점", new martInfo(new LatLng(37.4868502, 126.9794772), "태평백화점","서울특별시 동작구 동작대로 115 태평백화점","02-595-8000"));
        martLocation.put("롯데백화점 영등포점", new martInfo(new LatLng(37.515537, 126.9045957), "롯데백화점영등포점","서울특별시 영등포구 경인로 846 롯데백화점영등포점","02-2632-2500"));
        martLocation.put("남성시장", new martInfo(new LatLng(37.4856003, 126.9781995), "남성시장","서울특별시 동작구 동작대로29길 9","02-536-3002"));
        martLocation.put("원당종합시장", new martInfo(new LatLng(37.4742681, 126.9637013), "원당종합시장","서울특별시 관악구 인헌동 1638-20","02-888-0930"));
        martLocation.put("신원시장", new martInfo(new LatLng(37.4815721, 126.926177), "신원시장","서울특별시 관악구 남부순환로 1578","02-854-5453"));
        martLocation.put("세이브마트", new martInfo(new LatLng(37.474395, 126.9163593), "세이브마트","서울특별시 관악구 난곡로 220 세이브마트","02-856-6022"));
        martLocation.put("롯데백화점 관악점", new martInfo(new LatLng(37.4743937, 126.9010384), "롯데백화점관악점","서울특별시 관악구 봉천로 209 롯데백화점관악점","02-833-2500"));

        martLocation.put("방림시장", new martInfo(new LatLng(37.483325, 126.9940563), "방림시장","서울특별시 서초구 방배동 910-18","02-114"));
        martLocation.put("하나로클럽 양재점", new martInfo(new LatLng(37.4629931, 127.0409163), "하나로클럽양재점","서울특별시 서초구 청계산로 10 농협하나로","02-3498-1100"));
        martLocation.put("신세계백화점 강남점", new martInfo(new LatLng(37.5050983, 127.0019745), "신세계백화점강남점","서울특별시 서초구 신반포로 176 신세계백화점강남점","1588-1234"));
        martLocation.put("롯데백화점 잠실점", new martInfo(new LatLng(37.5122103, 127.0972705), "롯데백화점잠실점","서울특별시 송파구 올림픽로 240 롯데백화점잠실점","02-411-2500"));
        martLocation.put("홈플러스 잠실점", new martInfo(new LatLng(37.5118953, 127.0930387), "홈플러스잠실점","서울특별시 송파구 올림픽로35가길 16 홈플러스잠실점","02-3468-8000"));
        martLocation.put("둔촌역전통시장", new martInfo(new LatLng(37.5274376, 127.1263846), "둔촌역전통시장","서울특별시 강동구 풍성로58길 34","02-6052-5657 "));
        martLocation.put("이마트 명일점", new martInfo(new LatLng(37.554595, 127.1537638), "이마트명일점","서울특별시 강동구 고덕로 276 신세계이마트명일점","02-2145-1234"));
        martLocation.put("암사종합시장", new martInfo(new LatLng(37.5512061, 127.1269332), "암사종합시장","서울특별시 강동구 상암로11길 25","02-442-1040"));
        martLocation.put("홈플러스 강동점", new martInfo(new LatLng(37.5457124, 127.1399779), "홈플러스강동점","서울특별시 강동구 양재대로 1571 홈플러스강동점","02-3400-8000"));
        martLocation.put("홈플러스 시흥점", new martInfo(new LatLng(37.451853, 126.8985694), "홈플러스시흥점","서울특별시 금천구 시흥대로 201 홈플러스시흥점","02-895-2080 "));
        martLocation.put("현대시장", new martInfo(new LatLng(37.4518478, 126.8657387), "현대시장","서울특별시 금천구 독산로41길 7","02-802-6495"));
        martLocation.put("수유재래시장", new martInfo(new LatLng(37.6315058, 127.0202405), "수유재래시장","서울특별시 강북구 수유1동 50-77","02-987-6380"));
        martLocation.put("하나로마트 미아점", new martInfo(new LatLng(37.6215012, 127.0243856), "하나로클럽미아점","서울특별시 강북구 미아동 318-5","02-980-5670"));
        martLocation.put("숭인시장", new martInfo(new LatLng(37.613141, 127.0270803), "숭인시장","서울특별시 강북구 도봉로 45 숭인시장","02-988-1430"));
        martLocation.put("롯데백화점 미아점", new martInfo(new LatLng(37.614641, 127.0283021), "롯데백화점미아점","서울특별시 강북구 도봉로 62 롯데백화점미아점","02-944-2500"));
        martLocation.put("청담삼익시장", new martInfo(new LatLng(37.5224067, 127.0488528), "청담삼익시장","서울특별시 강남구 청담동 134-20","02-547-0225"));

        martLocation.put("도곡시장", new martInfo(new LatLng(37.4973378, 127.049444), "도곡시장","서울특별시 강남구 역삼동","02-114"));
        martLocation.put("이마트 역삼점", new martInfo(new LatLng(37.4991357, 127.0463732), "이마트역삼점","서울특별시 강남구 역삼로 310 이마트역삼점","02-6908-1234"));
        martLocation.put("롯데백화점 강남점", new martInfo(new LatLng(37.4969613, 127.051081), "롯데백화점강남점","서울특별시 강남구 도곡로 401 롯데백화점","02-531-2500"));
        martLocation.put("이마트 은평점", new martInfo(new LatLng(37.6003074, 126.9179244), "이마트은평점","서울특별시 은평구 은평로 111 이마트은평점","02-380-1234"));
        martLocation.put("NC백화점 불광점", new martInfo(new LatLng(37.609862, 126.9264511), "2001아울렛불광점","서울특별시 은평구 불광로 20 NC백화점불광점","02-3417-2001"));
        martLocation.put("대조시장", new martInfo(new LatLng(37.6105286, 126.9273399), "대조시장","서울특별시 은평구 진흥로 153","02-382-5599"));
        martLocation.put("대림시장", new martInfo(new LatLng(37.5868136, 126.9089592), "대림시장","서울특별시 은평구 응암동 300-10","02-302-1666"));
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        // only stop if it's connected, otherwise we crash
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            Log.d("ye0jun",mLastLocation.toString());
        }
    }

    public Location getCurrentLocation(){
        if(mGoogleApiClient.isConnected()) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(1);
            mLocationRequest.setFastestInterval(1);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                return mLastLocation;
            }
        }
        return null;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.d("Location", location.toString());
    }

    public void resetCurrentCount(){
        currentCount = 0;
        Shopping.clear();
    }

//    martLocation.put("현대시장", new martInfo(new LatLng(37.4518478, 126.8657387), "현대시장"));

    public class martInfo {
        LatLng mLocation;
        String mSearchValue;
        String mAddress;
        String mTel;

        public martInfo(LatLng location, String searchValue,String address,String tel) {
            mLocation = location;
            mSearchValue = searchValue;
            mAddress = address;
            mTel = tel;
        }

        public String getAddress() { return mAddress; }

        public String getNumber() { return mTel; }

        public LatLng getLocation() {
            return mLocation;
        }

        public String getSearchValue() {
            return mSearchValue;
        }

        public String makeSpecificUrl() {
            String Gu = " ";

            if (mSearchValue.equals("롯데백화점") || mSearchValue.equals("신세계백화점"))
                Gu = "중구";

            return getUrl(mSearchValue, " ", getCurrentDate(), " ", Gu);
        }

        public String makeSpecificUrlPrev() {
            String Gu = " ";

            if (mSearchValue.equals("롯대백화점") || mSearchValue.equals("신세계백화점"))
                Gu = "중구";

            return getUrl(mSearchValue, " ", getPrevDate(), " ", Gu);
        }
    }

    public void getLocationPermission(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        finish();
                        break;
                    }
                    case Activity.RESULT_OK:{
                        SearchResultFragment fragment = (SearchResultFragment) getSupportFragmentManager().findFragmentByTag("result");
                        fragment.setGpsStatus(true);
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

    }
}
