package net.mrkaan.printer.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.mrkaan.printer.Constants;
import net.mrkaan.printer.R;
import net.mrkaan.printer.photoeditor.EditImageActivity;

import java.util.Objects;

public class ChooseActivity extends AppCompatActivity /*implements ImagesAdapter.OnImageSelectedListener*/ {
    RecyclerView recyclerView;
    Button button;
    /*
     *Query query;
     *ImagesAdapter adapter;
     *private FirebaseFirestore vFirestore;
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        Intent intent = getIntent();
        button = findViewById(R.id.btn_upload);
        recyclerView = findViewById(R.id.rv_images);
        //vFirestore = FirebaseFirestore.getInstance();

        button.setOnClickListener(v ->
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setMultiTouchEnabled(false)
                        .setBackgroundColor(R.color.green_color_picker)
                        .setScaleType(CropImageView.ScaleType.CENTER)
                        .setFixAspectRatio(true)

                        .setInitialCropWindowPaddingRatio(0)
                        .setMinCropWindowSize(100, 100)
                        .setMaxZoom(8)

                        .start((Activity) v.getContext()));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                Intent i = new Intent(this, EditImageActivity.class);
                i.putExtra("image_uri", resultUri.toString());
                Constants.currentImgName = "";
                Constants.isSepiaNeeded = true;
                startActivityForResult(i, 1998);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Log.e("Crop Error", Objects.requireNonNull(result.getError().getMessage()));
            }
        }
    }

    /*
        @Override
        protected void onPause() {
            super.onPause();

        }
    */
    /*
            public void getRV() {
                setQuery();
                button.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new ImagesAdapter(query, this) {
                    @Override
                    protected void onError() {
                        Toast.makeText(getApplicationContext(), "Bir sorun oluştu", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onDataChanged() {
                        if (getItemCount() == 0) {
                            button.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else{
                            button.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        //super.onDataChanged();
                    }
                };

                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                if(adapter.getItemCount() == 0){
                    button.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else{
                    button.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                recyclerView.setAdapter(adapter);
            }

            public void setQuery() {
                query = vFirestore.collection("fzzpreloaded");
            }

            @Override
            public void onImageSelected(DocumentSnapshot snapshot) {
                ImagesPreloaded preloaded = getImage(Objects.requireNonNull(snapshot.getData()));

                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Constants.bm = bitmap;
                        Constants.isSepiaNeeded = false;
                        Constants.currentImgName = null;

                        Intent intent = new Intent(getApplicationContext(), EditImageActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
                builder.listener((picasso, uri, exception) -> Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show());
                builder.build().load(Uri.parse(preloaded.getImgUrl())).into(target);
            }


            private ImagesPreloaded getImage(Map<String, Object> map) {
                ImagesPreloaded preloaded = new ImagesPreloaded();
                preloaded.setImgName(Objects.requireNonNull(map.get("imgName")).toString());
                preloaded.setImgUrl(Objects.requireNonNull(map.get("imgUrl")).toString());
                return preloaded;
            }
        */
    public void thisImage(View v) {
        int anibus, clown, coffee, coffees, king, mummy, pharaoh, owl, socrates, yoda;
        mummy = findViewById(R.id.iv_mummy).getId();
        pharaoh = findViewById(R.id.iv_pharaoh).getId();
        owl = findViewById(R.id.iv_owl).getId();
        king = findViewById(R.id.iv_king).getId();
        clown = findViewById(R.id.iv_clown).getId();
        coffee = findViewById(R.id.iv_coffee).getId();
        coffees = findViewById(R.id.iv_coffees).getId();
        socrates = findViewById(R.id.iv_socrates).getId();
        yoda = findViewById(R.id.iv_yoda).getId();
        anibus = findViewById(R.id.iv_anibus).getId();
        int id = v.getId();

        Intent intent = new Intent(getApplicationContext(), EditImageActivity.class);

        if (id == anibus) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.anibus500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.anibus500);

        } else if (id == clown) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.clown500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.clown500);

        } else if (id == coffee) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.coffee500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.coffee500);

        } else if (id == coffees) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.coffees500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.coffees500);

        } else if (id == king) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.king500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.king500);

        } else if (id == mummy) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.mummy500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.mummy500);


        } else if (id == owl) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.owl500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.owl500);


        } else if (id == pharaoh) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.pharaoh500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.pharaoh500);

        } else if (id == socrates) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.socrates500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.socrates500);

        } else if (id == yoda) {
            Constants.bm = ((BitmapDrawable) getResources().getDrawable(R.drawable.yoda500)).getBitmap();
            intent.putExtra("image_uri", R.drawable.yoda500);

        } else {
            Toast.makeText(getApplicationContext(), "Hata", Toast.LENGTH_SHORT).show();
            return;
        }


        Constants.currentImgName = "";
        Constants.whereImg = Constants.shape;
        Constants.isSepiaNeeded = false;

        @SuppressLint("CutPasteId")
        ImageView[] viewList = {
                findViewById(R.id.iv_mummy),
                findViewById(R.id.iv_pharaoh),
                findViewById(R.id.iv_owl),
                findViewById(R.id.iv_king),
                findViewById(R.id.iv_clown),
                findViewById(R.id.iv_coffee),
                findViewById(R.id.iv_coffees),
                findViewById(R.id.iv_socrates),
                findViewById(R.id.iv_yoda),
                findViewById(R.id.iv_anibus)};

        free(viewList);

        startActivity(intent);
        finish();

    }

    public void free(ImageView[] vievList) {
        for (int i = 0; i < vievList.length; i++) {
            vievList[i].setImageDrawable(null);
        }
    }

    public void full(ImageView[] vievList) {

        vievList[0].setImageDrawable(getResources().getDrawable(R.drawable.mummy500));
        vievList[1].setImageDrawable(getResources().getDrawable(R.drawable.pharaoh500));
        vievList[2].setImageDrawable(getResources().getDrawable(R.drawable.owl500));
        vievList[3].setImageDrawable(getResources().getDrawable(R.drawable.king500));
        vievList[4].setImageDrawable(getResources().getDrawable(R.drawable.clown500));
        vievList[5].setImageDrawable(getResources().getDrawable(R.drawable.coffee500));
        vievList[6].setImageDrawable(getResources().getDrawable(R.drawable.coffees500));
        vievList[7].setImageDrawable(getResources().getDrawable(R.drawable.socrates500));
        vievList[8].setImageDrawable(getResources().getDrawable(R.drawable.yoda500));
        vievList[9].setImageDrawable(getResources().getDrawable(R.drawable.anibus500));
    }


}
