package com.example.ye0jun.seoulprice;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by ye0jun on 2016. 10. 31..
 */

public class AppInfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) (getActivity())).setFragmentState(2);
        View view = inflater.inflate(R.layout.fragment_appinfo, container, false);
        ImageView iv = (ImageView)view.findViewById(R.id.appinfo);
        Glide.with(getContext()).load(R.drawable.appinfo).centerCrop().into(iv);
        return view;
    }
}
