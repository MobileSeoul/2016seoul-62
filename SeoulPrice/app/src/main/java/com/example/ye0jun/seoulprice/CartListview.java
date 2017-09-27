package com.example.ye0jun.seoulprice;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ye0jun on 2016. 10. 29..
 */

public class CartListview {
    private String cartDate;
    private String cartName;
    private String cartContent;

    public void setCartDate(String cartDate) {
        this.cartDate = cartDate;
    }

    public void setCartName(String cartName) {
        this.cartName = cartName;
    }

    public void setCartContent(String cartContent) { this.cartContent = cartContent; }

    public String getCartDate() {
        return cartDate;
    }

    public String getCartName() {
        return cartName;
    }

    public String getCartContent() { return cartContent; }
}
