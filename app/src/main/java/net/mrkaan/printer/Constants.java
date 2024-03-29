package net.mrkaan.printer;


import android.graphics.Bitmap;

public class Constants {
    public static final String DEMO_PREFERENCES = "demo_preferences";
    public static final String CONTROLLER_WIFI_CONFIGURATION = "controller_wifi_configuration";
    public static final String CONTROLLER_PRINTER_CONFIGURATION = "controller_printer_configuration";
    public static final String CONTROLLER_PRINTER = "printer";
    public static final String CONTROLLER_WIFI = "wifi";
    public static final String CONTROLLER_MOBILE = "mobile";

    public static final int PRINTER_STATUS_COMPLETED = 1;
    public static final int PRINTER_STATUS_CANCELLED = 0;

    public static final int REQUEST_CODE_PRINTER = 1000;
    public static final int REQUEST_CODE_WIFI = 999;
    public static final int RESULT_CODE_PRINTER = 1001;
    public static final int RESULT_CODE_PRINTER_CONNECT_FAILED = 1002;
    public static final int REQUEST_CODE__ACCESS_COARSE_LOCATION = 1003;
    public static final int RESULT_CODE__ACCESS_COARSE_LOCATION = 1003;
    public static final String CONTROLLER_PDF_FOLDER = "mrk.printer";
    public static final int deviceVID = 0x1a86;
    public static final String ACTION_USB_PERMISSION = "net.mrkaan.printer.USB_PERMISSION";

    public static final long htomil = 10800000; //gmt+3 için zaman eklemesi
    public static String currentImgName;
    public static Bitmap bm;
    public static final int REQ_CODE_PDF = 100;
    public static final float centerPoint = 55f;
    public static boolean isSepiaNeeded = true;
    public static final short order = 1;
    public static final short shape = 2;
    public static final short image = 3;
    public static short whereImg = order;

}