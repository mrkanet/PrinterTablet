package net.mrkaan.printer.observers;


public interface Observable {
    void notifyObserver(boolean bool);
    void attach(Observer observer);
    void detach(Observer observer);
    void notify(Object param);
    void updateProgress(int percentage);
}
