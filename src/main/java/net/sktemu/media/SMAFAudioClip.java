package net.sktemu.media;

import com.keitaiwiki.music.*;
import com.skt.m.AudioClip;
import com.skt.m.ResourceAllocException;
import com.skt.m.UnsupportedFormatException;
import com.skt.m.UserStopException;

import javax.sound.sampled.*;
import java.io.IOException;

public class SMAFAudioClip implements AudioClip {
    private static final int BUFFER_SIZE = 512;
    private static final float SAMPLE_RATE = 44100;

    private final SourceDataLine dataLine;

    private final Sampler sampler;
    private SMAFPlayer player;
    private final Object playerLock = new Object();

    private static final int STATUS_PLAY = 1;
    private static final int STATUS_STOP = 2;

    private volatile int playbackStatus = STATUS_STOP;

    public SMAFAudioClip() throws IOException {
        AudioFormat fmt = new AudioFormat(
                44100,
                16,
                2,
                true,
                false
        );

        try {
            dataLine = AudioSystem.getSourceDataLine(fmt);
        } catch (LineUnavailableException e) {
            throw new IOException(e);
        }

        sampler = new MA3Sampler(MA3Sampler.FM_MA2, MA3Sampler.FM_MA2, MA3Sampler.WAVE_DRUM_NONE);
    }

    @Override
    public void open(byte[] data, int offset, int bufferSize) throws UnsupportedFormatException, ResourceAllocException {
        try {
            dataLine.open();
        } catch (LineUnavailableException e) {
            throw new ResourceAllocException();
        }

        try {
            SMAF smaf = new SMAF(data);
            synchronized (playerLock) {
                player = new SMAFPlayer(smaf, sampler, SAMPLE_RATE);
            }
        } catch (IOException | FormatException e) {
            throw new UnsupportedFormatException();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (playerLock) {
            stop();
            player = null;

            dataLine.close();
        }
    }

    @Override
    public void play() throws UserStopException, IOException {
        dataLine.start();
        try {
            float[] samples = new float[BUFFER_SIZE * 2];
            byte[] bytes = new byte[BUFFER_SIZE * 2 * 2];

            playbackStatus = STATUS_PLAY;

            while (playbackStatus != STATUS_STOP) {
                int numSamples;
                synchronized (playerLock) {
                    if (player != null) {
                        numSamples = player.render(samples, 0, BUFFER_SIZE);
                        if (numSamples < 0) {
                            playbackStatus = STATUS_STOP;
                            continue;
                        }
                    } else {
                        playbackStatus = STATUS_STOP;
                        continue;
                    }
                }

                for (int i = 0; i < numSamples * 2; i++) {
                    float scaledSample = samples[i] * 32767.f;
                    int s16Sample;
                    if (scaledSample < Short.MIN_VALUE) {
                        s16Sample = Short.MIN_VALUE;
                    } else if (scaledSample > Short.MAX_VALUE) {
                        s16Sample = Short.MAX_VALUE;
                    } else {
                        s16Sample = (int) scaledSample;
                    }
                    bytes[i * 2] = (byte) s16Sample;
                    bytes[i * 2 + 1] = (byte) (s16Sample >>> 8);
                }

                dataLine.write(bytes, 0, numSamples * 2 * 2);
            }
        } finally {
            dataLine.drain();
            dataLine.stop();
        }
    }

    @Override
    public void loop() throws UserStopException, IOException {
        play();
    }

    @Override
    public void stop() throws IOException {
        player.setTime(0);
        playbackStatus = STATUS_STOP;
    }

    @Override
    public void pause() throws IOException {
    }

    @Override
    public void resume() throws IOException {
    }
}
