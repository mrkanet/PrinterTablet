package net.mrkaan.printer.ui.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.koushikdutta.ion.Ion;

import net.mrkaan.printer.Constants;
import net.mrkaan.printer.DebugLog;
import net.mrkaan.printer.Json;
import net.mrkaan.printer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.mrkaan.printer.Constants.ACTION_USB_PERMISSION;
import static net.mrkaan.printer.Constants.currentImgName;

public class GCPActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int REQUEST_SINGIN = 1;
    private TextView txt, txtar, txtpdf;
    public static final String TAG = "mysupertag";
    public static final String URLBASE = "https://www.google.com/cloudprint/";
    private String YOUR_ACCESS_TOKEN;
    private String externalStorageDirectory;
    private String mPrinterId;
    private File mPdfFile;
    public static Bitmap imgBm;
    public Intent intentBefore;

    public UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    ArrayList<Integer> dataList;
    boolean two = false;
    float cap = 0f;


    //Defining a Callback which triggers whenever data is read.
    UsbSerialInterface.UsbReadCallback mCallback = arg0 -> {
        String data = new String(arg0);
        if (Integer.parseInt(data) == 2) {
            two = true;
        } else if (two) {
            if (dataList.size() < 10) {
                dataList.add(Integer.parseInt(data));
            } else {
                two = false;
                preparePdf();
            }
        }

    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_USB_PERMISSION:
                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        DebugLog.write();
                        connection = usbManager.openDevice(device);
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) { //Set Serial Connection Parameters.
                                serialPort.setBaudRate(9600);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(mCallback);


                            } else {
                                Log.d("SERIAL", "PORT NOT OPEN");
                            }
                        } else {
                            Log.d("SERIAL", "PORT IS NULL");
                        }
                    } else {
                        Log.d("SERIAL", "PERM NOT GRANTED");
                    }
                    break;

                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    findArduino();
                    break;

                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    stopConn();
                    break;
            }
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentBefore = getIntent();
        setContentView(R.layout.activity_g_c_p);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.gg_client_web_id))
                .requestEmail()
                .requestServerAuthCode(getString(R.string.gg_client_web_id))
                .requestScopes(new Scope("https://www.googleapis.com/auth/cloudprint"))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //sets up
        signIn();
        dataList = new ArrayList<>();
        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        findViewById(R.id.gcp_print_button).setOnClickListener(view -> {
            DebugLog.write(mPdfFile.getAbsolutePath());

            printPdf(mPdfFile.getAbsolutePath(), getResources().getString(R.string.gcp_id));
        });
        findViewById(R.id.create_pdf_button).setOnClickListener(view -> {
            DebugLog.write();
            createImage();
        });
        txt = findViewById(R.id.txt_sign);
        txtar = findViewById(R.id.txt_arduino);
        txtpdf = findViewById(R.id.txt_pdf);

        //setted

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                DebugLog.write("onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                DebugLog.write("onAuthStateChanged:signed_out");
            }
            // ...
        };



        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_SINGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                DebugLog.write(" error ");
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        DebugLog.write("firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    DebugLog.write("fsignInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    FirebaseUser user = task.getResult().getUser();
                    txt.setText(user.getDisplayName() + "\n" + user.getEmail());//todo
                    if (!task.isSuccessful()) {
                        DebugLog.write("fsignInWithCredential", task.getException());

                    }
                    getAccess(acct.getServerAuthCode());
                });
    }

    private void getAccess(String code) {
        String url = "https://www.googleapis.com/oauth2/v4/token";
        Ion.with(this)
                .load("POST", url)
                .setBodyParameter("client_id", getString(R.string.gg_client_web_id))
                .setBodyParameter("client_secret", getString(R.string.gg_client_web_secret))
                .setBodyParameter("code", code)
                .setBodyParameter("grant_type", "authorization_code")
                .asString()
                .withResponse()
                .setCallback((e, result) -> {
                    DebugLog.write("result: " + result.getResult());
                    if (e == null) {
                        try {
                            JSONObject json = new JSONObject(result.getResult());
                            getPrinters(json.getString("access_token"));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        DebugLog.write("error");
                    }
                });
    }

    private void getPrinters(String token) {
        DebugLog.write("TOKEN: " + token);
        YOUR_ACCESS_TOKEN = token;
        String url = URLBASE + "search";
        Ion.with(this)
                .load("GET", url)
                .addHeader("Authorization", "Bearer " + token)
                .asString()
                .withResponse()
                .setCallback((e, result) -> {
                    DebugLog.write("finished " + result.getHeaders().code() + ": " +
                            result.getResult());
                    mPrinterId = result.getResult();
                    if (e == null) {
                        DebugLog.write("nice");
                    } else {
                        DebugLog.write("error");
                    }
                });
    }


    private void printPdf(String pdfPath, String printerId) {
        String url = URLBASE + "submit";
        Ion.with(this)
                .load("POST", url)
                .addHeader("Authorization", "Bearer " + YOUR_ACCESS_TOKEN)
                .setMultipartParameter("printerid", printerId)
                .setMultipartParameter("title", "mrk")// buraya işlem başlığı yazılıyor
                .setMultipartParameter("ticket", getTicket())
                .setMultipartFile("content", "application/pdf", new File(pdfPath))
                .asString()
                .withResponse()
                .setCallback((e, result) -> {
                    if (e == null) {
                        DebugLog.write("PRINTTT CODE: " + result.getHeaders().code() +
                                ", RESPONSE: " + result.getResult());
                        Json j = Json.read(result.getResult());
                        if (j.at("success").asBoolean()) {
                            DebugLog.write("Success");
                        } else {
                            DebugLog.write("ERROR");
                        }
                    } else {
                        DebugLog.write("ERROR");
                        DebugLog.write("" + e.toString());
                    }
                });
    }


    private String getTicket() {
        Json ticket = Json.object();
        Json print = Json.object();
        ticket.set("version", "1.0");

        print.set("vendor_ticket_item", Json.array());
        print.set("color", Json.object("type", "STANDARD_MONOCHROME"));
        print.set("copies", Json.object("copies", 1));

        ticket.set("print", print);
        return ticket.toString();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        DebugLog.write("error connecting: " + connectionResult.getErrorMessage());
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_SINGIN);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void createImage() {

        Document document = new Document(new Rectangle(595, 842), 0, 0, 0, 0);
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addAuthor("Merhaba Android");
        document.addCreator("Mehaba Adroid 2");

        externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        File fol = new File(externalStorageDirectory, Constants.CONTROLLER_PDF_FOLDER);
        mPdfFile = new File(fol, currentImgName+".pdf");


        try {
            PdfWriter.getInstance(document, new FileOutputStream(mPdfFile));

            document.open();
            Image image = Image.getInstance(fol.getAbsolutePath() + "/"+currentImgName+".png");


            image.scaleAbsolute(calculateImageSize(cap), calculateImageSize(cap));
            float posBottomY = calculateAbsoluteBottomYPos(55.0f, cap);
            float posLeftX = calculateAbsoluteXPos(55.0f, cap);

            image.setAbsolutePosition(posLeftX, posBottomY);
            document.add(image);
            document.close();
            System.setProperty("http.agent", "Chrome");
            txtpdf.setText("Dosya hazır");
            findViewById(R.id.gcp_print_button).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("img_create", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }

    }

    private Float calculateImageSize(Float sensorDataMM) {
        return 2.8333f * sensorDataMM;
    }

    private Float calculateAbsoluteXPos(Float centerPointXMM, Float sensorDataMM) {
        return (centerPointXMM * 2.8333f) - (calculateImageSize(sensorDataMM) / 2);
    }

    private Float calculateAbsoluteBottomYPos(Float centerPointX, Float sensorDataMM) {
        return PageSize.A4.getHeight() - ((centerPointX * 2.8333f) + (calculateImageSize(sensorDataMM) / 2));
    }

    public void findArduino() {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x1a86)//Arduino Vendor ID
                {
                    DebugLog.write("Ardunio Vendor: " + deviceVID);
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    DebugLog.write("Ardunio Vendor Error");
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }
    }


    public void stopConn() {
        serialPort.close();
    }


    public void sendData(String data) {
        // datas will send
        serialPort.write(data.getBytes());
    }

    public void preparePdf() {
        int r = 0;
        if (dataList.size() == 10) {
            for (int i = 0; i < 10; i++) {
                r = r + dataList.get(i);
            }
            cap = (float) r / 10;
        }
        txtar.setText("Veriler hazır. Çap: " + cap);
    }


}