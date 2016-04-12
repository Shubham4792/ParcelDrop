package com.example.shubhampandey.parceldrop.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shubhampandey.parceldrop.R;
import com.example.shubhampandey.parceldrop.fragment.DropFragment;
import com.example.shubhampandey.parceldrop.fragment.PickupFragment;
import com.example.shubhampandey.parceldrop.fragment.RouteFragment;
import com.example.shubhampandey.parceldrop.util.FetchLatLng;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class AddressActivity extends BaseActivity {
    protected PickupFragment pickupFragment;
    protected DropFragment dropFragment;
    private EditText mSearchEditText;
    private TextView fragmentTitle;
    private LinearLayout mSearchLayout;
    private RouteFragment routeFragment;
    public LatLng fetchedLatLng;
    private static double EARTH_RADIUS = 6371.0;
    private boolean isPickupMap = true;
    public Address pickupAddress;
    public Address dropAddress;
    private ImageView mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickupFragment = new PickupFragment();
        addFragments();
        initViews();
        initListeners();
    }

    @Override
    protected int getContentPageLayoutId() {
        return R.layout.activity_main;
    }

    private void initViews() {
        mSearchLayout = (LinearLayout) findViewById(R.id.search_layout);
        mSearchEditText = (EditText) findViewById(R.id.search_input);
        mSearchButton = (ImageView) findViewById(R.id.search_cta);
        fragmentTitle = (TextView) findViewById(R.id.pickup_title);
    }

    private void initListeners() {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchedLatLng = null;
                if (TextUtils.isEmpty(mSearchEditText.getText())) {
                    showErrorNotification(getString(R.string.invalid_search), true);
                } else {
                    new MyTask().execute(mSearchEditText.getText().toString());
                }
                mSearchEditText.setText("");
            }
        });

        mSearchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEditText.setCursorVisible(true);
            }
        });
    }

    public void changeToDropTitle() {
        if (fragmentTitle != null) {
            fragmentTitle.setText(R.string.drop_text_title);
        }
    }

    public void changeToRouteTitle() {
        if (fragmentTitle != null) {
            fragmentTitle.setText(R.string.route_text_title);
        }
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.address_list_container;
    }

    protected void addFragments() {
        this.getFragmentManager().beginTransaction().add(R.id.address_list_container, pickupFragment).commitAllowingStateLoss();
    }

    public void showRoute() {
        hideSearch();
        changeToRouteTitle();
        routeFragment = new RouteFragment();
        replaceFragment(routeFragment, null);
    }

    public void showDropMap() {
        isPickupMap = false;
        showSearch();
        changeToDropTitle();
        dropFragment = new DropFragment();
        this.replaceFragment(dropFragment, null);
    }

    public void showPickupMap() {
        isPickupMap = true;
        showSearch();
        pickupFragment = new PickupFragment();
        this.replaceFragment(pickupFragment, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isPickupMap) {
            pickupFragment = null;
            showPickupMap();
        } else {
            finish();
        }
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        Double Radius = EARTH_RADIUS; //6371.00;
        Double dLat = toRadians(lat2 - lat1);
        Double dLon = toRadians(lng2 - lng1);
        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }

    public static Double toRadians(Double degree) {
        // Value degree * Pi/180
        return degree * 3.1415926 / 180;
    }

    public void setAsPickup(Address address) {
        pickupAddress = address;
    }

    public void setAsDrop(Address address) {
        Address temp = retrievePickup();
        if (!address.equals(temp)) {
            if (calculateDistance(address.getLatitude(), address.getLongitude(), temp.getLatitude(), temp.getLongitude()) > 0.1) {
                dropAddress = address;
            } else {
                showErrorNotification(getString(R.string.drop_pickup_cant_same), true);
            }
        } else {
            showErrorNotification(getString(R.string.drop_pickup_cant_same), true);
        }
    }

    private Address getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<android.location.Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            return addresses.get(0);
        }
        return null;
    }

    public Address retrieveDrop() {
        return dropAddress;
    }

    public Address retrievePickup() {
        return pickupAddress;
    }

    public void isPickup(boolean b) {
        isPickupMap = b;
    }

    public void hideSearch() {
        mSearchLayout.setVisibility(View.GONE);
    }

    public void showSearch() {
        mSearchLayout.setVisibility(View.VISIBLE);
    }

    public double getDistance() {
        if (pickupAddress != null && dropAddress != null) {
            return calculateDistance(pickupAddress.getLatitude(), pickupAddress.getLongitude(), dropAddress.getLatitude(), dropAddress.getLongitude());
        }
        return -1;
    }

    public void changeTitle(String s) {
        fragmentTitle.setText(s);
    }

    class MyTask extends AsyncTask<String, Void, String> {
        boolean noResponse = false;

        @Override
        protected String doInBackground(String... params) {
            final JSONObject json = FetchLatLng.getJSON(params[0]);
            if (json == null) {
                noResponse = true;
            } else {
                try {
                    double latitude = json.getDouble("lat");
                    double longitude = json.getDouble("lng");
                    DecimalFormat twoDForm = new DecimalFormat("#.##");
                    latitude = Double.valueOf(twoDForm.format(latitude));
                    longitude = Double.valueOf(twoDForm.format(longitude));
                    fetchedLatLng = new LatLng(latitude, longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (noResponse) {
                showErrorNotification(getString(R.string.no_place), true);
            }
            if (pickupFragment != null && fetchedLatLng != null) {
                hideKeyboard(AddressActivity.this);
                if (isPickupMap) {
                    pickupFragment.moveToLocation(fetchedLatLng);
                    changeTitle(getString(R.string.marker_click));
                }
            }
            if (dropFragment != null && fetchedLatLng != null) {
                hideKeyboard(AddressActivity.this);
                pickupFragment = null;
                dropFragment.moveToLocation(fetchedLatLng);
                changeTitle(getString(R.string.marker_click));
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
