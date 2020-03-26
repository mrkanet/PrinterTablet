package net.mrkaan.printer.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import net.mrkaan.printer.Constants;
import net.mrkaan.printer.R;
import net.mrkaan.printer.model.Queue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        TextView tableNo;
        TextView orderTime;

        ViewHolder(View itemView) {
            super(itemView);
            orderImage = itemView.findViewById(R.id.order_image);
            orderId = itemView.findViewById(R.id.order_id);
            tableNo = itemView.findViewById(R.id.table_no);
            orderTime = itemView.findViewById(R.id.order_time);
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

            Calendar cal = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            cal.setTimeInMillis(queue.getTime() + Constants.htomil);
            orderTime.setText(sdf.format(cal.getTime()));

            if (queue.getInCafe()) {
                tableNo.setText(queue.getTableNo());
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderSelected(snapshot);
                }
            });

        }

        private Queue makeQueue(Map<String, Object> queueMap) {
            Queue queue = new Queue();
            queue.setOrderId(Integer.parseInt(Objects.requireNonNull(queueMap.get("orderId")).toString()));
            queue.setCafeId(Objects.requireNonNull(queueMap.get("cafeId")).toString());
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
