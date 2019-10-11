package com.example.foodtruckfinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import java.util.Map;

public class CustomerMapsActivity extends FragmentActivity
        implements OnMarkerClickListener, OnInfoWindowClickListener, OnMapReadyCallback
{

    GoogleMap mMap;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    Location mCurrLocation;
    FusedLocationProviderClient mFusedLocationClient;

    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference UserOnlineRef = FirebaseDatabase.getInstance().getReference("UserOnline");
    DatabaseReference OwnerOnlineRef = FirebaseDatabase.getInstance().getReference("OwnerOnline");
    DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference("Users");

    Map<String, Marker> markerMap = new HashMap<>();
    Map<String, Marker> markerMapUser = new HashMap<>();


    // for info window
    TextView info_tv1;
    TextView info_tv2;
    //TextView info_tv3;
    String info_phone;
    String info_last_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_map);

        mFusedLocationClient = new FusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.infowindow_maps, null);

                LatLng latLng = marker.getPosition();

                info_tv1 = (TextView) v.findViewById(R.id.info_line1);
                info_tv2 = (TextView) v.findViewById(R.id.info_line2);
                //info_tv3 = (TextView) v.findViewById(R.id.info_line3);

                // String title= marker.getTitle();
                //String informations=arg0.getSnippet();

                if (marker.getTitle().equals("This is me!"))
                {
                    info_tv1.setText(marker.getTitle());
                }
                else
                {
                    info_tv1.setText(marker.getTitle());
                    info_tv2.setText(marker.getSnippet());
//                    for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
//                        if (entry.getValue().equals(marker))
//                        {
//
//                            String infoID = entry.getKey();
//                            info_tv1.setText("ID: " + infoID);
//
//                            info_tv2.setText("Phone: Loading...");
//                            info_tv3.setText("LastName: Loading...");
//
//                            DatabaseReference infoRef = UsersRef.child(infoID);
//                            infoRef.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    User user_info = dataSnapshot.getValue(User.class);
//                                    info_phone = user_info.phone;
//                                    info_last_name = user_info.last_name;
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                }
//                            });
//                            info_tv2.setText("Phone: " + info_phone);
//                            info_tv3.setText("LastName: " + info_last_name);
//                        }
                }
                return v;
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(false);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(false);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            Location location = locationResult.getLastLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            if (mCurrLocation == null) {
                mCurrLocation = location;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }

            // Place current location marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("This is me!");

            // resize marker
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.user_ic_foreground);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 90, 90, false);
            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

            // add marker
            markerOptions.icon(smallMarkerIcon);

            if (mCurrLocationMarker == null) {
                mCurrLocationMarker = mMap.addMarker(markerOptions);
            }

            // move map camera if distance > 3 meter ~10 ft
            if (mCurrLocation.distanceTo(location) > 3) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mCurrLocation = location;
                mCurrLocationMarker.remove();
                mCurrLocationMarker = mMap.addMarker(markerOptions);
            }

            else {
                mCurrLocation = location;
            }

            // add to user database
            GeoFire geoFire = new GeoFire(UserOnlineRef);
            geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        Toast.makeText(getApplicationContext(), "Cannot save", Toast.LENGTH_LONG).show();
                    } else {
                        // Toast.makeText(getApplicationContext(), "Done ", Toast.LENGTH_LONG).show();
                    }
                }
            });

            searchAround(location);
        }
    };

    private void searchAround(Location loc) {

        GeoFire geoFire = new GeoFire(OwnerOnlineRef);

        // query search, .6 = 1 mile
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(loc.getLatitude(), loc.getLongitude()), 1.8);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!key.equals(userID)) {

                    if (!markerMap.containsKey(key)) {
                        LatLng latLng = new LatLng(location.latitude, location.longitude);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        // resize marker
                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.other_owner_foodtruck_foreground);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 90, 90, false);
                        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

                        // add marker
                        markerOptions.icon(smallMarkerIcon);

                        markerOptions.title(key);
                        DatabaseReference infoRef = UsersRef.child(key);
                        infoRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user_info = dataSnapshot.getValue(User.class);
                                info_phone = user_info.phone;
                                info_last_name = user_info.last_name;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        markerOptions.snippet("Phone: " + info_phone + "\n" +
                                "LastName: " + info_last_name);
                        Marker newMarker = mMap.addMarker(markerOptions);
                        markerMap.put(key, newMarker);
                    }

                    else { // not sure if needed
                        markerMap.get(key).setVisible(true);
                    }
                }
            }

            @Override
            public void onKeyExited(String key)
            {
                if (markerMap.get(key) == null)
                {
                    markerMap.remove(key);
                }
                else
                {
                    markerMap.get(key).setVisible(false);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                if (!key.equals(userID))
                {
                    //remove the last location
                    if (markerMap.get(key) != null) {
                        markerMap.get(key).remove();
                    }

                    //markerMap.get(key).setVisible(true);
                    LatLng latLng = new LatLng(location.latitude, location.longitude);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    // resize marker
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.other_owner_foodtruck_foreground);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 90, 90, false);
                    BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

                    // add marker
                    markerOptions.icon(smallMarkerIcon);

                    markerOptions.title(key);
                    DatabaseReference infoRef = UsersRef.child(key);
                    infoRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user_info = dataSnapshot.getValue(User.class);
                            info_phone = user_info.phone;
                            info_last_name = user_info.last_name;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    markerOptions.snippet("Phone: " + info_phone + "\n" +
                            "LastName: " + info_last_name);
                    Marker newMarker = mMap.addMarker(markerOptions);
                    markerMap.put(key, newMarker);
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });



        //
        // Now search for other users
        //

        GeoFire geoFireUser = new GeoFire(UserOnlineRef);

        // query search, .6 = 1 mile
        GeoQuery geoQueryUser = geoFireUser.queryAtLocation(new GeoLocation(loc.getLatitude(), loc.getLongitude()), 1.8);
        geoQueryUser.removeAllListeners();

        geoQueryUser.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!key.equals(userID)) {

                    if (!markerMapUser.containsKey(key)) {
                        LatLng latLng = new LatLng(location.latitude, location.longitude);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        // resize marker
                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.other_user_ic_foreground);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 90, 90, false);
                        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

                        // add marker
                        markerOptions.icon(smallMarkerIcon);

                        markerOptions.title(key);
                        DatabaseReference infoRef = UsersRef.child(key);
                        infoRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user_info = dataSnapshot.getValue(User.class);
                                info_phone = user_info.phone;
                                info_last_name = user_info.last_name;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        markerOptions.snippet("Phone: " + info_phone + "\n" +
                                "LastName: " + info_last_name);
                        Marker newMarker = mMap.addMarker(markerOptions);
                        markerMapUser.put(key, newMarker);
                    }

                    else { // not sure if needed
                        markerMapUser.get(key).setVisible(true);
                    }
                }
            }

            @Override
            public void onKeyExited(String key)
            {
                if (markerMap.get(key) == null)
                {
                    markerMapUser.remove(key);
                }
                else
                {
                    markerMapUser.get(key).setVisible(false);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                if (!key.equals(userID))
                {
                    //remove the last location
                    if (markerMapUser.get(key) != null) {
                        markerMapUser.get(key).remove();
                    }

                    //markerMapUser.get(key).setVisible(true);
                    LatLng latLng = new LatLng(location.latitude, location.longitude);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    // resize marker
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.other_user_ic_foreground);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 90, 90, false);
                    BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

                    // add marker
                    markerOptions.icon(smallMarkerIcon);

                    markerOptions.title(key);
                    DatabaseReference infoRef = UsersRef.child(key);
                    infoRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user_info = dataSnapshot.getValue(User.class);
                            info_phone = user_info.phone;
                            info_last_name = user_info.last_name;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    markerOptions.snippet("Phone: " + info_phone + "\n" +
                            "LastName: " + info_last_name);
                    Marker newMarker = mMap.addMarker(markerOptions);
                    markerMapUser.put(key, newMarker);
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(CustomerMapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1 );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1 );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(false);
                    }

                }
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onPause()
    {
        super.onPause();

        //stop location updates when Activity is no longer active
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        GeoFire geoFireUser = new GeoFire(UserOnlineRef);
        geoFireUser.removeLocation(userID, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), "Cannot remove from OnlineUser", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Removed from OnlineUser ", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(false);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        GeoFire geoFireUser = new GeoFire(UserOnlineRef);
        geoFireUser.removeLocation(userID, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), "Cannot remove from OnlineUser", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Removed from OnlineUser ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GeoFire geoFireUser = new GeoFire(UserOnlineRef);
        geoFireUser.removeLocation(userID, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), "Cannot remove from OnlineUser", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Removed from OnlineUser ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        GeoFire geoFireUser = new GeoFire(UserOnlineRef);
        geoFireUser.removeLocation(userID, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), "Cannot remove from OnlineUser", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Removed from OnlineUser ", Toast.LENGTH_LONG).show();
                }
            }
        });

        startActivity(new Intent(CustomerMapsActivity.this,
                ProfileActivity.class));
        finish();
    }
}
