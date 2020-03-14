package net.mrkaan.printer.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import net.mrkaan.printer.DebugLog;
import net.mrkaan.printer.R;

import java.util.ArrayList;
import java.util.List;

public class WifiAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<ScanResult> mWifiList = new ArrayList<ScanResult>();

    public WifiAdapter(Activity mActivity, List<ScanResult> mWifiList) { DebugLog.write();
        this.mActivity = mActivity;
        this.mWifiList = mWifiList;
    }

    @Override
    public int getCount() {
        return mWifiList.size();
    }

    @Override
    public Object getItem(int i) {
        return mWifiList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DebugLog.write();
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_wifi_list_item, null);

        TextView txtWifiName = (TextView) view.findViewById(R.id.txtWifiName);
        txtWifiName.setText(mWifiList.get(i).SSID);

        return view;
    }

    public void setElements(List<ScanResult> mWifis) { DebugLog.write();
        this.mWifiList = mWifis;
        notifyDataSetChanged();
    }
}