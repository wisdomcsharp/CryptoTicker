package com.example.wisdom.pricewatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Message;

import org.w3c.dom.Text;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Spinner spinner;
    ImageView recording;
    loader priceLoader;
    Thread priceLoaderThread;
    TextView priceView;
    CryptoCurrency.Currency myCurrency;


    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            priceView.setText(msg.obj.toString());
            super.handleMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        priceView = (TextView) findViewById(R.id.priceView);
        //Instantiate object
        priceLoader = new loader(CryptoCurrency.Currency.Litecoin, CryptoCurrency.Convert.USD);
        //run on thread every 15 seconds.
        priceLoaderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        priceLoader.getPrice(myHandler);
                        Thread.sleep(1900);
                    } catch (Exception e) {
                        Log.v("ERROR", e.getMessage().toString());
                    }
                }
            }
        });
        priceLoaderThread.start();


        recording = (ImageView) findViewById(R.id.img_recording);
        Animation recording_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.recording);
        recording.startAnimation(recording_anim);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getApplicationContext(), parent.getSelectedItem().toString(), Toast.LENGTH_SHORT);
                        int item = parent.getSelectedItemPosition();
                        switch (item) {
                            case 0:
                                priceLoader.convert = CryptoCurrency.Convert.USD;
                                break;
                            case 1:
                                priceLoader.convert = CryptoCurrency.Convert.EUR;
                                break;
                            case 2:
                                priceLoader.convert = CryptoCurrency.Convert.JPY;
                                break;
                            case 3:
                                priceLoader.convert = CryptoCurrency.Convert.GBP;
                                break;
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String id = item.getTitle().toString().toLowerCase().trim();

        if (id.equals("bitcoin"))
            myCurrency = CryptoCurrency.Currency.Bitcoin;
        if (id.equals("ethereum"))
            myCurrency = CryptoCurrency.Currency.Ethereum;
        if (id.equals("litecoin"))
            myCurrency = CryptoCurrency.Currency.Litecoin;

        priceLoader.setCurrencyURL(myCurrency);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
