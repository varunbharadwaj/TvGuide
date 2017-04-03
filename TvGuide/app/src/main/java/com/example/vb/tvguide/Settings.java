package com.example.vb.tvguide;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Settings extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "TVGUIDE";
    private final String defaultCountryCode = "US";
    private final String defaultCountryName = "United States";
    private final int PERMISSION = 1;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private String countryName = "United States";
    private double latitude;
    private double longitude;
    private String autoDetectedCountry;
    private HashMap<String, String> countryCodes;
    private String[] countries;
    private TextView currentCodeView;
    private Spinner dropdown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String currentName = getCountryName();
        currentCodeView = (TextView) findViewById(R.id.currentCountry);
        currentCodeView.setText(currentName);

        buildGoogleApiClient();
        readCountryCodes();

        dropdown = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_row, countries);
        dropdown.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void autoSelectButton(View view) {
        storeCountryCode(autoDetectedCountry, countryName);
        currentCodeView.setText(countryName);
    }

    public void userSelectButton(View view) {
        String selectedCountry = dropdown.getSelectedItem().toString();
        storeCountryCode(countryCodes.get(selectedCountry), selectedCountry);
        currentCodeView.setText(selectedCountry);
    }

    // returns a map of country,code
    public void readCountryCodes() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("country-codes")));
            countryCodes = new HashMap<>();
            countries = new String[233];
            int i = 0;
            reader.readLine(); // reads heading
            String line = reader.readLine().trim();
            while (line != null) {
                String[] arr = line.trim().split(",");
                countryCodes.put(arr[0], arr[1]);
                countries[i++] = arr[0];
                line = reader.readLine();
            }
            reader.close();
            //Log.v(TAG,Integer.toString(countries.length));
        } catch (IOException e) {
            countryCodes = new HashMap<>();
            e.printStackTrace();
        }
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            checkLocationService();
        }


    }

    public String getCountryCode() {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        return sharedPreferences.getString("CountryCode", defaultCountryCode);
    }

    public String getCountryName() {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        return sharedPreferences.getString("CountryName", defaultCountryName);
    }

    public void storeCountryCode(String code, String name) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CountryCode", code);
        editor.putString("CountryName", name);
        editor.commit();
    }

    public void getCountry() {
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        TextView selectedCountry = (TextView) findViewById(R.id.autoSelectedCountry);
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                String countryCode = addresses.get(0).getCountryCode();
                String name = addresses.get(0).getCountryName();
                String code = countryCodes.get(name);
                Log.v(TAG, code + "  " + countryCode);
                if (code != null && code.equals(countryCode)) {
                    autoDetectedCountry = countryCode;
                    countryName = name;
                    selectedCountry.setText(name);
                }
            }
        } catch (IOException e) {
            Log.v(TAG, "error in getting country name");
            Toast.makeText(this, R.string.locNotFound, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            selectedCountry.setText(defaultCountryName);
            autoDetectedCountry = defaultCountryCode;
        }
    }

    public void getLocation() {
        if (checkNetworkConnection()) {
            LocationRequest mLocationRequest = LocationRequest.create();
            try {
                mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLocation != null) {
                    latitude = mLocation.getLatitude();
                    longitude = mLocation.getLongitude();
                    getCountry();
                } else {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.permissionnotavail, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, R.string.networkproblem, Toast.LENGTH_SHORT).show();
            TextView selectedCountry = (TextView) findViewById(R.id.autoSelectedCountry);
            selectedCountry.setText(defaultCountryName);
            autoDetectedCountry = defaultCountryCode;
        }
    }

    public void checkLocationService() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        getLocation();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Settings.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;

                    default:
                        TextView selectedCountry = (TextView) findViewById(R.id.autoSelectedCountry);
                        selectedCountry.setText(defaultCountryName);
                        autoDetectedCountry = defaultCountryCode;
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    checkLocationService();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    TextView selectedCountry = (TextView) findViewById(R.id.autoSelectedCountry);
                    selectedCountry.setText("-");
                    Toast.makeText(this, R.string.permissionnotavail, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // New location has now been determined
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        getCountry();
        //Log.v(TAG,getCountry());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
        Toast.makeText(this, R.string.playservice, Toast.LENGTH_SHORT).show();
        currentCodeView.setText("-");
    }
}
