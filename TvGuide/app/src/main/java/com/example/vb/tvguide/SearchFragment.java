package com.example.vb.tvguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class SearchFragment extends Fragment {

    InterstitialAd mInterstitialAd;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                showSearchResults(view);
            }
        });

        Button search = (Button) view.findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    showSearchResults(view);
                }
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.app_name) + " (" + getCountryCode() + ")");

        requestNewInterstitial();

        return view;
    }

    public void showSearchResults(View view) {
        String searchString = ((EditText) view.findViewById(R.id.searchText)).getText().toString();
        Intent intent = new Intent(getActivity(), SearchResults.class).putExtra("searchString", searchString);
        startActivity(intent);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("ABCDEF012345")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public String getCountryCode() {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(getContext());
        return sharedPreferences.getString("CountryCode", "US");
    }


}
