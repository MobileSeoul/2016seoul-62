package com.example.ye0jun.seoulprice;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

/**
 * Created by ye0jun on 2016. 8. 17..
 */
public class ImageViewpagerAdapter extends PagerAdapter {

    LayoutInflater layoutInflater;
    Context mContext;
    MainActivity mainActivity;
    int[] mResources = {
            R.drawable.banner01,
            R.drawable.banner02,
            R.drawable.banner03
    };

    public ImageViewpagerAdapter(Context context,MainActivity a){
        mContext = context;
        layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainActivity = a;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        View view = null;
        view = layoutInflater.inflate(R.layout.viewpager_image,null);
        ImageView img = (ImageView)view.findViewById(R.id.viewpager_childImage);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (position){
                    case 0:
                        mainActivity.changeState("recent");
                        break;
                    case 1:
                        mainActivity.changeState("oneeye");
                        break;
                    case 2:
                        mainActivity.changeState("nearby");
                        break;
                }
            }
        });

        Glide.with(mContext).load(mResources[position]).centerCrop().into(img);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container,int position, Object object){
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
