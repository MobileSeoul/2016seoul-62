package com.example.ye0jun.seoulprice;

/**
 * Created by ye0jun on 2016. 10. 30..
 */

public class cartDataStruct {
    private String date;
    private String title;
    private String content;

    public cartDataStruct(String sDate,String sTitle, String sContent){
        date = sDate;
        title = sTitle;
        content = sContent;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }
}
