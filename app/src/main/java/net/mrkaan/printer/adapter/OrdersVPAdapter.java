package net.mrkaan.printer.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import net.mrkaan.printer.R;
import net.mrkaan.printer.model.Queue;

import java.util.Map;
import java.util.Objects;

public abstract class OrdersVPAdapter extends FirestoreAdapter<OrdersVPAdapter.ViewHolder> {


    protected abstract void onError();

    public interface OnOrderSelectedListener {
        void onOrderSelected(DocumentSnapshot order);
    }

    private OnOrderSelectedListener listener;

    protected OrdersVPAdapter(Query query, OnOrderSelectedListener listener) {
        super(query);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView orderImage;
        TextView orderId;
        TextView inCafe;
        TextView tableNo;

        ViewHolder(View itemView) {
            super(itemView);
            orderImage = itemView.findViewById(R.id.order_image);
            orderId = itemView.findViewById(R.id.order_id);
            inCafe = itemView.findViewById(R.id.in_cafe);
            tableNo = itemView.findViewById(R.id.table_no);
        }

        void bind(final DocumentSnapshot snapshot,
                  final OnOrderSelectedListener listener) {
            Map<String, Object> queueMap = snapshot.getData();
            assert queueMap != null;
            Queue queue = makeQueue(queueMap);

            //load image
            Picasso.Builder builder = new Picasso.Builder(orderImage.getContext());
            builder.listener((picasso, uri, exception) -> {
                Toast.makeText(orderImage.getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.e("picasso problem",exception.getMessage());
                //Log.e("picasso uri", uri.toString());
            });
            //Log.e("pic url", queue.getPictureUrl());
            Uri uri = Uri.parse(queue.getPictureUrl());
            builder.build().load(uri).into(orderImage);

            orderId.setText(String.valueOf(queue.getOrderId()));
            if (queue.getInCafe()) {
                inCafe.setText(R.string.yes);
                tableNo.setText(queue.getTableNo());
            }

            itemView.setOnClickListener(v -> {
                //burada işlemler başlatılacak
                if (listener != null) {
                    listener.onOrderSelected(snapshot);
                }
            });

        }

        private Queue makeQueue(Map<String, Object> queueMap) {
            Queue queue = new Queue();
            queue.setOrderId(Integer.parseInt(Objects.requireNonNull(queueMap.get("orderId")).toString()));
            queue.setCafeId(Integer.parseInt(Objects.requireNonNull(queueMap.get("cafeId")).toString()));
            queue.setUserId(Integer.parseInt(Objects.requireNonNull(queueMap.get("userId")).toString()));

            queue.setInCafe(Boolean.valueOf(Objects.requireNonNull(queueMap.get("inCafe")).toString()));
            queue.setState(Boolean.getBoolean(Objects.requireNonNull(queueMap.get("orderId")).toString()));

            queue.setTableNo(Objects.requireNonNull(queueMap.get("tableNo")).toString());
            queue.setPictureUrl(Objects.requireNonNull(queueMap.get("pictureUrl")).toString());

            queue.setTime(Long.parseLong(Objects.requireNonNull(queueMap.get("time")).toString()));

            return queue;
        }
    }


}
