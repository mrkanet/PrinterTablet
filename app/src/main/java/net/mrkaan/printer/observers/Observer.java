package net.mrkaan.printer.observers;


public interface Observer {
    public void update();
    void updateObserver(boolean bool);
    void updateObserverProgress(int percentage);
}
