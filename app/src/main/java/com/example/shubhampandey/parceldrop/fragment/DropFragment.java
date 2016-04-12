package com.example.shubhampandey.parceldrop.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shubhampandey.parceldrop.R;
import com.example.shubhampandey.parceldrop.activity.AddressActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Display drop location fragment
 */
public class DropFragment extends Fragment implements View.OnClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, LocationListener {


    private GoogleMap mGMap;
    private LatLng mCurrentLatLon;
    private Marker mCurrentLocationMarker;
    private AddressActivity mActivity;
    private MapFragment mMapFragment;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView myLocation = (ImageView) view.findViewById(R.id.current_location);
        getMyLocation();
        mActivity = (AddressActivity) getActivity();
        populateDropAddress();
        myLocation.setOnClickListener(this);
    }

    private void populateDropAddress() {
        Address address = mActivity.retrieveDrop();
        LatLng latLng;
        if (address != null) {
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
            addQueriedMarker(latLng);
        }

    }

    private void getMyLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(1);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = getLastKnownLocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mCurrentLatLon = new LatLng(latitude, longitude);
            setUpMapIfNeeded(mCurrentLatLon);
            mGMap.setOnInfoWindowClickListener(this);
        } else {
            createAndShowLocationDialog();
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onDestroyView() {
        if (null != mActivity && !mActivity.isFinishing() && mActivity.isActivityForeground() && null != mMapFragment) {
            getFragmentManager().beginTransaction().remove(mMapFragment).commit();
        }
        super.onDestroyView();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setUpMapIfNeeded(LatLng latlng) {
        mMapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.location_map);
        if (mMapFragment != null) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && mMapFragment.getMap() != null) {
                mGMap = mMapFragment.getMap();
            }
            mGMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            // Zoom in the Google Map
            mGMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }

    private void showCurrentLocation(LatLng latLng) {
        if (null != latLng) {
            addCurrentLocationMarker(latLng);
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(10);
            builder.target(latLng);
            mGMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        } else {
            createAndShowLocationDialog();
        }
    }

    public void moveToLocation(LatLng latLng) {
        setUpMapIfNeeded(latLng);
        if (null != latLng && mGMap != null) {
            addQueriedMarker(latLng);
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(10);
            builder.target(latLng);
            mGMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
        } else {
            String noLoc = getString(R.string.no_such_place);
            mActivity.showErrorNotification(noLoc, true);
        }
    }

    private void addQueriedMarker(LatLng latLng) {
        Marker mQueriedMarker = mGMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        setupMarkerUi(mQueriedMarker);
    }

    private void setupMarkerUi(Marker marker) {
        Address address = getAddress(marker);
        if (address != null) {
            String title = address.getAddressLine(1) + " " + address.getLocality();
            marker.setTitle(title);
            marker.setSnippet(mActivity.getString(R.string.drop_info_window_snippet));
        }

    }

    private Address getAddress(Marker marker) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<android.location.Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            return addresses.get(0);
        }
        return null;
    }

    private void createAndShowLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.location_alert_title);
        builder.setMessage(R.string.location_alert_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void createAndShowDropConfirmDialog(final Address address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.drop_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.setAsDrop(address);
                if (mActivity.retrieveDrop() != null) {
                    mActivity.showRoute();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void addCurrentLocationMarker(LatLng currentLatLon) {
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }
        mCurrentLocationMarker = mGMap.addMarker(new MarkerOptions()
                .position(currentLatLon)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user)));
        setupMarkerUi(mCurrentLocationMarker);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.current_location) {
            showCurrentLocation(mCurrentLatLon);
        }
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
        mCurrentLatLon = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLon, 10);
        mGMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLon, 10);
        mGMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGMap = googleMap;
        mGMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mGMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGMap.getUiSettings().setMapToolbarEnabled(true);
        mGMap.setMyLocationEnabled(true);
        if (mCurrentLatLon != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLon, 10);
            mGMap.animateCamera(cameraUpdate);
        } else {
            createAndShowLocationDialog();
        }
        mGMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Address address = getAddress(marker);
        marker.hideInfoWindow();
        createAndShowDropConfirmDialog(address);
    }


    protected class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mWindow;
        private TextView snippetUi;
        private TextView titleUi;

        CustomInfoWindowAdapter() {
            mWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null,
                    false);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        /**
         * Draw's Info window on map with custom config.
         */
        private void render(Marker marker, View view) {
            String title = marker.getTitle();
            String snippet = marker.getSnippet();
            titleUi = (TextView) view.findViewById(R.id.title);
            snippetUi = (TextView) view.findViewById(R.id.snippet);
            titleUi.setText(title);
            if (null != snippet) {
                snippetUi.setVisibility(View.VISIBLE);
                snippetUi.setText(snippet);
            } else {
                snippetUi.setVisibility(View.GONE);
            }
        }
    }
}
