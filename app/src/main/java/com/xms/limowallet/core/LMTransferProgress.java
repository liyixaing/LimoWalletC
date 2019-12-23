package com.xms.limowallet.core;

abstract public class LMTransferProgress {

    private int totalObjects;
    private int indexedObjects;
    private int receivedObjects;
    private int localObjects;
    private int totalDeltas;
    private int indexedDeltas;
    private int receivedBytes;

    public abstract void ProgressUpdate( LMTransferProgress tp );

    public final void Reload() {
        this.ProgressUpdate(this);
    }

    public int getTotalObjects() {
        return totalObjects;
    }

    public int getIndexedObjects() {
        return indexedObjects;
    }

    public int getReceivedObjects() {
        return receivedObjects;
    }

    public int getLocalObjects() {
        return localObjects;
    }

    public int getTotalDeltas() {
        return totalDeltas;
    }

    public int getIndexedDeltas() {
        return indexedDeltas;
    }

    public int getReceivedBytes() {
        return receivedBytes;
    }
}
