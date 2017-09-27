package com.example.ye0jun.seoulprice;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ye0jun on 2016. 10. 29..
 */

public class CartListviewAdapter extends BaseAdapter {

    private ArrayList<CartListview> listviewItemList = new ArrayList<CartListview>();
    private MainActivity mainActivity;

    public CartListviewAdapter(MainActivity a){
        mainActivity = a;
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
            view = inflater.inflate(R.layout.listview_cart,viewGroup,false);
        }


        TextView tv_cartDate = (TextView)view.findViewById(R.id.cartDate);
        TextView tv_cartName = (TextView)view.findViewById(R.id.cartName);
        ImageView iv_search = (ImageView)view.findViewById(R.id.btn_detail);

        final CartListview listviewItem = listviewItemList.get(pos);

        tv_cartDate.setText(listviewItem.getCartDate());
        tv_cartName.setText(listviewItem.getCartName());
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.resetCurrentCount();
                boolean bProduct = false;
                boolean bCount = false;
                String product = "";
                String count = "";
                for(int i=0; i<listviewItem.getCartContent().length(); i++){
                    if(listviewItem.getCartContent().charAt(i) == '/') {
                        bProduct = true;
                        i += 1;
                    }
                    else if(listviewItem.getCartContent().charAt(i) == '-')
                        bCount = true;

                    if(!bProduct)
                        product += listviewItem.getCartContent().charAt(i);
                    else if(!bCount) {
                        count += listviewItem.getCartContent().charAt(i);
                    }
                    else{
                        Log.d("ye0jun-product",product);
                        Log.d("ye0jun-count",count);
                        bProduct = false;
                        bCount = false;
                        mainActivity.putShoppingItem(product,Integer.parseInt(count));
                        product = "";
                        count = "";
                    }
                }
                mainActivity.changeState("result");
            }
        });

        return view;
    }

    public void addItem(String date,String name,String content){
        CartListview item = new CartListview();

        item.setCartDate(date);
        item.setCartName(name);
        item.setCartContent(content);

        listviewItemList.add(item);
    }

    public void deleteAll(){
        listviewItemList.clear();
    }

    public ArrayList<CartListview> getAllItem(){
        return listviewItemList;
    }
}
