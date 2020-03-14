package net.mrkaan.printer.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import net.mrkaan.printer.DebugLog;
import net.mrkaan.printer.R;

public class PrintActivity extends AppCompatActivity {

    WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://homepages.cae.wisc.edu/~ece533/images/boat.png");
    }


    public void onClickPrint(View view) {
        DebugLog.write();

        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FILL);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.tjpg);
        photoPrinter.printBitmap("launcher.jpg - test print", bitmap);

        /*

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = getString(R.string.app_name) + " Document";

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

        // Save the job object for later status checking
       // printJobs.add(printJob);

       */

    }
}