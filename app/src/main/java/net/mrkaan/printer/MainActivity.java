package net.mrkaan.printer;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import net.mrkaan.printer.ui.activities.MyActivity;
import net.mrkaan.printer.ui.activities.OrdersActivity;
import net.mrkaan.printer.ui.activities.PrintScreenActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        FirebaseFirestore.setLoggingEnabled(true);
        findViewById(R.id.btn_arduino).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PrintScreenActivity.class)));
        findViewById(R.id.btn_printer).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MyActivity.class)));
        findViewById(R.id.btn_orders).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), OrdersActivity.class)));

    }


    @Override
    protected void onResume() {
        super.onResume();
        // stores printer configuration and prints..

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_CODE__ACCESS_COARSE_LOCATION);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1200);
        }
    }
}