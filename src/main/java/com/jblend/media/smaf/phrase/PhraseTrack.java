package com.jblend.media.smaf.phrase;

public class PhraseTrack extends PhraseTrackBase {
    private PhraseTrack syncMaster;
    private Phrase phrase;

    public PhraseTrack(int id) {
        super(id);
    }

    public void setPhrase(Phrase p) {
        phrase = p;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    public void removePhrase() {
        phrase = null;
    }

    public void setSubjectTo(PhraseTrack master) {
        syncMaster = master;
    }

    public PhraseTrack getSyncMaster() {
        return syncMaster;
    }
}
