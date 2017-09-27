package com.example.ye0jun.seoulprice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ye0jun on 2016. 9. 5..
 */
public class ResultDetailListviewAdapter extends BaseAdapter {

    private ArrayList<ResultDetailListview> listviewItemList = new ArrayList<>();

    public ResultDetailListviewAdapter(){
        
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
            view = inflater.inflate(R.layout.listview_result_detail,viewGroup,false);
        }

        ImageView iv_averageImage = (ImageView)view.findViewById(R.id.detail_average_photo);
        TextView tv_itemName = (TextView)view.findViewById(R.id.detail_itemName);
        TextView tv_itemPrice = (TextView)view.findViewById(R.id.detail_itemPrice);
        TextView tv_itemCount = (TextView)view.findViewById(R.id.detail_itemCount);
        TextView tv_itemPriceAll = (TextView)view.findViewById(R.id.detail_itemPriceAll);

        ResultDetailListview listviewItem = listviewItemList.get(pos);

        if(!listviewItem.getAverage())
            iv_averageImage.setVisibility(View.INVISIBLE);
        else
            iv_averageImage.setVisibility(View.VISIBLE);

        tv_itemName.setText(listviewItem.getItemName());
        tv_itemPrice.setText(listviewItem.getItemPrice());
        tv_itemCount.setText(listviewItem.getItemCount());
        tv_itemPriceAll.setText(listviewItem.getItemPriceAll());

        return view;
    }

    public void addItem(String name,int price,int count,boolean average){
        ResultDetailListview item = new ResultDetailListview();

        item.setItemName(name);
        item.setItemPrice(price,count);
        item.setAverage(average);

        listviewItemList.add(item);
    }
}
