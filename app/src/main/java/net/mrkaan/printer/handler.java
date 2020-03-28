package net.mrkaan.printer;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class handler {

    public void fun() {
        HandlerThread handlerThread = new HandlerThread("TesHandlerThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        handler.post(() -> {

        });
    }
}
