package net.mrkaan.printer.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import net.mrkaan.printer.Constants;
import net.mrkaan.printer.DebugLog;
import net.mrkaan.printer.ObservableSingleton;
import net.mrkaan.printer.R;
import net.mrkaan.printer.Util;
import net.mrkaan.printer.adapter.WifiAdapter;
import net.mrkaan.printer.observers.Observable;
import net.mrkaan.printer.observers.Observer;

import java.util.ArrayList;
import java.util.List;

public class WifiListActivity extends Activity implements View.OnClickListener, Observer {

    private ListView mListWifi;
    private Button mBtnScan;

    private WifiManager mWifiManager;
    private WifiAdapter adapter;
    private WifiListener mWifiListener;

    private List<ScanResult> mScanResults = new ArrayList<ScanResult>();

    private Observable mObservable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        DebugLog.write();
        try {
            mObservable = ObservableSingleton.getInstance();
            mObservable.attach(this);

            mBtnScan = (Button) findViewById(R.id.btnNext);
            mBtnScan.setOnClickListener(this);

            mListWifi = (ListView) findViewById(R.id.wifiList);

            mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (mWifiManager.isWifiEnabled() == false) {
                Toast.makeText(getApplicationContext(), "Enabling WiFi..", Toast.LENGTH_LONG).show();
                mWifiManager.setWifiEnabled(true);
            }

            mWifiListener = new WifiListener();

            adapter = new WifiAdapter(WifiListActivity.this, mScanResults);
            mListWifi.setAdapter(adapter);

            mListWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    connectToWifi(i);
                }
            });
        } catch (Exception e) {
            DebugLog.write();
            mObservable.notifyObserver(true);
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            DebugLog.write();
            registerReceiver(mWifiListener, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            mWifiManager.startScan();
        } catch (Exception e) {
            DebugLog.write();
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            DebugLog.write();
            unregisterReceiver(mWifiListener);
        } catch (Exception e) {
            DebugLog.write();
            e.printStackTrace();
        }
    }

    private void finishActivity(final WifiConfiguration mWifiConfiguration, int networkId, final int resultCode) {
        // Don't know how to get class of device -> wifi or printer..
        // for now whatever user clicks assuming that.. It is a printer.. and saving that details in shared Prefs.
        DebugLog.write();
        if (resultCode == Constants.RESULT_CODE_PRINTER) {
            mWifiConfiguration.networkId = networkId;
            DebugLog.write("Code:"+resultCode+" networkId:"+networkId);
            if (networkId != -1) {
                DebugLog.write();
                Util.savePrinterConfiguration(WifiListActivity.this, mWifiConfiguration);
                Intent intent = new Intent();
                setResult(resultCode, intent);
                finish();
            } else { DebugLog.write();
                Toast.makeText(WifiListActivity.this, "Failed to connect to wifi", Toast.LENGTH_SHORT).show();
            }

        } else if (resultCode == Constants.RESULT_CODE_PRINTER_CONNECT_FAILED) {
            DebugLog.write("CONNECTED");
            Util.savePrinterConfiguration(WifiListActivity.this, null);
            Intent intent = new Intent();
            setResult(resultCode, intent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        DebugLog.write();
        mWifiManager.startScan();
        Toast.makeText(this, "Scanning....", Toast.LENGTH_SHORT).show();
    }

    private void connectToWifi(int position) {

        try {
            DebugLog.write();
            final ScanResult item = mScanResults.get(position);

            String Capabilities = item.capabilities;

            //Then you could add some code to check for a specific security type.
            if (Capabilities.contains("WPA")) {
                DebugLog.write();
                AlertDialog.Builder builder = new AlertDialog.Builder(WifiListActivity.this);
                builder.setTitle("Password:");

                // Set up the input
                final EditText input = new EditText(WifiListActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if (!input.getText().toString().trim().isEmpty()) {
                            WifiConfiguration wifiConfiguration = new WifiConfiguration();
                            DebugLog.write("\"" + item.SSID + "\""+ " : "+"\"" + m_Text + "\"");
                           // wifiConfiguration.SSID = "\"" + item.SSID + "\"";
                           // wifiConfiguration.preSharedKey = "\"" + m_Text + "\"";

                           //  wifiConfiguration.SSID = "\"" +  "Photon" + "\"";
                           // wifiConfiguration.preSharedKey = "\"" + "Ev12345678E"  + "\"";
                            wifiConfiguration.SSID = "\"" +  "HP-Print-19-D" + "\"";
                            wifiConfiguration.preSharedKey = "\"" + "12345678"  + "\"";
                            wifiConfiguration.priority = 40;
                           // wifiConfiguration.BSSID =  "c8:54:4b:22:78:bb" ;
                           // wifiConfiguration.SSID = "Photon";
                          // wifiConfiguration.preSharedKey =   "Ev12345678E"  ;
                           // wifiConfiguration.SSID = "HP-Print-19-D";
                           // wifiConfiguration.preSharedKey =   "12345678"  ;
                            wifiConfiguration.hiddenSSID = true;
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                            /*
                            wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
                            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN); //ADDED FOR WPA
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // For WPA
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // For WPA2
                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);*/
                            int res = mWifiManager.addNetwork(wifiConfiguration);
                            DebugLog.write("WifiPreference add Network returned " + res);
                            boolean b = mWifiManager.enableNetwork(res, true);
                            DebugLog.write("WifiPreference enableNetwork returned " + b);

                            finishActivity(wifiConfiguration, res, Constants.RESULT_CODE_PRINTER);
                        } else {
                            Toast.makeText(WifiListActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            } else if (Capabilities.contains("WEP")) { DebugLog.write();
                AlertDialog.Builder builder = new AlertDialog.Builder(WifiListActivity.this);
                builder.setTitle("Title");

                // Set up the input
                final EditText input = new EditText(WifiListActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if (!m_Text.trim().isEmpty()) {
                            WifiConfiguration wifiConfiguration = new WifiConfiguration();
                            wifiConfiguration.SSID = "\"" + item.SSID + "\"";
                            wifiConfiguration.wepKeys[0] = "\"" + m_Text + "\"";
                            wifiConfiguration.wepTxKeyIndex = 0;
                            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                            int res = mWifiManager.addNetwork(wifiConfiguration);
                            Log.d("WifiPreference", "add Network returned " + res);
                            boolean b = mWifiManager.enableNetwork(res, true);
                            Log.d("WifiPreference", "enableNetwork returned " + b);

                            finishActivity(wifiConfiguration, res, Constants.RESULT_CODE_PRINTER);
                        } else {
                            Toast.makeText(WifiListActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            } else {
                DebugLog.write();
                WifiConfiguration wifiConfiguration = new WifiConfiguration();
                wifiConfiguration.SSID = "\"" + item.SSID + "\"";
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                int res = mWifiManager.addNetwork(wifiConfiguration);
                Log.d("WifiPreference", "add Network returned " + res);
                boolean b = mWifiManager.enableNetwork(res, true);
                Log.d("WifiPreference", "enableNetwork returned " + b);

                finishActivity(wifiConfiguration, res, Constants.RESULT_CODE_PRINTER);
            }
        } catch (Exception e) { DebugLog.write();
            e.printStackTrace();
        }

    }

    @Override
    public void update() {
        DebugLog.write();
        mObservable.detach(this);
    }

    @Override
    public void updateObserver(boolean bool) {
        DebugLog.write();
    }

    @Override
    public void updateObserverProgress(int percentage) {
        DebugLog.write();
    }

    @Override
    public void onBackPressed() {
        try {
            final AlertDialog.Builder alert = new AlertDialog.Builder(WifiListActivity.this);

            alert.setMessage("Do you want to cancel print?");

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finishActivity(null, -1, Constants.RESULT_CODE_PRINTER_CONNECT_FAILED);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class WifiListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                mScanResults = mWifiManager.getScanResults();
                DebugLog.write("scan result size "  + mScanResults.size());

                adapter.setElements(mScanResults);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}