package com.example.wisdom.pricewatch;


import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Currency;

/**
 * Created by Wisdom on 03/06/2016.
 */
public class loader extends CryptoCurrency {

    private loader() {
    }

    Handler myHandler;
    Message myMessage;
    URL url;
    URL googleConvert;

    public loader(CryptoCurrency.Currency currency, Convert convert) {
        this.currency = currency;
        this.convert = convert;

        //instantiate url
        try {
            if (currency == Currency.Bitcoin) {
                url = new URL("https://btc-e.com/api/3/ticker/btc_usd");
            }
            if (currency == Currency.Ethereum) {
                url = new URL("https://btc-e.com/api/3/ticker/eth_usd");
            }

            if (currency == Currency.Litecoin) {
                url = new URL("https://btc-e.com/api/3/ticker/ltc_usd");
            }
        } catch (Exception ex) {

        }
    }


    @Override
    public void setCurrencyURL(Currency currency) {
        this.currency = currency;
        try {
            if (currency == Currency.Bitcoin)
                url = new URL("https://btc-e.com/api/3/ticker/btc_usd");
            if (currency == Currency.Litecoin)
                url = new URL("https://btc-e.com/api/3/ticker/ltc_usd");
            if (currency == Currency.Ethereum)
                url = new URL("https://btc-e.com/api/3/ticker/eth_usd");
        } catch (Exception ex) {
        }


    }

    @Override
    public void getPrice(Handler handler) {

        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));

            String testLine, dataLine = "";
            while ((testLine = read.readLine()) != null) {
                dataLine += testLine;
            }


            JSONObject a = new JSONObject(dataLine);

            if (this.currency == Currency.Bitcoin)
                this.price = a.getJSONObject("btc_usd").getString("sell");
            if (this.currency == Currency.Ethereum)
                this.price = a.getJSONObject("eth_usd").getString("sell");
            if (this.currency == Currency.Litecoin)
                this.price = a.getJSONObject("ltc_usd").getString("sell");
                Log.v("currency", dataLine);


            NumberFormat cur = NumberFormat.getIntegerInstance();
            cur.setMinimumFractionDigits(2);
            cur.setMaximumFractionDigits(2);
            this.price = cur.format(Double.valueOf(this.price.trim()));


            //Check if convert is set to other currency, then convert with google api;

            if (convert == Convert.EUR)
                googleConvert = new URL("https://www.google.com/finance/converter?a=" + this.price + "&from=USD&to=EUR");
            if (convert == Convert.JPY)
                googleConvert = new URL("https://www.google.com/finance/converter?a=" + this.price + "&from=USD&to=JPY");
            if (convert == Convert.GBP)
                googleConvert = new URL("https://www.google.com/finance/converter?a=" + this.price + "&from=USD&to=GBP");


            if (convert != Convert.USD) {
                BufferedReader readHtmlOutput = new BufferedReader(new InputStreamReader(googleConvert.openStream()));

                String htmlLine, htmlData = "";
                while ((htmlLine = readHtmlOutput.readLine()) != null) {
                    htmlData += htmlLine;
                }

                String convertPrice = Jsoup.parse(htmlData).select("span.bld").first().text().toString();
                int index = convertPrice.lastIndexOf(" ");
                this.price = convertPrice.substring(0, index);
                this.price = cur.format(Double.valueOf(this.price));

                readHtmlOutput.close();
            }

            read.close();

        } catch (Exception ex) {

        }


        myMessage = handler.obtainMessage();
        myMessage.what = 2;
        myMessage.obj = symbol(this.convert) + this.price;
        myMessage.sendToTarget();

    }

}
