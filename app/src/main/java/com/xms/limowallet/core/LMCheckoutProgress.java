package com.xms.limowallet.core;

abstract public class LMCheckoutProgress {

    private int curSize;

    private int totalSize;

    public abstract void ProgressUpdate( LMCheckoutProgress cp );

    public final void Reload() {
        this.ProgressUpdate(this);
    }

    public final int getCurSize() {
        return curSize;
    }

    public final void setCurSize(int curSize) {
        this.curSize = curSize;
    }

    public final int getTotalSize() {
        return totalSize;
    }

    public final void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }
}
