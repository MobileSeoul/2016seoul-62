package com.example.ye0jun.seoulprice;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ye0jun on 2016. 8. 9..
 */
public class SearchResultFragment extends Fragment implements OnMapReadyCallback {

    SearchResultListviewAdapter adapter;
    ListView listview;
    ArrayList<sortDistance> sort = new ArrayList<sortDistance>();
    ArrayList<Marker> markers = new ArrayList<Marker>();
    HashMap<String, Integer> forintent = new HashMap<>();
    boolean forshow = false;
    boolean bGPSOn = false;
    GoogleMap gGoogleMap;
    Location gCurrentLocation;
    int nSortWay;
    RelativeLayout warningView;
    boolean bMarker;
    View PrevSelectedView;
    SearchResultListview prevSelectedItem;
    Bitmap bitmapMarker;
    DbOpenHelper dbmanger;
    ArrayList<String> address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        adapter = new SearchResultListviewAdapter((MainActivity)getActivity(),getContext());
        PrevSelectedView = null;
        bitmapMarker = null;
        dbmanger = new DbOpenHelper(getContext());
        address = dbmanger.selectAllAdrress();
        setHasOptionsMenu(true);
        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        myToolbar.setTitle("가격보기");
        getActivity().getMenuInflater().inflate(R.menu.sortmenu, myToolbar.getMenu());
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        Button warningButton = (Button) view.findViewById(R.id.warning_button);
        warningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        });

        gGoogleMap = null;
        prevSelectedItem = null;
        nSortWay = 0;
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listview = (ListView) view.findViewById(R.id.resultListView);
        listview.setAdapter(adapter);

        warningView = (RelativeLayout) view.findViewById(R.id.warning_root);


        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view1, int ig, long l) {
                final View tempView = view1;
                if (prevSelectedItem != null) {
                    prevSelectedItem.setMartSelected(false);
                }
                SearchResultListview item = (SearchResultListview) adapter.getItem(ig);
                prevSelectedItem = item;
                item.setMartSelected(true);
                ((BaseAdapter)listview.getAdapter()).notifyDataSetChanged();

                for(int i=0; i<markers.size(); i++){
                    TextView temp = (TextView)tempView.findViewById(R.id.cartName);
                    if(markers.get(i).getTitle().equals(temp.getText().toString())) {
                        dropPinEffect(markers.get(i));
//                        markers.clear();
//                        markers.add(googleMap.addMarker(newMarker(MartInfo.getLocation(), key)));
                    }
                }

                ImageView iv_See_Detail = (ImageView)view1.findViewById(R.id.btn_detail);
//                iv_See_Detail.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view2) {
//                        forintent.clear();
//                        TextView transitionText = (TextView) tempView.findViewById(R.id.cartName);
//                        TextView martDistance = (TextView) tempView.findViewById(R.id.cartDate);
//                        Intent intent = new Intent(getActivity(), ResultDetailActivity.class);
//                        intent.putExtra("martName", transitionText.getText().toString());
//                        intent.putExtra("martDistance", martDistance.getText().toString());
//                        intent.putExtra("Shopping", ((MainActivity) getActivity()).getShoppingInformation());
//                        intent.putExtra("Average", ((MainActivity) getActivity()).getMartPriceAverage());
//                        MainActivity.martInfo tempInfo  = ((MainActivity) getActivity()).getMartInformation().get(transitionText.getText().toString());
//                        LatLng martLocation = tempInfo.getLocation();
//                        String martAddress = tempInfo.getAddress();
//                        String martNumber = tempInfo.getNumber();
//                        intent.putExtra("martLocationX", martLocation.latitude);
//                        intent.putExtra("martLocationY", martLocation.longitude);
//                        intent.putExtra("Address",martAddress);
//                        intent.putExtra("Number",martNumber);
//
//                        JsonArray priceData = ((MainActivity) getActivity()).getMartPriceData().get(transitionText.getText().toString());
//
//                        if (priceData != null) {
//                            for (Map.Entry<String, Integer> ShoppingEntry : ((MainActivity) getActivity()).Shopping.entrySet()) {
//                                String ShoppingKey = ShoppingEntry.getKey();
//                                Integer ShoppingCount = ShoppingEntry.getValue();
//
//                                for (int i = 0; i < priceData.size(); i++) {
//                                    String itemName = priceData.get(i).getAsJsonObject().get("A_NAME").toString();
//                                    if (itemName.contains(ShoppingKey)) {
//                                        if (ShoppingKey.contentEquals("호박") && itemName.contains("애호박"))
//                                            break;
//
//                                        if (priceData.get(i).getAsJsonObject().get("A_PRICE").getAsInt() != 0) {
//                                            forintent.put(ShoppingKey, priceData.get(i).getAsJsonObject().get("A_PRICE").getAsInt() * ShoppingCount);
//                                        }
//                                        break;
//                                    }
//
//                                }
//                            }
//                        }
//
//                        intent.putExtra("martPriceData", forintent);
//
//                        ActivityOptionsCompat options = ActivityOptionsCompat.
//                                makeSceneTransitionAnimation(getActivity(), tempView.findViewById(R.id.cartName), "placeName");
//                        startActivity(intent, options.toBundle());
//                    }
//                });
            }
        });



        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.sortmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_price:
                if (nSortWay != 0) {
                    moveMapFunction(gGoogleMap.getProjection().getVisibleRegion().latLngBounds, 0);
                    nSortWay = 0;
                }
                return true;
            case R.id.sort_place:
                if (nSortWay != 1) {
                    moveMapFunction(gGoogleMap.getProjection().getVisibleRegion().latLngBounds, 1);
                    nSortWay = 1;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gGoogleMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                PermissionSetting(gGoogleMap);
            }
        } else {
            PermissionSetting(gGoogleMap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    warningView.setVisibility(View.GONE);
                    PermissionSetting(gGoogleMap);
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

    public void PermissionSetting(final GoogleMap googleMap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        ((MainActivity) getActivity()).getLocationPermission();
        gCurrentLocation = null;
        gCurrentLocation = ((MainActivity) getActivity()).getCurrentLocation();
        googleMap.setMyLocationEnabled(true);
        seoulMapSetting(googleMap);

        bMarker = false;
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (PrevSelectedView != null)
                    PrevSelectedView.setVisibility(View.INVISIBLE);
                moveMapFunction(googleMap.getProjection().getVisibleRegion().latLngBounds, nSortWay);
                listview.setSelection(0);
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                bMarker = true;
                if (prevSelectedItem != null) {
                    prevSelectedItem.setMartSelected(false);
                }
                marker.showInfoWindow();
                moveMapFunction(googleMap.getProjection().getVisibleRegion().latLngBounds, nSortWay);
                String martName = marker.getTitle();
                for (int i = 0; i < adapter.getCount(); i++) {
                    SearchResultListview item = (SearchResultListview) adapter.getItem(i);
                    prevSelectedItem = item;
                    if (item.getMartName().equals(martName)) {
                        item.setMartSelected(true);
                        listview.smoothScrollToPosition(i);
                        break;
                    }
                }
                ((BaseAdapter)listview.getAdapter()).notifyDataSetChanged();
                return true;
            }
        });
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void seoulMapSetting(GoogleMap googleMap) {
//        boolean bGPSOn = false;
//        if(chkGpsService()){
//            bGPSOn = true;
//        }


        for (Map.Entry<String, MainActivity.martInfo> entry : ((MainActivity) getActivity()).getMartInformation().entrySet()) {
            String key = entry.getKey();
            MainActivity.martInfo MartInfo = entry.getValue();

            markers.add(googleMap.addMarker(newMarker(MartInfo.getLocation(), key)));

            Location destiny = new Location("destiny");
            destiny.setLatitude(MartInfo.getLocation().latitude);
            destiny.setLongitude(MartInfo.getLocation().longitude);

            float distance = 0.0f;
            if (gCurrentLocation != null)
                distance = gCurrentLocation.distanceTo(destiny);

            sort.add(new sortDistance(key, distance));

        }
        for (int i = 0; i < sort.size() - 1; i++) {
            for (int j = i + 1; j < sort.size(); j++) {
                if (sort.get(i).getDistance() > sort.get(j).getDistance()) {
                    sortDistance tmp = sort.get(i);
                    sort.set(i, sort.get(j));
                    sort.set(j, tmp);
                }
            }
        }
        for (int i = 0; i < sort.size() - 1; i++) {
            adapter.addItem(sort.get(i).getPlace(), sort.get(i).mDistance, 10000);
        }
        adapter.notifyDataSetChanged();

        LatLng pivotAddress = getLocationFromAddress(address.get(0));
        if(pivotAddress != null)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pivotAddress, 13.0f));
        else
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.55042542973083, 126.98934104293585), 13.0f));

//        if (gCurrentLocation != null)
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gCurrentLocation.getLatitude(), gCurrentLocation.getLongitude()), 13.0f));
//        else
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.55042542973083, 126.98934104293585), 13.0f));

    }

    public void moveMapFunction(LatLngBounds currentBound, int sortWay) {
        Log.d("ye0jun-map", currentBound.toString());
        adapter.deleteAll();
        for (Map.Entry<String, MainActivity.martInfo> entry : ((MainActivity) getActivity()).getMartInformation().entrySet()) {
            String key = entry.getKey();
            MainActivity.martInfo MartInfo = entry.getValue();

            boolean isContain = currentBound.contains(MartInfo.getLocation());
            if (isContain) {
                Location destiny = new Location("destiny");
                destiny.setLatitude(MartInfo.getLocation().latitude);
                destiny.setLongitude(MartInfo.getLocation().longitude);
                float distance = 0.0f;
                if (gCurrentLocation != null)
                    distance = gCurrentLocation.distanceTo(destiny);

                int price = 0;

                JsonArray priceData = ((MainActivity) getActivity()).getMartPriceData().get(key);

                if (priceData != null) {
                    boolean isItem;
                    for (Map.Entry<String, Integer> ShoppingEntry : ((MainActivity) getActivity()).Shopping.entrySet()) {
                        isItem = false;
                        String ShoppingKey = ShoppingEntry.getKey();
                        Integer ShoppingCount = ShoppingEntry.getValue();
                        for (int i = 0; i < priceData.size(); i++) {
                            String itemName = priceData.get(i).getAsJsonObject().get("A_NAME").toString();
                            if (itemName.contains(ShoppingKey)) {
                                if (ShoppingKey.contentEquals("호박") && itemName.contains("애호박"))
                                    break;
                                price += priceData.get(i).getAsJsonObject().get("A_PRICE").getAsInt() * ShoppingCount;
                                if (priceData.get(i).getAsJsonObject().get("A_PRICE").getAsInt() != 0)
                                    isItem = true;
                                break;
                            }
                        }
                        if (!isItem)
                            price += ((MainActivity) getActivity()).getMartPriceAverage().get(ShoppingKey).getAveragePrice() * ShoppingCount;
                    }
                    adapter.addItem(key, distance, price);
                }
            }
        }
        RelativeLayout emptyView = (RelativeLayout) getView().findViewById(R.id.resultEmptyList);

        if (adapter.getCount() != 0) {
            emptyView.setVisibility(RelativeLayout.INVISIBLE);
            forshow = !forshow;
            if (sortWay == 0)
                sortByPrice(adapter);
            else
                sortByDistance(adapter);

            adapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(RelativeLayout.VISIBLE);
            adapter.notifyDataSetChanged();
        }

    }

    public void sortByDistance(SearchResultListviewAdapter adapter) {
        ArrayList<SearchResultListview> sort = (ArrayList<SearchResultListview>) adapter.getAllItem().clone();

        for (int i = 0; i < sort.size() - 1; i++) {
            for (int j = i + 1; j < sort.size(); j++) {
                if (sort.get(i).getfMartDistance() > sort.get(j).getfMartDistance()) {
                    SearchResultListview tmp = sort.get(i);
                    sort.set(i, sort.get(j));
                    sort.set(j, tmp);
                }
            }
        }

        adapter.deleteAll();
        adapter.setAllItem(sort);
        adapter.notifyDataSetChanged();
    }

    public void sortByPrice(SearchResultListviewAdapter adapter) {
        ArrayList<SearchResultListview> sort = (ArrayList<SearchResultListview>) adapter.getAllItem().clone();

        for (int i = 0; i < sort.size() - 1; i++) {
            for (int j = i + 1; j < sort.size(); j++) {
                if (sort.get(i).getiMartPrice() > sort.get(j).getiMartPrice()) {
                    SearchResultListview tmp = sort.get(i);
                    sort.set(i, sort.get(j));
                    sort.set(j, tmp);
                }
            }
        }

        adapter.deleteAll();
        adapter.setAllItem(sort);
        adapter.notifyDataSetChanged();
    }

    public MarkerOptions newMarker(LatLng location, String placeName) {
//        MarkerOptions markerOptions = new MarkerOptions().position(location).title(placeName);
//        MarkerOptions markerOptions = new MarkerOptions().position(location).title(placeName).icon(BitmapDescriptorFactory.fromResource(R.drawable.join_icon_small));
        int pxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,(float)37.5,getContext().getResources().getDisplayMetrics());
//        int pxWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,(float)34,getContext().getResources().getDisplayMetrics());

        if(bitmapMarker == null)
            bitmapMarker = resizeMapIcons("icon_marker",pxHeight,pxHeight);
        MarkerOptions markerOptions = new MarkerOptions().position(location).title(placeName).icon(BitmapDescriptorFactory.fromBitmap(bitmapMarker));
        return markerOptions;
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getContext().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public class sortDistance {
        String mPlace;
        float mDistance;

        sortDistance(String place, float distance) {
            mPlace = place;
            mDistance = distance;
        }

        public String getPlace() {
            return mPlace;
        }

        public float getDistance() {
            return mDistance;
        }
    }

    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(getActivity());
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }

    public void setGpsStatus(boolean onoff) {
        bGPSOn = onoff;
    }

    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location = null;
            if(address.size() > 0) {
                location = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            }
            else
                return null;

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        marker.showInfoWindow();


        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 1 * t);

                if (t > 0.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 0);
                } else {
//                    marker.showInfoWindow();

                }
            }
        });
    }

}
