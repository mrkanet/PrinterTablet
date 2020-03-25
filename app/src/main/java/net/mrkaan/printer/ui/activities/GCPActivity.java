package net.mrkaan.printer.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
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
import java.util.Objects;

public class GCPActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int REQUEST_SINGIN = 1;
    private TextView txt;
    public static final String TAG = "mysupertag";
    public static final String URLBASE = "https://www.google.com/cloudprint/";
    private String YOUR_ACCESS_TOKEN;
    private String externalStorageDirectory;
    private String mPrinterId;
    private File mPdfFile;
    public static Bitmap imgBm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        findViewById(R.id.gcp_print_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugLog.write(mPdfFile.getAbsolutePath());

                printPdf(mPdfFile.getAbsolutePath(), getResources().getString(R.string.gcp_id));
            }
        });
        findViewById(R.id.create_pdf_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugLog.write();
                createImage();
            }
        });
        txt = findViewById(R.id.textView);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    DebugLog.write("onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    DebugLog.write("onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                    }
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
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.img);

        Document document = new Document(new Rectangle(595, 842), 0, 0, 0, 0);
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addAuthor("Merhaba Android");
        document.addCreator("Mehaba Adroid 2");

        externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        File fol = new File(externalStorageDirectory, Constants.CONTROLLER_PDF_FOLDER);
        mPdfFile = new File(fol, "sample3.pdf");
/*
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(info);

        Canvas c = page.getCanvas();
        Paint p = new Paint();
        p.setColor(Color.parseColor("#FFFFFF"));
        c.drawPaint(p);


        Bitmap bm = Bitmap.createScaledBitmap(imgBm, imgBm.getWidth(), imgBm.getHeight(), true);
        p.setColor(Color.BLUE);
        float posBottomY = calculateAbsoluteBottomYPos(55.0f, 68.0f);
        float posLeftX = calculateAbsoluteXPos(55.0f, 68.0f);
        c.drawBitmap(bm, posLeftX, posBottomY, null);
        pdfDocument.finishPage(page);
*/

        try {
            //URL url = new URL("https://i.picsum.photos/id/381/200/300.jpg?grayscale");
            PdfWriter.getInstance(document, new FileOutputStream(mPdfFile));

            document.open();
            //document.add(new Paragraph("hello"));
            Image image =  Image.getInstance(fol.getAbsolutePath()+"/kahve.png");

            //Image image = Image.getInstance(url);
            image.scaleAbsolute(calculateImageSize(68.0f), calculateImageSize(68.0f));
            //float y = PageSize.A4.getHeight() - image.getScaledHeight() - 50;
            float posBottomY = calculateAbsoluteBottomYPos(55.0f,68.0f);
            float posLeftX = calculateAbsoluteXPos(55.0f,68.0f);
            //DebugLog.write(PageSize.A4.getHeight()+" - " +image.getScaledHeight() );
            DebugLog.write("y:" + posBottomY);
            DebugLog.write("x:" + posLeftX);
            image.setAbsolutePosition(posLeftX, posBottomY);
            document.add(image);
            document.close();
            System.setProperty("http.agent", "Chrome");
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

}