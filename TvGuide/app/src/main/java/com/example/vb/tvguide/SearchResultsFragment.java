package com.example.vb.tvguide;


import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment {

    private static final String TAG = "TVGUIDE";
    ImageAdapter imgAdapter;
    TextView emptyView;
    private String date;
    private List<TVShow> tvShows;

    public SearchResultsFragment() {
        // Required empty public constructor
    }


    public void displayMessage() {

        if (imgAdapter.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        emptyView = (TextView) view.findViewById(R.id.empty_view);
        GridView gridview = (GridView) view.findViewById(R.id.imageGrid);
        Bundle data = getActivity().getIntent().getExtras();
        String searchString = data.getString("searchString");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(searchString);

        if (savedInstanceState == null || !savedInstanceState.containsKey("searchshows")) {
            tvShows = new ArrayList<>();
            fetchShowData(searchString);
        } else {
            tvShows = savedInstanceState.getParcelableArrayList("searchshows");
        }

        imgAdapter = new ImageAdapter(getActivity(), tvShows);
        gridview.setAdapter(imgAdapter);
        imgAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                displayMessage();
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getActivity(), ShowDetails.class).putExtra("showobj", tvShows.get(position));
                startActivity(intent);
            }
        });
        displayMessage();

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("searchshows", (ArrayList<TVShow>) tvShows);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void fetchShowData(String searchString) {
        if (checkNetworkConnection()) {
            tvShows.clear();
            FetchShowDetails showTask = new FetchShowDetails();
            showTask.execute(searchString);
        } else {
            Toast.makeText(getContext(), R.string.networkproblem, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class FetchShowDetails extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = HomeFragment.FetchShowDetails.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String showJsonStr = null;
            date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

            Log.v(TAG, "started: " + date);

            try {
                final String BASE_URL = "http://api.tvmaze.com/search/shows?q=" + params[0];
                Log.v(TAG, BASE_URL);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .build();
                Response response = client.newCall(request).execute();
                showJsonStr = response.body().string();


                try {
                    // returns list of Show objects
                    return getShowData(showJsonStr);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] imageList) {
            if (imageList != null) {
                imgAdapter.setImageUrls(imageList);
            }
        }

        // retrieves movie data from JSON string
        public String[] getShowData(String JsonStr) throws JSONException {

            Log.v(TAG, JsonStr);

            int id;
            String season;
            String episode;
            String name;
            String language;
            String status;
            String runTime;
            String type;
            String summary;
            String channel;
            String country;
            String url;
            String image;
            String time;
            String days = "";
            JSONArray daysArray;

            // consider 30 shows
            JSONArray jsonarray = new JSONArray(JsonStr);
            int listSize = (jsonarray.length() < 30) ? jsonarray.length() : 30;
            String[] urlList = new String[listSize];

            for (int i = 0; i < jsonarray.length() && i < 30; i++) {
                try {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    days = "";
                    season = "-";
                    episode = "-";

                    jsonobject = jsonobject.getJSONObject("show");


                    id = jsonobject.getInt("id");
                    runTime = jsonobject.getString("runtime");
                    url = jsonobject.getString("url");
                    name = jsonobject.getString("name");
                    status = jsonobject.getString("status");
                    language = jsonobject.getString("language");
                    type = jsonobject.getString("type");

                    try {
                        JSONObject scheduleObj = jsonobject.getJSONObject("schedule");
                        time = scheduleObj.getString("time");
                        daysArray = scheduleObj.getJSONArray("days");
                    } catch (Exception e) {
                        time = "-";
                        daysArray = new JSONArray();
                    }

                    try {
                        JSONObject networkObj = jsonobject.getJSONObject("network");
                        channel = networkObj.getString("name");
                        country = networkObj.getJSONObject("country").getString("name");
                    } catch (Exception e) {
                        channel = "-";
                        country = "-";
                    }

                    try {
                        JSONObject imageObj = jsonobject.getJSONObject("image");
                        image = imageObj.getString("original");
                    } catch (Exception e) {
                        image = "null";
                    }

                    summary = jsonobject.getString("summary");

                    // get a string from days json array
                    for (int x = 0; x < daysArray.length(); x++) {
                        days += daysArray.getString(x) + " ";
                    }

                    TVShow newShow = new TVShow()
                            .setId(id)
                            .setShowDetails(season, episode, name, language, status, runTime, type)
                            .setSummary(summary)
                            .setSchedule(time, days)
                            .setNetworkDetails(channel, country)
                            .setUrl(url)
                            .setImage(image);

                    tvShows.add(newShow);
                    urlList[i] = image;
                    //Log.v(TAG,newShow.getName());
                } catch (Exception e) {
                    Log.v(TAG, "Error in downloading show information");
                }

            }

            return urlList;
        }

    }

    // adapter for images
    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private List<String> image_url = new ArrayList<String>();

        public ImageAdapter(Context c, List<TVShow> shows) {
            context = c;
            for (int i = 0; i < shows.size(); i++)
                image_url.add(shows.get(i).getImage());
        }

        public int getCount() {
            return image_url.size();
        }

        public void clear() {
            image_url.clear();
        }

        public Object getItem(int position) {
            return image_url.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public void setImageUrls(String[] urls) {
            image_url.clear();
            for (int i = 0; i < urls.length; i++) {
                image_url.add(urls[i]);
            }
            notifyDataSetChanged();
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            View gridView;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                //imageView = new ImageView(context);
                gridView = new View(context);
                // get layout from mobile.xml
                gridView = inflater.inflate(R.layout.griditem, null);
            } else {
                gridView = (View) convertView;
            }

            imageView = (ImageView) gridView.findViewById(R.id.gridimg);
            String url = image_url.get(position);
            if (url.equals("null")) {
                imageView.setBackgroundResource(R.drawable.noimg);
            } else {
                Picasso.with(context).load(url).into(imageView);
            }
            TextView textView = (TextView) gridView.findViewById(R.id.gridtext);
            textView.setText(tvShows.get(position).getName());
            return gridView;
        }

    }

}
