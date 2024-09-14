package com.nttdocomo.ui;

public interface MediaSound extends MediaResource {
    String AUDIO_3D_RESOURCES = "3d.resources";
    
    void use(MediaResource overwritten, boolean useOnce);
}
