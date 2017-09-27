package com.example.ye0jun.seoulprice;

import android.provider.BaseColumns;

/**
 * Created by ye0jun on 2016. 10. 29..
 */

public class PricePlaceDB implements BaseColumns {
    public static final String REAL = "real";
    public static final String address_TABLENAME = "address";
    public static final String address_CREATE =
            "create table " + address_TABLENAME  + "("
                    + _ID + " integer primary key autoincrement, "
                    + REAL + " text not null);";

    public static final String DATE = "date";
    public static final String TITLE = "title";
    public static final String CONTENTS = "contents";
    public static final String cart_TABLENAME = "cart";
    public static final String cart_CREATE =
            "create table "+cart_TABLENAME+"("
                    +_ID+" integer primary key autoincrement, "
                    +DATE+" text not null , "
                    +TITLE+" text not null , "
                    +CONTENTS+" text not null );";
}
