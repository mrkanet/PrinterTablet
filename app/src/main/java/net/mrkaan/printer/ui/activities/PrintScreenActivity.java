package net.mrkaan.printer.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.mrkaan.printer.DebugLog;
import net.mrkaan.printer.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PrintScreenActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    public final String ACTION_USB_PERMISSION = "net.mrkaan.printer.USB_PERMISSION";
    Button startButton, sendButton, clearButton, stopButton;
    TextView textView, textViewDebug;
    EditText editText;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            writeLog("onReceivedData");
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                tvAppend(textView, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                writeLog("UEE: " + e.getMessage());
            } catch (Exception e) {
                writeLog("Ex " + e.getMessage());
            }


        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            DebugLog.write();
            writeLog("onReceive");
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    writeLog("onReceive granted");
                    DebugLog.write();
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            tvAppend(textView, "Serial Connection Opened!\n");

                        } else {
                            writeLog("onReceive SERIAL PORT NOT OPEN");
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        writeLog("onReceive SERIAL PORT IS NULL");
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    writeLog("onReceive SERIAL PERM NOT GRANTED");
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickStop(stopButton);

            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_print_screen);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (Button) findViewById(R.id.buttonStart);
        sendButton = (Button) findViewById(R.id.buttonSend);
        clearButton = (Button) findViewById(R.id.buttonClear);
        stopButton = (Button) findViewById(R.id.buttonStop);
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        textViewDebug = (TextView) findViewById(R.id.textViewDebug);

        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
        writeLog("onCreate");
    }


    public void setUiEnabled(boolean bool) {
        DebugLog.write();
        startButton.setEnabled(!bool);
        sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);

    }

    public void onClickStart(View view) {
        writeLog("onClickStart");
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x1a86)//Arduino Vendor ID
                {
                    writeLog("onClickStart deviceVID: " + deviceVID);
                    DebugLog.write("Ardunio Vendor: " + deviceVID);
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    writeLog("onClickStart deviceVID  ELSE: " + deviceVID);
                    DebugLog.write("Ardunio Vendor Error");
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }


    }

    public void onClickSend(View view) {
        writeLog("onClickSend");
        String string = editText.getText().toString();
        serialPort.write(string.getBytes());
        tvAppend(textView, "\nData Sent : " + string + "\n");

    }

    public void onClickStop(View view) {
        DebugLog.write();
        writeLog("onClickStop");
        setUiEnabled(false);
        serialPort.close();
        tvAppend(textView, "\nSerial Connection Closed! \n");

    }

    public void onClickClear(View view) {
        DebugLog.write();
        writeLog("onClickClear");
        textView.setText(" ");
    }

    private void tvAppend(TextView tv, CharSequence text) {
        DebugLog.write();
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });


    }

    private void writeLog(String msg) {
        final String mssg = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewDebug.setText(mssg);
            }
        });
    }

    public void onClickOpenPrint(View view) {
        Intent intent = new Intent(this, PrintActivity.class);
        startActivity(intent);

    }


}