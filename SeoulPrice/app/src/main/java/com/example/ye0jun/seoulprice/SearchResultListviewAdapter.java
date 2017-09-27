package com.example.ye0jun.seoulprice;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ye0jun on 2016. 8. 16..
 */
public class SearchResultListviewAdapter extends BaseAdapter{

    private ArrayList<SearchResultListview> listviewItemList = new ArrayList<SearchResultListview>();
    private View rootView;
    private ImageView iv_Reveal;
    private Animator anim;
    private Animation anim2;
    private MainActivity mainActivity;
    private Context mContext;
    HashMap<String, Integer> forintent = new HashMap<>();

    public SearchResultListviewAdapter(MainActivity a,Context b){
        mainActivity = a;
        mContext = b;
    }

    @Override
    public int getCount() {
        return listviewItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return listviewItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int pos = i;
        Context context = viewGroup.getContext();

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_result,viewGroup,false);
            rootView = view;
        }




        TextView tv_martName = (TextView)view.findViewById(R.id.cartName);
        TextView tv_martDistance = (TextView)view.findViewById(R.id.cartDate);
        TextView tv_martPrice = (TextView)view.findViewById(R.id.martPrice);
        View v_martSelected = view.findViewById(R.id.selected);

        final SearchResultListview listviewItem = listviewItemList.get(pos);

        ImageView iv_select = (ImageView)view.findViewById(R.id.btn_detail);
        iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                forintent.clear();
                TextView transitionText = (TextView) rootView.findViewById(R.id.cartName);
                TextView martDistance = (TextView) rootView.findViewById(R.id.cartDate);
                Intent intent = new Intent(mContext, ResultDetailActivity.class);
                intent.putExtra("martName", listviewItem.getMartName());
                intent.putExtra("martDistance", listviewItem.getMartDistance());
                intent.putExtra("Shopping", mainActivity.getShoppingInformation());
                intent.putExtra("Average", mainActivity.getMartPriceAverage());
                MainActivity.martInfo tempInfo  = mainActivity.getMartInformation().get(listviewItem.getMartName());
                LatLng martLocation = tempInfo.getLocation();
                String martAddress = tempInfo.getAddress();
                String martNumber = tempInfo.getNumber();
                intent.putExtra("martLocationX", martLocation.latitude);
                intent.putExtra("martLocationY", martLocation.longitude);
                intent.putExtra("Address",martAddress);
                intent.putExtra("Number",martNumber);

                JsonArray priceData = mainActivity.getMartPriceData().get(listviewItem.getMartName());

                if (priceData != null) {
                    for (Map.Entry<String, Integer> ShoppingEntry : mainActivity.Shopping.entrySet()) {
                        String ShoppingKey = ShoppingEntry.getKey();
                        Integer ShoppingCount = ShoppingEntry.getValue();

                        for (int i = 0; i < priceData.size(); i++) {
                            String itemName = priceData.get(i).getAsJsonObject().get("A_NAME").toString();
                            if (itemName.contains(ShoppingKey)) {
                                if (ShoppingKey.contentEquals("호박") && itemName.contains("애호박"))
                                    break;

                                if (priceData.get(i).getAsJsonObject().get("A_PRICE").getAsInt() != 0) {
                                    forintent.put(ShoppingKey, priceData.get(i).getAsJsonObject().get("A_PRICE").getAsInt() * ShoppingCount);
                                }
                                break;
                            }

                        }
                    }
                }

                intent.putExtra("martPriceData", forintent);

                RelativeLayout rl_parent = (RelativeLayout)view2.getParent();
                TextView transitionText2 = (TextView)rl_parent.findViewById(R.id.cartName);


                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(mainActivity, transitionText2, "placeName");
                mainActivity.startActivity(intent, options.toBundle());
            }
        });

        tv_martName.setText(listviewItem.getMartName());
        tv_martDistance.setText(listviewItem.getMartDistance());
        tv_martPrice.setText(listviewItem.getMartPrice());

        if(listviewItem.getbMartSelected()) {
            v_martSelected.setVisibility(View.VISIBLE);
            final View finalView = view.findViewById(R.id.for_reveal);
            final ImageView iv_Reveal = (ImageView)finalView.findViewById(R.id.view_reveal2);
            finalView.post(new Runnable(){

                @Override
                public void run()
                {
                    int cx = (finalView.getLeft() + finalView.getRight()) / 2;
                    int cy = (finalView.getTop() + finalView.getBottom()) / 2;

                    int finalRadius = Math.max(iv_Reveal.getWidth(), iv_Reveal.getHeight());
                    anim = ViewAnimationUtils.createCircularReveal(iv_Reveal, cx, cy, 0, finalRadius);
                    anim.setDuration(700);

                    anim2 = AnimationUtils.loadAnimation(finalView.getContext(), R.anim.alpha);
                    anim2.setFillAfter(true);

                    anim.start();
                    iv_Reveal.startAnimation(anim2);
                }
            });
        }
        else {
            v_martSelected.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private void show(View RootView,final View view) {
        // get the center for the clipping circle
        int cx = (RootView.getLeft() + RootView.getRight()) / 2;
        int cy = (RootView.getTop() + RootView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                0, finalRadius);
        anim.setDuration(500);

        final Animation anim2 = AnimationUtils.loadAnimation(view.getContext(), R.anim.alpha);
        anim2.setFillAfter(true);
        view.startAnimation(anim2);

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void addItem(String name,Float distance,int price){
        SearchResultListview item = new SearchResultListview();

        item.setMartName(name);
        item.setMartDistance(distance);
        item.setMartPrice(price);

        listviewItemList.add(item);
    }

    public void deleteAll(){
        listviewItemList.clear();
    }

    public ArrayList<SearchResultListview> getAllItem(){
        return listviewItemList;
    }

    public void setAllItem(ArrayList<SearchResultListview> solted){
        listviewItemList = solted;
    }
}
