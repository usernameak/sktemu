package com.nttdocomo.ui;

import com.nttdocomo.util.EventListener;

public interface MediaListener extends EventListener {
    void mediaAction(MediaPresenter source, int type, int param);
}