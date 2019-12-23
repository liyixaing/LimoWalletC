package com.xms.limowallet.core;

public class LMRepoHeadEntries {

    private String OID;
    private String refName;
    private String remoteURL;
    private boolean isMerge;

    LMRepoHeadEntries() {}

    public String getOID() {
        return OID;
    }

    public String getRefName() {
        return refName;
    }

    public String getRemoteURL() {
        return remoteURL;
    }

    public boolean isMerge() {
        return isMerge;
    }
}
