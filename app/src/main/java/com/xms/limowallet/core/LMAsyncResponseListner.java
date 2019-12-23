package com.xms.limowallet.core;

abstract public class LMAsyncResponseListner<T> {

    public void OnCoreExpection( LMCoreException e ) {
        e.printStackTrace();
    }

    public void OnExpection( Exception e ) {
        e.printStackTrace();
    }

    public abstract void OnSuccess( T response );

}