package net.mrkaan.printer.services;


public interface PrintCompleteService {
    public void onMessage(int status);
    public void respondAfterWifiSwitch();
}
