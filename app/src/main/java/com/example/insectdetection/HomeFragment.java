package com.example.insectdetection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private LocationManager locationManager;
    private TextView locationTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        ImageButton mapButton = rootView.findViewById(R.id.map_button);
        locationTextView = rootView.findViewById(R.id.location);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    showCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        });

        return rootView;
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void showCurrentLocation() {
        Location location = getLastKnownLocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (!addresses.isEmpty()) {
                    String address = addresses.get(0).getAddressLine(0);
                    locationTextView.setText(address);
                    locationTextView.setVisibility(View.VISIBLE); // Make the TextView visible
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to get location address", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
        }
    }

    private Location getLastKnownLocation() {
        if (checkLocationPermission()) {
            Location gpsLocation = null;
            Location networkLocation = null;

            if (locationManager != null) {
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (gpsLocation != null && networkLocation != null) {
                return gpsLocation.getTime() > networkLocation.getTime() ? gpsLocation : networkLocation;
            } else if (gpsLocation != null) {
                return gpsLocation;
            } else if (networkLocation != null) {
                return networkLocation;
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }
}
