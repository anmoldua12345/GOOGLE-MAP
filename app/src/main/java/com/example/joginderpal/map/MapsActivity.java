package com.example.joginderpal.map;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private Toolbar bottontb;
    private GoogleMap mMap;
    RequestQueue requestQueue;
    TextView dis, dur;
    GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 1;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bottontb = (Toolbar) findViewById(R.id.inc_tb_bottom);
        dis = (TextView) findViewById(R.id.distance);
        dur = (TextView) findViewById(R.id.time);
        bottontb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.satellite:
                        Toast.makeText(getApplicationContext(), "click sat", Toast.LENGTH_SHORT).show();
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.terrain:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    case R.id.none:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case R.id.search:
                        try {
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);

                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }

                        break;
                }
                return true;
            }
        });
        bottontb.inflateMenu(R.menu.items);
        bottontb.findViewById(R.id.iv_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a1 = "";
                Intent intent = new Intent(MapsActivity.this, second.class);
                startActivity(intent);


            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    Geocoder gc = new Geocoder(MapsActivity.this);
                    LatLng ll = marker.getPosition();
                    try {
                        List<android.location.Address> list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
                        android.location.Address ad = list.get(0);
                        marker.setTitle(ad.getLocality());
                        marker.showInfoWindow();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView tx = (TextView) v.findViewById(R.id.tx);
                    TextView tx1 = (TextView) v.findViewById(R.id.tx1);
                    TextView tx2 = (TextView) v.findViewById(R.id.tx2);
                    LatLng ll = marker.getPosition();
                    tx.setText(marker.getTitle());
                    tx1.setText("latitude :" + ll.latitude);
                    tx2.setText("longitude :" + ll.longitude);


                    return v;
                }
            });


            Polyline polyline = null;
            ArrayList<Double> startlatlist = (ArrayList<Double>) getIntent().getSerializableExtra("startlat");
            ArrayList<Double> startlnglist = (ArrayList<Double>) getIntent().getSerializableExtra("startlng");
            ArrayList<Double> endlatlist = (ArrayList<Double>) getIntent().getSerializableExtra("endlat");
            ArrayList<Double> endlnglist = (ArrayList<Double>) getIntent().getSerializableExtra("endlng");
            String direction = getIntent().getExtras().getString("distance");
            String duration = getIntent().getExtras().getString("duration");
            dis.setText(direction);
            dur.setText(duration);

            setMarker(startlatlist.get(0), startlnglist.get(0));
            for (int i = 0; i < startlatlist.size(); i++) {
                PolylineOptions rectOptions = new PolylineOptions().add(new LatLng(startlatlist.get(i), startlnglist.get(i)))
                        .add(new LatLng(endlatlist.get(i), endlnglist.get(i))).color(Color.BLUE).geodesic(true);

                polyline = mMap.addPolyline(rectOptions);
                setMarker1(startlatlist.get(i), startlnglist.get(i));
            }


            if (polyline != null) {
                polyline.remove();
            }

        }

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


    }

    Marker marker;

    public void gotoLocation(double lat, double lon) {
        LatLng ll = new LatLng(lat, lon);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mMap.moveCamera(update);
    }

    public void gotoLocation(double lat, double lon, float zoom) {
        LatLng ll = new LatLng(lat, lon);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }

    public void geoLocate(View view) throws IOException {
        EditText editText = (EditText) findViewById(R.id.ed1);
        String address = editText.getText().toString();
        Geocoder geo = new Geocoder(this);
        List<android.location.Address> list = geo.getFromLocationName(address, 1);
        android.location.Address ad = list.get(0);
        String locality = ad.getLocality();
        Toast.makeText(getApplicationContext(), locality, Toast.LENGTH_SHORT).show();
        double lat = ad.getLatitude();
        double lon = ad.getLongitude();
        gotoLocation(lat, lon, 15);
        setMarker(locality, lat, lon);

    }

    public void setMarker(String locality, double lat, double lon) {

        if (marker != null) {
            marker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions().title(locality).position(new LatLng(lat, lon)).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        marker = mMap.addMarker(markerOptions);

    }


    public void setMarker(double lat, double lon) {

        if (marker != null) {
            marker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, lon)).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        marker = mMap.addMarker(markerOptions);

    }

    public void setMarker1(double lat, double lon) {

        if (marker != null) {
            marker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, lon)).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker = mMap.addMarker(markerOptions);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_LONG).show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }


}




