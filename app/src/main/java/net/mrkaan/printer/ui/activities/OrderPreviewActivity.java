package net.mrkaan.printer.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.mrkaan.printer.Constants;
import net.mrkaan.printer.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrderPreviewActivity extends AppCompatActivity {

    public static int defValue = -1000;
    public static long defValueL = -1000;
    public static int PRINT_REQ_CODE = 7768;
    public static int PRINT_RES_CODE = 7761;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_preview);
        Intent intent = getIntent();


        //todo buradaki buton siparişin iptali işlemlerini yapacak firebase
        //todo ile haberleşilmesi ve veride değişiklik yapılması gerekiyor
        Button b = findViewById(R.id.btnCancel);
        b.setVisibility(View.GONE);
        /*
        b.setOnClickListener(v -> {
            //cancel things
        });
        */
        findViewById(R.id.btnGoBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btnPrint).setOnClickListener(v -> {
            //burada arduino ve print işlemleri yapılacak

            String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
            long time = System.currentTimeMillis();
            File imageFile = new File(externalStorageDirectory, Constants.CONTROLLER_PDF_FOLDER + "/" + time + ".jpg");

            try {
                FileOutputStream out = new FileOutputStream(imageFile);
                boolean t = Constants.bm.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                Toast.makeText(getApplicationContext(), String.valueOf(t), Toast.LENGTH_SHORT).show();
                Constants.currentImgName = time + ".png";
                out.flush();
                out.close();
                // PNG is a lossless format, the compression factor (100) is ignored

            } catch (Exception e) {
                e.printStackTrace();
            }


            Intent i = new Intent(getApplicationContext(), GCPActivity.class);
            //i.putExtra("image_bitmap", Constants.bm);
            startActivityForResult(i, PRINT_REQ_CODE);
        });


        setThingsUp(intent);
    }

    @SuppressLint("SetTextI18n")
    private void setThingsUp(Intent intent) {
        int orderid = intent.getIntExtra("orderId", -1000);
        int userid = intent.getIntExtra("userId", -1000);

        long time = intent.getLongExtra("time", -1000);

        String url = intent.getStringExtra("url");
        String tableno = intent.getStringExtra("tableNo");

        if (time == -1000 || orderid == -1000 || userid == -1000) {
            Toast.makeText(getApplicationContext(), "Hatalı veri girildi", Toast.LENGTH_SHORT).show();
        }
        TextView table = findViewById(R.id.table_no);

        if (tableno == null || tableno.equals("")) {
            setViews(orderid, userid, time, url);
            table.setVisibility(View.GONE);
        } else {
            setViews(orderid, userid, time, url);

            table.setText(getResources().getString(R.string.table_num) + tableno);
        }


    }

    @SuppressLint("SetTextI18n")
    private void setViews(int orderid, int userid, long time, String url) {
        //setting texts
        TextView order = findViewById(R.id.order_id);
        order.setText(getResources().getString(R.string.order_num) + orderid);


        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        cal.setTimeInMillis(time + Constants.htomil);
        TextView timex = findViewById(R.id.time);
        timex.setText(getResources().getString(R.string.order_time) + sdf.format(cal.getTime()));
        TextView user = findViewById(R.id.username);
        user.setText(getResources().getString(R.string.user_name_lname) + userid);


        ImageView img = findViewById(R.id.img_order);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Constants.bm = bitmap;
                img.setImageBitmap(bitmap);
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
        builder.build().load(Uri.parse(url)).into(target);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRINT_REQ_CODE) {
            if (resultCode == PRINT_RES_CODE) {
                Toast.makeText(getApplicationContext(), "Basım başarılı", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "İptal edildi", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
