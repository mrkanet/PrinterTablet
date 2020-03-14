package net.mrkaan.printer.services;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;


import net.mrkaan.printer.Constants;
import net.mrkaan.printer.DebugLog;
import net.mrkaan.printer.ObservableSingleton;
import net.mrkaan.printer.observers.Observable;
import net.mrkaan.printer.observers.Observer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader extends AsyncTask<String, Void, Boolean> implements Observer {

    private static final int MEGABYTE = 1024 * 1024;
    protected Observable mObservable;
    private Activity mActivity;
    private String extStorageDirectory;
    private File folder;
    private File pdfFile;
    private String mFileUrl, mFileName;
    public FileDownloader(Activity mActivity, String fileUrl, String fileName) {
        this.mActivity = mActivity;
        this.mFileUrl = fileUrl;
        this.mFileName = fileName;

        mObservable = ObservableSingleton.getInstance();
        mObservable.attach(this);
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        folder = new File(extStorageDirectory, Constants.CONTROLLER_PDF_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }

        pdfFile = new File(folder, mFileName);

        try {
            return downloadFile(mFileUrl, pdfFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }

    public Boolean downloadFile(String fileUrl
            , String fileName) {
        try {
            DebugLog.write();
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.flush();
            fileOutputStream.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update() {
        mObservable.detach(this);
    }

    @Override
    public void updateObserver(boolean bool) {

    }

    @Override
    public void updateObserverProgress(int percentage) {

    }

}