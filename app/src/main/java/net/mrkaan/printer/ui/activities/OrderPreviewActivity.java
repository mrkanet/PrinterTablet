package net.mrkaan.printer.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.mrkaan.printer.R;

public class OrderPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_preview);
        Intent intent = getIntent();

        int orderid = intent.getIntExtra("orderId", -1000);
        int userid = intent.getIntExtra("userId", -1000);

        long time = intent.getLongExtra("time", -1000);

        String url = intent.getStringExtra("url");
        String tableno = intent.getStringExtra("tableNo");

        Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
        builder.listener((picasso, uri, exception) -> Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show());
        ImageView img = findViewById(R.id.img_order);
        builder.build().load(Uri.parse(url)).into(img);
        //setting texts
        TextView order = findViewById(R.id.order_id);
        order.setText("Sipariş numarası: " + orderid);
        TextView table = findViewById(R.id.table_no);
        table.setText("Masa numarası: " + tableno);
        TextView timex = findViewById(R.id.time);
        timex.setText("Sipariş zamanı: " + time);
        TextView user = findViewById(R.id.username);
        user.setText("Kullanıcı adı : " + userid);
        //setted


        findViewById(R.id.btnGoBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btnCancel).setOnClickListener(v -> {
            //burada siparişin iptal edildiği işlemler yapılacak
            Toast.makeText(getApplicationContext(), "Cancel successful", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btnPrint).setOnClickListener(v -> {
            //burada arduino ve print işlemleri yapılacak
            Toast.makeText(getApplicationContext(), "Print successful", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
