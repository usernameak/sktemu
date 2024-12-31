package com.jblend.media.smaf.phrase;

public class PhraseTrackBase {
    private final int id;
    private boolean isClaimed = false;

    private int volume = 127;
    private int panpot = 64;
    private boolean mute = false;

    public PhraseTrackBase(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void setClaimed(boolean value) {
        isClaimed = value;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void play() {

    }

    public void play(int loop) {

    }

    public void stop() {

    }

    public void pause() {

    }

    public void resume() {

    }

    public int getState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setVolume(int value) {
        volume = value;
    }

    public int getVolume() {
        return volume;
    }

    public void setPanpot(int value) {
        panpot = value;
    }

    public int getPanpot() {
        return panpot;
    }

    public void mute(boolean mute) {
        this.mute = mute;
    }

    public boolean isMute() {
        return mute;
    }

    public void setEventListener(PhraseTrackListener l) {

    }
}
