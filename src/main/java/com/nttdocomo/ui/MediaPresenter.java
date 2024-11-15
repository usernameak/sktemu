package com.nttdocomo.ui;

public interface MediaPresenter {
    void setData(MediaData data);

    MediaResource getMediaResource();

    void play();

    void stop();

    void setAttribute(int attrib, int value);

    void setMediaListener(MediaListener listener);
}
