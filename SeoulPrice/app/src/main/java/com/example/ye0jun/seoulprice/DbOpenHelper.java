package com.example.ye0jun.seoulprice;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ye0jun on 2016. 10. 29..
 */

public class DbOpenHelper {

    private String DATABASE_NAME = "ppdb.db";
    private static final int DATABASE_VERSION = 3;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PricePlaceDB.address_CREATE);
            db.execSQL(PricePlaceDB.cart_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + PricePlaceDB.address_TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS " + PricePlaceDB.cart_TABLENAME);
            onCreate(db);
        }

    }

    public DbOpenHelper(Context context) {
        this.mCtx = context;
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }


    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }


    public void close() {
        mDB.close();
    }

    public void insertCartData(String date,String title, String contents) {
        String sql = "insert into " + "cart" + " values(NULL, '" + date + "', '" + title + "', '" + contents +"');";
        mDB.execSQL(sql);
    }

    public void insertAddressData(String address) {
        String sql = "insert into " + "address" + " values(NULL, '" + address + "');";
        mDB.execSQL(sql);
    }

    public void deleteAll(){
        mDB.execSQL("DROP TABLE IF EXISTS " + PricePlaceDB.address_TABLENAME);
        mDB.execSQL("DROP TABLE IF EXISTS " + PricePlaceDB.cart_TABLENAME);
    }

    public void updateAddressData(String address) {
//        String sql = "update " + "address" + " set real = '" + address
//                + "' where id = " + 0
//                + ";";
//        mDB.execSQL(sql);
//        mDB.update("update",)
//        mDB.execSQL("UPDATE address SET real = '"+ address +"' WHERE = '"+selectAllAdrress().get(0)+"';");
        mDB.execSQL("update address set real='"+address+"' where _id=1;");
    }

    public ArrayList<String> selectAllAdrress() {
        String sql = "select * from " + "address" + ";";
        Cursor results = mDB.rawQuery(sql, null);

        results.moveToFirst();
        ArrayList<String> infos = new ArrayList<String>();

        while (!results.isAfterLast()) {
            String info = results.getString(1);
            Log.d("ye0jun-helhelel",info);
            infos.add(info);
            results.moveToNext();
        }
        results.close();
        return infos;
    }

    public ArrayList<cartDataStruct> selectAllCart() {
        String sql = "select * from " + "cart" + ";";
        Cursor results = mDB.rawQuery(sql, null);

        results.moveToFirst();
        ArrayList<cartDataStruct> infos = new ArrayList<cartDataStruct>();

        while (!results.isAfterLast()) {
            String date = results.getString(1);
            String title = results.getString(2);
            String contents = results.getString(3);
            infos.add(new cartDataStruct(date,title,contents));
            results.moveToNext();
        }
        results.close();
        return infos;
    }
}