package com.example.ye0jun.seoulprice;

import java.io.Serializable;

/**
 * Created by ye0jun on 2016. 9. 26..
 */
public class AverageCounter implements Serializable{
    private int price;
    private int count;

    public AverageCounter(){
        price = 0;
        count = 0;
    }

    public void addCount(){ count++; }
    public void setPrice(int n){ price = n; }
    public void addPrice(int n){ price += n; addCount();}
    public int getPrice(){ return price; }
    public int getCount(){ return count; }
    public int getAveragePrice() { return price / count; }
}
