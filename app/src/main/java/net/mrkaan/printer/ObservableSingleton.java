package net.mrkaan.printer;


import net.mrkaan.printer.observers.Observable;
import net.mrkaan.printer.observers.ObservableImpl;

public class ObservableSingleton {

    private static Observable mObservable;
    public static void initInstance() {
        if(mObservable==null){
            mObservable = new ObservableImpl();
        }
    }

    public static Observable getInstance(){
        return mObservable;
    }
}