package net.mrkaan.printer.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import net.mrkaan.printer.Constants;
import net.mrkaan.printer.DebugLog;
import net.mrkaan.printer.ObservableSingleton;
import net.mrkaan.printer.R;
import net.mrkaan.printer.Util;
import net.mrkaan.printer.observers.Observable;
import net.mrkaan.printer.observers.Observer;
import net.mrkaan.printer.services.PrintCompleteService;
import net.mrkaan.printer.services.PrintUtility;
import net.mrkaan.printer.services.WifiScanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MyActivity extends Activity implements Observer, PrintCompleteService {

    Dialog mPrintDialog;
    private Button mBtnPrint, mBtnPrintFromFragment, mBtnDownloadAndPrint,btnPrintPdf,btnCreatePdf,btnGcp;
    private File mPdfFile;
    private String externalStorageDirectory;
    private PrintUtility mPrintUtility;
    private WifiScanner mWifiScanner;
    private WifiManager mWifiManager;
    private Observable mObservable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        DebugLog.write();
        /*
        try {
            externalStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(externalStorageDirectory, Constants.CONTROLLER_PDF_FOLDER);
            pdfFile = new File(folder, "file name with extension");
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        mObservable = ObservableSingleton.getInstance();

        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiScanner = new WifiScanner();
        mPrintUtility = new PrintUtility(
                this,
                mWifiManager,
                mWifiScanner
        );

        mBtnPrint = (Button) findViewById(R.id.btnPrint);
        btnPrintPdf = (Button) findViewById(R.id.btnPrintPdf);
        btnCreatePdf = (Button) findViewById(R.id.btnCreatePdf);
        btnGcp = (Button) findViewById(R.id.btnGcp);

        mBtnDownloadAndPrint = (Button) findViewById(R.id.btnDownloadAndPrint);
        mBtnPrintFromFragment = (Button) findViewById(R.id.btnNext);

        mBtnDownloadAndPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrintDialog.show();
                mObservable.attach(MyActivity.this);
                if (Util.hasConnection(MyActivity.this)) {
                    mPrintUtility.downloadAndPrint("fileUrl", "fileName with extension");
                } else {
                    mObservable.notifyObserver(true);
                    Toast.makeText(MyActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrintDialog.show();
                mObservable.attach(MyActivity.this);
                mPrintUtility.print( mPdfFile);
            }
        });

        btnPrintPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DebugLog.write( mPdfFile.length());
              //  mPrintUtility.print( mPdfFile);

                if (Util.computePDFPageCount(mPdfFile) > 0) {
                    DebugLog.write();
                    mPrintUtility.printDocument(mPdfFile);
                }
            }
        });

        btnGcp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              DebugLog.write();
                Intent intent = new Intent(MyActivity.this, GCPActivity.class);

                startActivity(intent);
            }
        });

        btnCreatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DebugLog.write("Create Pdf");
               //stringtopdf("Merhaba");
               // createPdf();
                //createImage();
              //  createNativePdf();
                createNativePdf2();
                /*try {
                    externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File folder = new File(externalStorageDirectory, Constants.CONTROLLER_PDF_FOLDER);
                    mPdfFile = new File(folder, "test.pdf");
                    DebugLog.write(externalStorageDirectory);
                    DebugLog.write(folder.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });

        mBtnPrintFromFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { DebugLog.write();
                Intent iNext = new Intent(MyActivity.this, PrintFragmentActivity.class);
                startActivity(iNext);
            }
        });

        initPrintDialog();
        try {
            // This will give list of wifi available nearby.
            registerReceiver(mWifiScanner, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            boolean isSuccess= mWifiManager.startScan();
            if(isSuccess){
                DebugLog.write();
                List<ScanResult> results = mWifiManager.getScanResults();
                DebugLog.write(results.size());
            }else{
                DebugLog.write();
            }
            //  mPrintUtility.setScanResults(mWifiScanner.getScanResults());
        } catch (Exception e) { DebugLog.write();
            e.printStackTrace();
        }
    }

    private void initPrintDialog() { DebugLog.write();
        mPrintDialog = new Dialog(this);
        mPrintDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mPrintDialog.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPrintDialog.setContentView(R.layout.dialog_progressbar);


        mPrintDialog.setCancelable(true);
        mPrintDialog.setCanceledOnTouchOutside(false);

        mPrintDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) { DebugLog.write();
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MyActivity.this);

                    alert.setMessage("Do you want to cancel printing?");

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            mPrintUtility.onPrintCancelled();
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    alert.show();
                    return true;
                }
                return false;
            }
        });
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



            /*
            try {
                externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
                File folder = new File(externalStorageDirectory, Constants.CONTROLLER_PDF_FOLDER);
                pdfFile = new File(folder, "modal.pdf");
                DebugLog.write(externalStorageDirectory);
                DebugLog.write(folder.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }*/



            /*
            pdfFile = new File(getExternalFilesDir("pdf"), "test.pdf");
            Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", pdfFile);
            Log.e("create pdf uri path==>", "" + path);

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                finish();
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(),
                        "There is no any PDF Viewer",
                        Toast.LENGTH_SHORT).show();
                finish();
            }*/


    }

    @Override
    protected void onPause() {
        super.onPause();
        try { DebugLog.write();
            unregisterReceiver(mWifiScanner);
        } catch (Exception e) {
            e.printStackTrace(); DebugLog.write();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DebugLog.write();
        if (requestCode == Constants.REQUEST_CODE_PRINTER && resultCode == Constants.RESULT_CODE_PRINTER) {
            DebugLog.write();
            // stores printer configuration and prints..
            if (!mPrintDialog.isShowing())
                mPrintDialog.show();
            mPrintUtility.test( mPdfFile);
           // mPrintUtility.getPrinterConfigAndPrint();


        } else if (requestCode == Constants.REQUEST_CODE_WIFI && resultCode == Constants.RESULT_CODE_PRINTER) {
            // after switch back to wifi..
            DebugLog.write();

        } else if (requestCode == Constants.REQUEST_CODE_PRINTER && resultCode == Constants.RESULT_CODE_PRINTER_CONNECT_FAILED) {
            DebugLog.write();
            if (!mPrintDialog.isShowing())
                mPrintDialog.show();

            mPrintUtility.onPrintCancelled();

        }
    }

    @Override
    public void onBackPressed() {
        try { DebugLog.write();
            if (mPrintDialog != null && mPrintDialog.isShowing()) {

            } else {
                finish();
            }
        } catch (Exception e) { DebugLog.write();
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(int status) {
        // get List of PrintJob from PrintManager
        mPrintUtility.completePrintJob();
    }

    @Override
    public void respondAfterWifiSwitch() {
        // code after network switch completes.
    }

    @Override
    public void update() {
        if (mPrintDialog != null && mPrintDialog.isShowing()) {
            mPrintDialog.dismiss();
        }
        mObservable.detach(this);
    }

    @Override
    public void updateObserver(boolean bool) {
        try {
            mObservable.detach(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateObserverProgress(int percentage) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == Constants.REQUEST_CODE__ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // TODO: What you want to do when it works or maybe .PERMISSION_DENIED if it works better
            DebugLog.write();
        }
    }

    void createPdf(){
        String extstoragedir = Environment.getExternalStorageDirectory().toString();
        externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        File fol = new File(externalStorageDirectory, Constants.CONTROLLER_PDF_FOLDER);
        mPdfFile = new File(fol, "sample2.pdf");
        try{
            mPdfFile.createNewFile();
        Document document = new Document();
// Location to save
        PdfWriter.getInstance(document, new FileOutputStream( mPdfFile));
            document.open();
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Merhaba Android");
            document.addCreator("Mehaba Adroid 2");
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            Font mOrderDetailsTitleFont = new Font();
            // Creating Chunk
            Chunk mOrderDetailsTitleChunk = new Chunk("Order Details", mOrderDetailsTitleFont);
// Creating Paragraph to add...
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            document.add(mOrderDetailsTitleParagraph);
            document.add(lineSeparator);
            document.close();
        }catch (Exception e){}
// Open to write



    }



    void createImage(){
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.img);
        Document document = new Document();
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addAuthor("Merhaba Android");
        document.addCreator("Mehaba Adroid 2");
        externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        File fol = new File(externalStorageDirectory, Constants.CONTROLLER_PDF_FOLDER);
        mPdfFile = new File(fol, "sample2.pdf");

        try {
            URL url = new URL("https://i.picsum.photos/id/381/200/300.jpg?grayscale");
            PdfWriter.getInstance(document, new FileOutputStream(mPdfFile));

            document.open();
            document.add(new Paragraph("hello"));
            //Image image =  Image.getInstance(fol.getAbsolutePath()+"/sample.jpg");
            Image image =  Image.getInstance(url);
            image.scaleAbsolute(200f, 200f);

            document.add(image);
            document.close();
            System.setProperty("http.agent", "Chrome");
        }
        catch (Exception e){
e.printStackTrace();
        }

    }



    public void stringtopdf(String data)  {
        String extstoragedir = Environment.getExternalStorageDirectory().toString();
        File fol = new File(extstoragedir, "pdf");
        File folder=new File(fol,"pdf");
        if(!folder.exists()) {
            boolean bool = folder.mkdir();
        }
        try {
            mPdfFile = new File(folder, "sample.pdf");
            mPdfFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream( mPdfFile);


            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(100, 100, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            canvas.drawText(data, 10, 10, paint);



            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

        }catch (IOException e){
            Log.i("error",e.getLocalizedMessage());
        }

    }

    private void createNativePdf(){
        // create a new document
        PdfDocument document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(100, 100, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.RED);

        canvas.drawCircle(50, 50, 30, paint);

        // finish the page
        document.finishPage(page);

        // Create Page 2
        pageInfo = new PdfDocument.PageInfo.Builder(500, 500, 2).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawCircle(200, 200, 100, paint);
        document.finishPage(page);

        // write the document content
        String targetPdf = "/sdcard/test.pdf";
        mPdfFile = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(mPdfFile));
            DebugLog.write();
        } catch (IOException e) {
            e.printStackTrace();

        }

        // close the document
        document.close();
    }

    private void createNativePdf2(){
        // create a new document
        PdfDocument document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(100, 100, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.img);
        logo.setDensity(100);
        canvas.setDensity(72);
        canvas.drawBitmap(logo, 50, 50, null);
        document.finishPage(page);

        // write the document content
        String targetPdf = "/sdcard/test.pdf";
        mPdfFile = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(mPdfFile));
            DebugLog.write();
        } catch (IOException e) {
            e.printStackTrace();

        }

        // close the document
        document.close();
    }
}