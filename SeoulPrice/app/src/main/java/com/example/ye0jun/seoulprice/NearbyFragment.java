package com.example.ye0jun.seoulprice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ye0jun on 2016. 10. 24..
 */

public class NearbyFragment extends Fragment implements OnMapReadyCallback {

    RelativeLayout warningView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) (getActivity())).setFragmentState(2);
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        warningView = (RelativeLayout) view.findViewById(R.id.warning_root);

        Button warningButton = (Button) view.findViewById(R.id.warning_button);
        warningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    warningView.setVisibility(View.GONE);
                    Log.d("ye0jun-success", "권한획득");
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                    warningView.setVisibility(View.VISIBLE);
                    Log.d("ye0jun-success", "권한거부");
                }
                return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        googleMap.setMyLocationEnabled(true);

        for(Map.Entry<String,MainActivity.martInfo> entry : ((MainActivity)getActivity()).getMartInformation().entrySet() ){
            String key = entry.getKey();
            MainActivity.martInfo MartInfo = entry.getValue();

            googleMap.addMarker(newMarker(MartInfo.getLocation(),key));

        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.55042542973083, 126.98934104293585), 12.0f));
    }

    public MarkerOptions newMarker(LatLng location, String placeName){
        MarkerOptions markerOptions = new MarkerOptions().position(location).title(placeName);
        return markerOptions;
    }
}
