package net.mrkaan.printer;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import net.mrkaan.printer.model.Cafe;
import net.mrkaan.printer.model.Queue;
import net.mrkaan.printer.ui.activities.MyActivity;
import net.mrkaan.printer.ui.activities.OrdersActivity;
import net.mrkaan.printer.ui.activities.PrintScreenActivity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    private GeoLocation vLocation;
    private FirebaseFirestore vFirestore;
    private boolean isSignIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setvLocation();
        vFirestore = FirebaseFirestore.getInstance();
        vLocation = getLocation();

        findViewById(R.id.btn_arduino).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PrintScreenActivity.class)));
        findViewById(R.id.btn_printer).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MyActivity.class)));
        findViewById(R.id.btn_orders).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), OrdersActivity.class)));
        findViewById(R.id.btn_sign_out).setOnClickListener(v -> {
            AuthUI.getInstance().signOut(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Çıkış başarılı", Toast.LENGTH_LONG).show();
            finish();
        });

        if (shouldStartSignIn()) {
            startSignIn();
            isSignIn = true;
        }
        try {
            String str = FirebaseAuth.getInstance(vFirestore.getApp()).getCurrentUser().getDisplayName();
            Toast.makeText(this, "Hoşgeldiniz " + str, Toast.LENGTH_SHORT).show();
            setCafe(vLocation);
        } catch (Exception e) {
            isSignIn = false;
        }

    }


    //sign in activity
    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        setSignIn(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str = FirebaseAuth.getInstance(vFirestore.getApp()).getCurrentUser().getDisplayName();
        Toast.makeText(this, "Hoşgeldiniz " + str, Toast.LENGTH_SHORT).show();
    }

    private boolean shouldStartSignIn() {
        return (!isSignIn && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    public void setSignIn(boolean signIn) {
        isSignIn = signIn;
    }
    //sign in activity end

    private void setCafe(GeoLocation vLocation) {
        CollectionReference reference = vFirestore.collection("cafes");
        FirebaseAuth auth = new FirebaseAuth(vFirestore.getApp());
        FirebaseUser user = auth.getCurrentUser();
        //if user.getUid() in users table then:
        Query query = reference.whereEqualTo("cafeId", user.getUid());
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            try {
                assert queryDocumentSnapshots != null;
                if (queryDocumentSnapshots.isEmpty()) {
                    EditText username, phone, name;
                    username = findViewById(R.id.username);
                    phone = findViewById(R.id.phone_num);
                    name = findViewById(R.id.name);

                    //signubutton
                    findViewById(R.id.btn_signup).setOnClickListener(v -> {
                        Query query1 = reference.whereEqualTo("username", username.getText().toString());
                        query1.addSnapshotListener((queryDocumentSnapshots1, e12) -> {

                            if (!queryDocumentSnapshots1.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Kullanıcı adı daha önce alınmış", Toast.LENGTH_LONG).show();
                            } else {
                                Cafe cafe = new Cafe(username.getText().toString(), user.getUid(), user.getEmail(), vLocation, name.getText().toString(), phone.getText().toString(), null, null, null, true);

                                reference.add(cafe)
                                        .addOnFailureListener(e121 -> Toast.makeText(getApplicationContext(), "Kullanıcı eklenemedi, bir hata var!", Toast.LENGTH_LONG).show())
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(getApplicationContext(), "Kullanıcı ekleme başarılı.", Toast.LENGTH_LONG).show();
                                        });

                            }
                        });
                    });

                } else {
                    // Toast.makeText(getApplicationContext(), "Hoşgeldin " + user.getDisplayName(), Toast.LENGTH_LONG).show();

                }
            } catch (Exception e1) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_CODE__ACCESS_COARSE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1200);
        }

    }


    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }

    protected GeoLocation getLocation() {
        if (isLocationEnabled(MainActivity.this)) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);

            if (location != null) {
                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                return new GeoLocation(latitude, longitude);
                //Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
            } else {
                //This is what you need:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return null;
                }
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
                return new GeoLocation(longitude, latitude);
            }
        } else {
            //prompt user to enable location....
            //.................
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_CODE__ACCESS_COARSE_LOCATION);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1200);
            }

        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }

}