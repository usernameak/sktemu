package com.nttdocomo.util;

import com.nttdocomo.ui.PushManager;

public interface ParameterPushListener extends EventListener {
    void parameterPushed(PushManager source);
}