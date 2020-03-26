package net.mrkaan.printer.services;

public interface PrintFragmentCommunicator {
    void respondOnPrintComplete();
    void respondOnPrinterSelect();
    void respondOnPrinterSelectCancelled();
}
