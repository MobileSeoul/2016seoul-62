package com.example.ye0jun.seoulprice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ye0jun on 16. 8. 8..
 */
public class SearchFragment extends Fragment{
    ArrayList<View> dots = null;
    ViewPager viewPager = null;
    View view;

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity) getActivity()).resetCurrentCount();
        dots = new ArrayList<View>();

        ((MainActivity) (getActivity())).setFragmentState(1);
        view = inflater.inflate(R.layout.fragment_search, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("농산물"));
        tabLayout.addTab(tabLayout.newTab().setText("수산물"));
        tabLayout.addTab(tabLayout.newTab().setText("축산물"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

        NavigationView nav = (NavigationView) view.findViewById(R.id.nav_view);
        nav.setItemIconTintList(null);

        DbOpenHelper dbmanger = new DbOpenHelper(getContext());
        ArrayList<String> check = dbmanger.selectAllAdrress();

        TextView tv_Address = (TextView)nav.getHeaderView(0).findViewById(R.id.nav_textview);
        tv_Address.setText(check.get(0));

        RelativeLayout rl_setAddress = (RelativeLayout)nav.getHeaderView(0).findViewById(R.id.nav_address);
        rl_setAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),JoinActivity.class);
                intent.putExtra("edit","true");
                startActivity(intent); // 로딩이 끝난후 이동할 Activity
                getActivity().finish(); // 로딩페이지 Activity Stack에서 제거
//                Log.d("ye0jun-click","click");
            }
        });

        final DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer);
        RelativeLayout drawerIcon = (RelativeLayout) view.findViewById(R.id.drawerOpen);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawer.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (item.getItemId()) {
                    case R.id.nav_recent:
                        ((MainActivity) getActivity()).changeState("recent");
                        break;
                    case R.id.nav_oneeye:
                        ((MainActivity) getActivity()).changeState("oneeye");
                        break;
                    case R.id.nav_nearby:
                        ((MainActivity) getActivity()).changeState("nearby");
                        break;
                    case R.id.nav_appinfo:
                        ((MainActivity) getActivity()).changeState("appinfo");
                        break;
                }
                return false;
            }
        });


        ViewPager pagerTop = (ViewPager) view.findViewById(R.id.pagerTop);
        ImageViewpagerAdapter imageAdapter = new ImageViewpagerAdapter(getContext(),(MainActivity)getActivity());
        pagerTop.setAdapter(imageAdapter);

        LinearLayout indicatorContainer = (LinearLayout) view.findViewById(R.id.indicator);

        final float scale = getResources().getDisplayMetrics().density;
        final int circleSmallSize = (int) (7 * scale);
        final int circleBigSizeHeight = (int) (7 * scale);
        final int circleBigSizeWidth = (int) (16 * scale);
        final LinearLayout.LayoutParams smallDot = new LinearLayout.LayoutParams(circleSmallSize, circleSmallSize);
        final LinearLayout.LayoutParams bigDot = new LinearLayout.LayoutParams(circleBigSizeWidth, circleBigSizeHeight);
        smallDot.leftMargin = (int) (3 * scale);
        smallDot.rightMargin = (int) (3 * scale);
        bigDot.leftMargin = (int) (3 * scale);
        bigDot.rightMargin = (int) (3 * scale);
        for (int i = 0; i < imageAdapter.getCount(); i++) {
            View v = new View(getContext());
            if (i == 0) {
                v.setLayoutParams(bigDot);
                v.setBackgroundResource(R.drawable.dot_selected);
            } else {
                v.setLayoutParams(smallDot);
                v.setBackgroundResource(R.drawable.dot);
                v.getBackground().setAlpha(120);
            }
            indicatorContainer.addView(v);
            dots.add(v);
        }

        pagerTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dots.size(); i++) {
                    dots.get(i).setLayoutParams(smallDot);
                    dots.get(i).setBackgroundResource(R.drawable.dot);
                    dots.get(i).getBackground().setAlpha(120);
                }
                Log.d("ye0jun-dots", position + "position");

                dots.get(position).setLayoutParams(bigDot);
                dots.get(position).setBackgroundResource(R.drawable.dot_selected);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        viewPager = (ViewPager) view.findViewById(R.id.pager);
        SearchPagerAdapter adapter = new SearchPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                double phoneHeight = displayMetrics.heightPixels / displayMetrics.density;
                double phoneWidth = displayMetrics.widthPixels / displayMetrics.density;
                double scale = getResources().getDisplayMetrics().density;

                View customIndicator = view.findViewById(R.id.customIndicator);
                RelativeLayout.LayoutParams indicatorParam = (RelativeLayout.LayoutParams) customIndicator.getLayoutParams();

                indicatorParam.width = (int) ((phoneWidth / 3) * scale);
                int indicatorLeftMargin = (position * indicatorParam.width) + (int) (indicatorParam.width * positionOffset);
                indicatorParam.leftMargin = indicatorLeftMargin;
                customIndicator.setLayoutParams(indicatorParam);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();

                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                double phoneHeight = displayMetrics.heightPixels / displayMetrics.density;
                double scale = getResources().getDisplayMetrics().density;
                int resourceId = getActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
                int statusbarHeight = getActivity().getResources().getDimensionPixelSize(resourceId);
                double bannerHeight = 180;
                double tabTitleHeight = 58.5;
                double customIndicatorHeight = 5;
                double tabHeight = 42.5;
                double topSizeAll = tabTitleHeight + customIndicatorHeight + tabHeight;

                //여분을 구해서 자연스럽게하기.. 폰 세로 크기에서 tab layout height 와 button height, list height를 뺀 나머지를 dpHeight에 더한다
                double margin = phoneHeight - statusbarHeight - topSizeAll;
                double margin2 = phoneHeight - (58.5 + 5 + 30 + 42.5 + 323);

                int tmptmp = displayMetrics.heightPixels;

                int dpHeight = 0;
                switch (tab.getPosition()) {
                    case 0:
                        dpHeight = (int) (((80 * 9) + 80) * scale);
                        break;
                    case 1:
                        dpHeight = (int) (((80 * 5) + 80) * scale);
                        break;
                    case 2:
                        dpHeight = (int) (((80 * 5) + 80) * scale);
                        break;
                }

                layoutParams.height = dpHeight;
                viewPager.setLayoutParams(layoutParams);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Button.OnClickListener mClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeState("result");
            }
        };

        Button searchButton = (Button) view.findViewById(R.id.showResult_button);
        searchButton.setOnClickListener(mClickListener);

        return view;
    }
}

class SearchPagerAdapter extends FragmentStatePagerAdapter {
    int nNumOfTabs;

    public SearchPagerAdapter(FragmentManager fm,int NumOfTabs){
        super(fm);
        this.nNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position){
        switch(position){
            case 0:
                SearchNongsanFragment searchNongsanFragment = new SearchNongsanFragment();
                return searchNongsanFragment;
            case 1:
                SearchSusanFragment searchSusanFragment = new SearchSusanFragment();
                return searchSusanFragment;
            case 2:
                SearchChooksanFragment searchChooksanFragment = new SearchChooksanFragment();
                return searchChooksanFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return nNumOfTabs;
    }
}

class SearchNongsanFragment extends Fragment {
    View globalView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_search_nongsan,container,false);
        globalView = view;
        ButterKnife.bind(this,view);

        return view;
    }

    @OnClick({R.id.nongsan_plusButton_1,R.id.nongsan_plusButton_2,R.id.nongsan_plusButton_3,
            R.id.nongsan_plusButton_4,R.id.nongsan_plusButton_5,R.id.nongsan_plusButton_6,
            R.id.nongsan_plusButton_7,R.id.nongsan_plusButton_8,R.id.nongsan_plusButton_9})
    public void plusButtonClick(View itemView){
        ((MainActivity)getActivity()).gPlusButtonClick(globalView,itemView);
    }

    @OnClick({R.id.nongsan_minusButton_1,R.id.nongsan_minusButton_2,R.id.nongsan_minusButton_3,
            R.id.nongsan_minusButton_4,R.id.nongsan_minusButton_5,R.id.nongsan_minusButton_6,
            R.id.nongsan_minusButton_7,R.id.nongsan_minusButton_8,R.id.nongsan_minusButton_9})
    public void minusButtonClick(View itemView){
        ((MainActivity)getActivity()).gMinusButtonClick(globalView,itemView);
    }
}

class SearchSusanFragment extends Fragment {
    View globalView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_search_susan,container,false);
        globalView = view;
        ButterKnife.bind(this,view);

        return view;
    }

    @OnClick({R.id.susan_plusButton_1,R.id.susan_plusButton_2,R.id.susan_plusButton_3,
            R.id.susan_plusButton_4,R.id.susan_plusButton_5})
    public void plusButtonClick(View itemView){
        ((MainActivity)getActivity()).gPlusButtonClick(globalView,itemView);
    }

    @OnClick({R.id.susan_minusButton_1,R.id.susan_minusButton_2,R.id.susan_minusButton_3,
            R.id.susan_minusButton_4,R.id.susan_minusButton_5})
    public void minusButtonClick(View itemView){
        ((MainActivity)getActivity()).gMinusButtonClick(globalView,itemView);
    }
}

class SearchChooksanFragment extends Fragment {
    View globalView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_search_chooksan,container,false);
        globalView = view;
        ButterKnife.bind(this,view);

        return view;
    }

    @OnClick({R.id.chooksan_plusButton_1,R.id.chooksan_plusButton_2,R.id.chooksan_plusButton_3,
            R.id.chooksan_plusButton_4})
    public void plusButtonClick(View itemView){
        ((MainActivity)getActivity()).gPlusButtonClick(globalView,itemView);
    }

    @OnClick({R.id.chooksan_minusButton_1,R.id.chooksan_minusButton_2,R.id.chooksan_minusButton_3,
            R.id.chooksan_minusButton_4})
    public void minusButtonClick(View itemView){
        ((MainActivity)getActivity()).gMinusButtonClick(globalView,itemView);
    }
}