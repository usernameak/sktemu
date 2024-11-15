package com.nttdocomo.ui;

public class AudioPresenter implements MediaPresenter {
    public static final int AUDIO_PLAYING = 1;
    public static final int AUDIO_STOPPED = 2;
    public static final int AUDIO_COMPLETE = 3;
    public static final int AUDIO_SYNC = 4;
    public static final int AUDIO_PAUSED = 5;
    public static final int AUDIO_RESTARTED = 6;
    public static final int AUDIO_LOOPED = 7;
    public static final int PRIORITY = 1;
    public static final int SYNC_MODE = 2;
    public static final int TRANSPOSE_KEY = 3;
    public static final int SET_VOLUME = 4;
    public static final int CHANGE_TEMPO = 5;
    public static final int LOOP_COUNT = 6;
    public static final int ATTR_SYNC_OFF = 0;
    public static final int ATTR_SYNC_ON = 1;
    public static final int MIN_PRIORITY = 1;
    public static final int NORM_PRIORITY = 5;
    public static final int MAX_PRIORITY = 10;
    protected static final int MIN_VENDOR_ATTR = 64;
    protected static final int MAX_VENDOR_ATTR = 127;
    public static final int MIN_OPTION_ATTR = 128;
    public static final int MAX_OPTION_ATTR = 255;
    protected static final int MIN_VENDOR_AUDIO_EVENT = 64;
    protected static final int MAX_VENDOR_AUDIO_EVENT = 127;

    private MediaResource mediaResource;

    protected AudioPresenter() {

    }

    public static AudioPresenter getAudioPresenter() {
        return getAudioTrackPresenter();
    }

    public static AudioPresenter getAudioPresenter(int port) {
        return getAudioTrackPresenter();
    }

    public static AudioTrackPresenter getAudioTrackPresenter() {
        return new AudioTrackPresenter();
    }

    public int getCurrentTime() {
        return 0;
    }

    public int getTotalTime() {
        return 0;
    }

    @Override
    public void setData(MediaData data) {
        mediaResource = data;
    }

    @Override
    public MediaResource getMediaResource() {
        return mediaResource;
    }

    @Override
    public void play() {

    }

    public void play(int time) {

    }

    public void pause() {
    }

    public void restart() {
    }

    @Override
    public void stop() {

    }

    @Override
    public void setAttribute(int attrib, int value) {

    }

    @Override
    public void setMediaListener(MediaListener listener) {

    }

    public void setSound(MediaSound sound) {

    }
}
