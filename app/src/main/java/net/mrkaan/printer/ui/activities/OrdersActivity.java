package net.mrkaan.printer.ui.activities;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import net.mrkaan.printer.R;
import net.mrkaan.printer.adapter.OrdersVPAdapter;
import net.mrkaan.printer.model.Queue;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.abs;

public class OrdersActivity extends AppCompatActivity implements OrdersVPAdapter.OnOrderSelectedListener {
    //static number definitions
    //private static final String TAG = "QueueActivity";

    private static final int LIMIT = 50;

    //definitions
    private FirebaseFirestore vFirestore;
    private Query vQuery;
    private OrdersVPAdapter vOrdersVPAdapter;
    private RecyclerView vOrdersRecycler;
    private View emptyView;
    private boolean isSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        emptyView = findViewById(R.id.error_empty);
        vOrdersRecycler = findViewById(R.id.rv_orders);
        vFirestore = FirebaseFirestore.getInstance();
        isSignIn = false;

        initFirestore();
        initRecyclerView();

        findViewById(R.id.btnAdd).setOnClickListener(v -> addItems());

        // Start listening for Firestore updates
        if (vOrdersVPAdapter != null) {
            vOrdersVPAdapter.startListening();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // Apply filters
        //we have no filter
        //onFilter(mViewModel.getFilters());
        if (vOrdersVPAdapter != null) vOrdersVPAdapter.startListening();

    }



    private void initFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        vQuery = vFirestore.collection("orders").whereEqualTo("cafeId", userId).whereEqualTo("state", true);
        //vQuery = vQuery.orderBy("time", Query.Direction.DESCENDING).limit(LIMIT);
    }

    private void initRecyclerView() {
        vOrdersVPAdapter = new OrdersVPAdapter(vQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    vOrdersRecycler.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    vOrdersRecycler.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError() {
                Toast.makeText(getApplicationContext(), "There is an error", Toast.LENGTH_LONG).show();
            }
        };

        vOrdersRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        vOrdersRecycler.setAdapter(vOrdersVPAdapter);
    }

    @Override
    public void onOrderSelected(DocumentSnapshot order) {
        //burada arduino ve print işlemleri yapılacak
        Queue queue = makeQueue(Objects.requireNonNull(order.getData()));

        Intent intent = new Intent(getApplicationContext(), OrderPreviewActivity.class);
        intent.putExtra("orderId", queue.getOrderId());
        intent.putExtra("userId", queue.getUserId());
        intent.putExtra("time", queue.getTime());
        intent.putExtra("url", queue.getPictureUrl());
        intent.putExtra("tableNo", queue.getTableNo());
        startActivity(intent);
    }

    private Queue makeQueue(Map<String, Object> queueMap) {
        Queue queue = new Queue();
        queue.setOrderId(Integer.parseInt(Objects.requireNonNull(queueMap.get("orderId")).toString()));
        queue.setCafeId(Objects.requireNonNull(queueMap.get("cafeId")).toString());
        queue.setUserId(Integer.parseInt(Objects.requireNonNull(queueMap.get("userId")).toString()));

        queue.setInCafe(Boolean.valueOf(Objects.requireNonNull(queueMap.get("inCafe")).toString()));
        queue.setState(Boolean.getBoolean(Objects.requireNonNull(queueMap.get("state")).toString()));

        queue.setTableNo(Objects.requireNonNull(queueMap.get("tableNo")).toString());
        queue.setPictureUrl(Objects.requireNonNull(queueMap.get("pictureUrl")).toString());

        queue.setTime(Long.parseLong(Objects.requireNonNull(queueMap.get("time")).toString()));

        return queue;
    }

    private Queue getQueue() {
        Location location = new Location("");
        location.setLatitude(123123);
        location.setLongitude(343123);
        String url = "https://i.imgur.com/hueoozZ.jpg";
        //Date time = new Date("2");
        Random random = new Random();
        return new Queue(11, FirebaseAuth.getInstance().getCurrentUser().getUid(), location, true, url, true, abs(random.nextLong()), "B23", abs(random.nextInt() % 100));

    }

    private void addItems() {
        CollectionReference reference = vFirestore.collection("orders");

        Queue order = getQueue();

        reference.add(order)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getApplicationContext(), "Başarılı", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Başarısız", Toast.LENGTH_SHORT).show());
    }

}
        