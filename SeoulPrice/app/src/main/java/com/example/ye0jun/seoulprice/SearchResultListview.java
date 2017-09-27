package com.example.ye0jun.seoulprice;

import android.util.Log;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ye0jun on 2016. 8. 16..
 */
public class SearchResultListview {
    private String martDistance;
    private String martName;
    private String martPrice;
    private Float fMartDistance;
    private int iMartPrice;
    private boolean bMartSelected;

    public void setMartSelected(boolean martSelected){
        bMartSelected = martSelected;
    }

    public void setMartDistance(Float distance){
        fMartDistance = (float)(distance * 0.001);
        martDistance = fMartDistance.toString().substring(0,fMartDistance.toString().indexOf(".")+2)+"km";
    }

    public void setMartName(String name){
        martName = name;
    }

    public void setMartPrice(int price){
        iMartPrice = price;
        if(iMartPrice == 0) {
            iMartPrice = 999999999;
            martPrice = "가격정보가 없습니다.";
        }
        else
            martPrice = NumberFormat.getIntegerInstance(Locale.US).format(price) + "원";
    }

    public String getMartDistance(){
        return martDistance;
    }

    public String getMartName(){
        return martName;
    }

    public String getMartPrice(){
        return martPrice;
    }

    public Float getfMartDistance() { return fMartDistance; }

    public int getiMartPrice() { return iMartPrice; }

    public boolean getbMartSelected() { return bMartSelected; }
}