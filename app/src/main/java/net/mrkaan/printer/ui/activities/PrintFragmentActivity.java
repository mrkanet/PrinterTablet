package net.mrkaan.printer.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import net.mrkaan.printer.Constants;
import net.mrkaan.printer.DebugLog;
import net.mrkaan.printer.R;
import net.mrkaan.printer.services.PrintFragmentCommunicator;
import net.mrkaan.printer.ui.fragments.PrintFragment;


public class PrintFragmentActivity extends FragmentActivity {

    public PrintFragmentCommunicator mPrintFragmentCommunicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_fragment);
        DebugLog.write();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new PrintFragment())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { DebugLog.write();
        super.onActivityResult(requestCode, resultCode, data);
        DebugLog.write();
        if(requestCode == Constants.REQUEST_CODE_PRINTER && resultCode == Constants.RESULT_CODE_PRINTER) { DebugLog.write();
            if(mPrintFragmentCommunicator!=null){
                mPrintFragmentCommunicator.respondOnPrinterSelect();
            }
        } else if (requestCode == Constants.REQUEST_CODE_WIFI && resultCode == Constants.RESULT_CODE_PRINTER) { DebugLog.write();
            if(mPrintFragmentCommunicator!=null){
                mPrintFragmentCommunicator.respondOnPrintComplete();
            }
        } else if (requestCode == Constants.REQUEST_CODE_PRINTER && resultCode == Constants.RESULT_CODE_PRINTER_CONNECT_FAILED) { DebugLog.write();
            if(mPrintFragmentCommunicator!=null){
                mPrintFragmentCommunicator.respondOnPrinterSelectCancelled();
            }
        }
    }

}