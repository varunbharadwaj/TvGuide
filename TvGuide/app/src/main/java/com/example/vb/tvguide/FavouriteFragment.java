package com.example.vb.tvguide;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vb.tvguide.data.ShowContract.ShowEntry;
import com.example.vb.tvguide.data.ShowProvider;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SHOW_LOADER = 0;
    ListView listView;
    ShowAdapter adapter;
    TextView emptyView;


    public FavouriteFragment() {
        // Required empty public constructor
    }

    public static FavouriteFragment newInstance() {
        FavouriteFragment fragment = new FavouriteFragment();
        return fragment;
    }

    public void displayMessage() {

        if (adapter.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        emptyView = (TextView) view.findViewById(R.id.empty_view2);
        listView = (ListView) view.findViewById(R.id.favourite_list_view);

        adapter = new ShowAdapter(getContext(), null, 0);
        listView.setAdapter(adapter);
        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                int id = cursor.getInt(cursor.getColumnIndex(ShowEntry.ID));
                String season = cursor.getString(cursor.getColumnIndex(ShowEntry.SEASON));
                String episode = cursor.getString(cursor.getColumnIndex(ShowEntry.EPISODE));
                String name = cursor.getString(cursor.getColumnIndex(ShowEntry.NAME));
                String language = cursor.getString(cursor.getColumnIndex(ShowEntry.LANGUAGE));
                String status = cursor.getString(cursor.getColumnIndex(ShowEntry.STATUS));
                String runTime = cursor.getString(cursor.getColumnIndex(ShowEntry.RUNTIME));
                String type = cursor.getString(cursor.getColumnIndex(ShowEntry.TYPE));
                String summary = cursor.getString(cursor.getColumnIndex(ShowEntry.SUMMARY));
                String channel = cursor.getString(cursor.getColumnIndex(ShowEntry.CHANNEL));
                String country = cursor.getString(cursor.getColumnIndex(ShowEntry.COUNTRY));
                String url = cursor.getString(cursor.getColumnIndex(ShowEntry.URL));
                String image = cursor.getString(cursor.getColumnIndex(ShowEntry.IMAGE));
                String days = cursor.getString(cursor.getColumnIndex(ShowEntry.DAYS));
                String time = cursor.getString(cursor.getColumnIndex(ShowEntry.TIME));

                TVShow selectedShow = new TVShow()
                        .setId(id)
                        .setShowDetails(season, episode, name, language, status, runTime, type)
                        .setSummary(summary)
                        .setSchedule(time, days)
                        .setNetworkDetails(channel, country)
                        .setUrl(url)
                        .setImage(image);

                Intent intent = new Intent(getActivity(), ShowDetails.class).putExtra("showobj", selectedShow);
                Bundle bundle = new Bundle();
                bundle.putBoolean("favourites", true);
                startActivity(intent);
            }
        });

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                displayMessage();
            }
        });
        displayMessage();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SHOW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ShowProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    public class ShowAdapter extends CursorAdapter {

        public ShowAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.favourite_show, parent, false);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ImageView imageView = (ImageView) view.findViewById(R.id.favourite_image);
            TextView showName = (TextView) view.findViewById(R.id.favourite_show_name);

            String name = cursor.getString(cursor.getColumnIndex(ShowEntry.NAME));
            String image = cursor.getString(cursor.getColumnIndex(ShowEntry.IMAGE));
            String channel = cursor.getString(cursor.getColumnIndex(ShowEntry.CHANNEL));
            String country = cursor.getString(cursor.getColumnIndex(ShowEntry.COUNTRY));

            String text = "<B><H1>" + name + "</H1></B><BR><BR>" + getResources().getString(R.string.channelDetails) + channel + "<BR>" +
                    getResources().getString(R.string.countryDetails) + country;
            showName.setText(Html.fromHtml(text));
            Picasso.with(getContext()).load(image).into(imageView);
        }
    }

}
