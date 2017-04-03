package com.example.vb.tvguide;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.vb.tvguide.data.ShowProvider;
import com.example.vb.tvguide.widget.WidgetObserver;
import com.example.vb.tvguide.widget.WidgetProvider;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    public static ContentObserver showObserver;
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        getSupportActionBar().setElevation(0);

        if (showObserver == null) {
            ComponentName componentName = new ComponentName(this, WidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            showObserver = new WidgetObserver(new Handler(), appWidgetManager, componentName);
            this.getContentResolver().registerContentObserver(ShowProvider.CONTENT_URI, true, showObserver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String countryCode = getCountryCode();
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name) + " (" + countryCode + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.location) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public String getCountryCode() {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        return sharedPreferences.getString("CountryCode", "US");
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages.
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for a particular page.
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HomeFragment.newInstance();
                case 1:
                    return FavouriteFragment.newInstance();
                case 2:
                    return SearchFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            String pageTitle = "Page";
            switch (position) {
                case 0:
                    pageTitle = "Shows";
                    break;
                case 1:
                    pageTitle = "Favourites";
                    break;
                case 2:
                    pageTitle = "Search";
                    break;

            }
            return pageTitle;
        }

    }

}
