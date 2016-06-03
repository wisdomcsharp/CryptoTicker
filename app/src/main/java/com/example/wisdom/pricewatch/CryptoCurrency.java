package com.example.wisdom.pricewatch;

import android.os.Handler;

/**
 * Created by Wisdom on 03/06/2016.
 */
public abstract class CryptoCurrency {

    enum Currency {
        Bitcoin,
        Litecoin,
        Ethereum
    }

    enum Convert {
        USD,
        EUR,
        JPY,
        GBP
    }

    public String symbol(Convert convert) {
        return convert == Convert.USD ? "$" : convert == Convert.EUR ? "€" :
                convert == Convert.JPY ? "¥" : convert == Convert.GBP ? "£" : null;
    }

    public Currency currency;
    public Convert convert;
    public String price;

    public abstract void setCurrencyURL(Currency currency);
    public abstract void getPrice(Handler handler) throws InterruptedException;
}
