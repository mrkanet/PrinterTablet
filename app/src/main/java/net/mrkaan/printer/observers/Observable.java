package net.mrkaan.printer.observers;


public interface Observable {
    public void notifyObserver(boolean bool);
    public void attach(Observer observer);
    public void detach(Observer observer);
    public void notify(Object param);
    public void updateProgress(int percentage);
}
