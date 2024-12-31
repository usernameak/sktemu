package com.jblend.media.smaf.phrase;

public class PhrasePlayer {
    private static final PhrasePlayer instance = new PhrasePlayer();

    private static final int NUM_TRACKS = 8;
    private static final int NUM_AUDIO_TRACKS = 8;

    private static final PhraseTrack[] tracks = new PhraseTrack[NUM_TRACKS];
    private static final AudioPhraseTrack[] audioTracks = new AudioPhraseTrack[NUM_AUDIO_TRACKS];

    static {
        for (int i = 0; i < NUM_TRACKS; i++) {
            tracks[i] = new PhraseTrack(i);
        }
        for (int i = 0; i < NUM_AUDIO_TRACKS; i++) {
            audioTracks[i] = new AudioPhraseTrack(i);
        }
    }

    public static PhrasePlayer getPlayer() {
        return instance;
    }

    public void disposePlayer() {
        kill();
    }

    public PhraseTrack getTrack() {
        for (int i = NUM_TRACKS - 1; i >= 0; i--) {
            if (tracks[i].isClaimed()) continue;
            return getTrack(i);
        }
        throw new IllegalStateException("no free tracks");
    }

    public AudioPhraseTrack getAudioTrack() {
        for (int i = NUM_AUDIO_TRACKS - 1; i >= 0; i--) {
            if (audioTracks[i].isClaimed()) continue;
            return getAudioTrack(i);
        }
        throw new IllegalStateException("no free audio tracks");
    }

    public int getTrackCount() {
        return NUM_TRACKS;
    }

    public int getAudioTrackCount() {
        return NUM_AUDIO_TRACKS;
    }

    public PhraseTrack getTrack(int i) {
        PhraseTrack track = tracks[i];
        track.setClaimed(true);
        return track;
    }

    public AudioPhraseTrack getAudioTrack(int i) {
        AudioPhraseTrack audioTrack = audioTracks[i];
        audioTrack.setClaimed(true);
        return audioTrack;
    }

    public void disposeTrack(PhraseTrack t) {
        t.setClaimed(false);
    }

    public void disposeAudioTrack(AudioPhraseTrack t) {
        t.setClaimed(false);
    }

    public void kill() {
        pause();
        for (int i = 0; i < NUM_TRACKS; i++) {
            tracks[i].setClaimed(false);
        }
    }

    public void pause() {

    }

    public void resume() {

    }
}
