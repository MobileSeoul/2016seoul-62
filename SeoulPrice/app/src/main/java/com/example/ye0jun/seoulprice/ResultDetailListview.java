package com.example.ye0jun.seoulprice;

import android.util.Log;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ye0jun on 2016. 9. 5..
 */
public class ResultDetailListview {
    private String itemName;
    private String itemPrice;
    private String itemPriceAll;
    private int itemCount;
    private boolean Average;

    public void setItemName(String s){
        itemName = s;
    }

    public void setItemPrice(int price,int count){
        if(price == 0)
            itemPrice = "가격정보가 없습니다.";
        else {
            itemPriceAll = NumberFormat.getIntegerInstance(Locale.US).format(price);
            itemPrice = NumberFormat.getIntegerInstance(Locale.US).format(price / count);
            itemCount = count;
        }
    }

    public void setAverage(boolean average){ Average = average; }

    public String getItemName(){
        return itemName;
    }

    public String getItemPrice(){
        return itemPrice;
    }

    public String getItemPriceAll() { return itemPriceAll; }

    public String getItemCount() { return itemCount+""; }

    public boolean getAverage() {return Average;}
}
