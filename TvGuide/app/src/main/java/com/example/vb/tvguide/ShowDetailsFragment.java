package com.example.vb.tvguide;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vb.tvguide.data.ShowContract.ShowEntry;
import com.example.vb.tvguide.data.ShowProvider;
import com.squareup.picasso.Picasso;



/**
 * A simple {@link Fragment} subclass.
 */
public class ShowDetailsFragment extends Fragment {

    private TVShow tvshow;
    private boolean favourite = false;
    private boolean dbOperationComplete = true; // false if operation is taking place
    private boolean in_favourite = false;

    public TVShow getSelectedShow() {
        return getArguments().getParcelable("showObject");
    }

    // return true when displaying favourite shows
    public boolean inFavourites() {
        if (getArguments() == null || !getArguments().containsKey("favourites")) {
            return false;
        } else {
            return getArguments().getBoolean("favourites");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("showObject", tvshow);
        super.onSaveInstanceState(outState);
    }

    public void updateFAB(Boolean favourite, View view) {
        if (favourite) {
            ((FloatingActionButton) view.findViewById(R.id.fab)).setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            ((FloatingActionButton) view.findViewById(R.id.fab)).setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_details, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey("showObject")) {
            tvshow = savedInstanceState.getParcelable("showObject");
            favourite = savedInstanceState.getBoolean("favourite");
        } else {
            Bundle data = getActivity().getIntent().getExtras();
            if (data != null) {
                if (data.containsKey("favourites") && data.getBoolean("favourites")) {
                    //in_favourite = true;
                }
                if (data.containsKey("showobj"))
                    tvshow = (TVShow) data.getParcelable("showobj");
            }
            in_favourite = inFavourites();
        }

        if (checkDb(tvshow.getId())) {
            favourite = true;
        }

        updateFAB(favourite, view);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(tvshow.getName());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbOperationComplete) {
                    favourite = !favourite;
                    if (favourite && (checkNetworkConnection())) {
                        writeToDb();
                        Toast.makeText(getContext(), R.string.addToFav, Toast.LENGTH_SHORT).show();
                    } else if (!favourite) {
                        removeFromDb();
                        Toast.makeText(getContext(), R.string.removeFromFav, Toast.LENGTH_SHORT).show();
                    } else {
                        favourite = !favourite;
                        Toast.makeText(getContext(), R.string.networkproblem, Toast.LENGTH_SHORT).show();
                    }
                    updateFAB(favourite, view);
                }
            }
        });

        String season = getResources().getString(R.string.seasonDetails) + tvshow.getSeason();
        String language = getResources().getString(R.string.languageDetails) + tvshow.getLanguage();
        String runTime = getResources().getString(R.string.runTimeDetails) + tvshow.getRunTime();
        String episode = getResources().getString(R.string.episodeDetails) + tvshow.getEpisode();
        String status = getResources().getString(R.string.statusDetails) + tvshow.getStatus();
        String type = getResources().getString(R.string.typeDetails) + tvshow.getType();
        String time = getResources().getString(R.string.timeDetails) + tvshow.getTime();
        String days = getResources().getString(R.string.daysDetails) + "\n" + tvshow.getDays();
        String channel = getResources().getString(R.string.channelDetails) + tvshow.getChannel();
        String country = getResources().getString(R.string.countryDetails) + tvshow.getCountry();

        ImageView pic = (ImageView) view.findViewById(R.id.image);
        Picasso.with(getActivity()).load(tvshow.getImage()).into(pic);
        ((TextView) (view.findViewById(R.id.showName))).setText(tvshow.getName());
        ((TextView) (view.findViewById(R.id.season))).setText(season);
        ((TextView) (view.findViewById(R.id.language))).setText(language);
        ((TextView) (view.findViewById(R.id.runtime))).setText(runTime);
        ((TextView) (view.findViewById(R.id.episode))).setText(episode);
        ((TextView) (view.findViewById(R.id.status))).setText(status);
        ((TextView) (view.findViewById(R.id.type))).setText(type);

        //schedule
        ((TextView) (view.findViewById(R.id.time))).setText(time);
        ((TextView) (view.findViewById(R.id.day))).setText(days);

        //network
        ((TextView) (view.findViewById(R.id.channel))).setText(channel);
        ((TextView) (view.findViewById(R.id.country))).setText(country);

        ((TextView) (view.findViewById(R.id.summary))).setText(Html.fromHtml(tvshow.getSummary()));

        Log.v("tvguide", tvshow.getTime());
        Log.v("tvguide", tvshow.getDays());

        return view;
    }


    private boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // returns true if show is in DB
    private boolean checkDb(int id) {
        String selection = ShowEntry.ID + "=" + id;
        String[] projection = {ShowEntry.ID};
        Cursor cursor = getContext().getContentResolver().query(ShowProvider.CONTENT_URI, projection, selection, null, null);
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private void writeToDb() {
        DataBaseTask dbtask = new DataBaseTask();
        dbtask.execute("write");
    }

    private void removeFromDb() {
        DataBaseTask dbtask = new DataBaseTask();
        dbtask.execute("remove");
    }


    private class DataBaseTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params[0].equals("write")) {
                dbOperationComplete = false;
                ContentValues values = new ContentValues();

                values.put(ShowEntry.ID, tvshow.getId());
                values.put(ShowEntry.SEASON, tvshow.getSeason());
                values.put(ShowEntry.EPISODE, tvshow.getEpisode());
                values.put(ShowEntry.NAME, tvshow.getName());
                values.put(ShowEntry.LANGUAGE, tvshow.getLanguage());
                values.put(ShowEntry.STATUS, tvshow.getStatus());
                values.put(ShowEntry.RUNTIME, tvshow.getRunTime());
                values.put(ShowEntry.TYPE, tvshow.getType());
                values.put(ShowEntry.SUMMARY, tvshow.getSummary());
                values.put(ShowEntry.CHANNEL, tvshow.getChannel());
                values.put(ShowEntry.COUNTRY, tvshow.getCountry());
                values.put(ShowEntry.URL, tvshow.getUrl());
                values.put(ShowEntry.IMAGE, tvshow.getImage());
                values.put(ShowEntry.DAYS, tvshow.getDays());
                values.put(ShowEntry.TIME, tvshow.getTime());

                getContext().getContentResolver().insert(ShowProvider.CONTENT_URI, values);

                return "write";

            } else if (params[0].equals("remove")) {
                dbOperationComplete = false;
                getContext().getContentResolver().delete(ShowProvider.CONTENT_URI, ShowEntry.ID + "=" + tvshow.getId(), null);
                return "remove";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            dbOperationComplete = true;
        }
    }

}
