package net.mrkaan.printer.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;


import net.mrkaan.printer.DebugLog;

import java.util.List;

public class WifiScanner extends BroadcastReceiver {

    private List<ScanResult> mScanResults;

    @Override
    public void onReceive(Context context, Intent intent) { DebugLog.write();
        // fetch list of available wifi nearby.
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        setScanResults(mWifiManager.getScanResults());
    }

    public List<ScanResult> getScanResults() { DebugLog.write();
        return mScanResults;
    }

    public void setScanResults(List<ScanResult> mScanResults) { DebugLog.write();
        this.mScanResults = mScanResults;
    }
}