package com.jblend.media.smaf.phrase;

public abstract class PhraseBase {
    public PhraseBase(byte[] data) {

    }

    public PhraseBase(String url) {

    }

    public int getSize() {
        return 0;
    }

    public int getUseTracks() {
        return 1;
    }
}
