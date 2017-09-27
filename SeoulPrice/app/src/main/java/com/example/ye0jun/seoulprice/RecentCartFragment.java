package com.example.ye0jun.seoulprice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by ye0jun on 2016. 10. 30..
 */

public class RecentCartFragment extends Fragment {

    CartListviewAdapter adapter;
    ListView cartListview;
    DbOpenHelper dbManager;
    ArrayList<cartDataStruct> datas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_recent_cart, container, false);
        ((MainActivity) (getActivity())).setFragmentState(2);

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        myToolbar.setTitle("최근 장바구니");

        adapter = new CartListviewAdapter((MainActivity)getActivity());
        cartListview = (ListView)view.findViewById(R.id.cartListView);
        cartListview.setAdapter(adapter);

        dbManager = new DbOpenHelper(getActivity());
        datas = dbManager.selectAllCart();

        for(int i=datas.size()-1; i>=0; i--){
            String date = datas.get(i).getDate();
            String title = datas.get(i).getTitle();
            String content = datas.get(i).getContent();
            adapter.addItem(date,title,content);
        }
        adapter.notifyDataSetChanged();

        return view;
    }
}
