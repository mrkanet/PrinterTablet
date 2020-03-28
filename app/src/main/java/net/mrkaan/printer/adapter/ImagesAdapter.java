package net.mrkaan.printer.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import net.mrkaan.printer.R;
import net.mrkaan.printer.model.ImagesPreloaded;

import java.util.Map;
import java.util.Objects;

public abstract class ImagesAdapter extends FirestoreAdapter<ImagesAdapter.ViewHolder> {

    protected abstract void onError();

    public interface OnImageSelectedListener {
        void onImageSelected(DocumentSnapshot snapshot);
    }

    private OnImageSelectedListener listener;

    protected ImagesAdapter(Query query, OnImageSelectedListener listener) {
        super(query);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_rv_images, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Bind(getSnapshot(position),listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cell_img);
        }

        void Bind(final DocumentSnapshot snapshot, final OnImageSelectedListener listener){
            Map<String, Object> imagesMap = snapshot.getData();
            assert imagesMap != null;
            ImagesPreloaded imageP = getImage(imagesMap);

            Picasso.Builder builder = new Picasso.Builder(image.getContext());
            builder.listener((picasso, uri, exception) -> {
                Toast.makeText(image.getContext(),"Hata: "+exception.getMessage(),Toast.LENGTH_SHORT).show();
            });

            Uri uri = Uri.parse(imageP.getImgUrl());
            builder.build().load(uri).into(image);

        }
        private ImagesPreloaded getImage(Map<String, Object> map){
            ImagesPreloaded preloaded = new ImagesPreloaded();
            preloaded.setImgName(Objects.requireNonNull(map.get("imgName")).toString());
            preloaded.setImgUrl(Objects.requireNonNull(map.get("imgUrl")).toString());
            return preloaded;
        }
    }


}
