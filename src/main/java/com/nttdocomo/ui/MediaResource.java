package com.nttdocomo.ui;

import com.nttdocomo.io.ConnectionException;

public interface MediaResource {
    void use() throws ConnectionException;
    void use(MediaResource overwritten, boolean useOnce) throws ConnectionException;
    void unuse();
    void dispose();

    void setProperty(String key, String value);
    String getProperty(String key);

    boolean isRedistributable();
    boolean setRedistributable(boolean redistributable);
}