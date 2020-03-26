package net.mrkaan.printer.observers;


public interface Observer {
    void update();
    void updateObserver(boolean bool);
    void updateObserverProgress(int percentage);
}
