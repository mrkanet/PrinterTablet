package net.mrkaan.printer.services;


public interface PrintCompleteService {
    void onMessage(int status);
    void respondAfterWifiSwitch();
}
