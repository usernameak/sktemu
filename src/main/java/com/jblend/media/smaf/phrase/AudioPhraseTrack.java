package com.jblend.media.smaf.phrase;

public class AudioPhraseTrack extends PhraseTrackBase {
    private AudioPhrase phrase;

    public AudioPhraseTrack(int id) {
        super(id);
    }

    public void setPhrase(AudioPhrase p) {
        phrase = p;
    }

    public AudioPhrase getPhrase() {
        return phrase;
    }

    public void removePhrase() {
        phrase = null;
    }
}
