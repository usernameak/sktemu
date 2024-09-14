package com.nttdocomo.ui;

public interface MediaPresenter {
    public void setData(MediaData data);
    public MediaResource getMediaResource();
    public void play();
    public void stop();
    public void setAttribute(int attrib, int value);
    public void setMediaListener(MediaListener listener);
}
