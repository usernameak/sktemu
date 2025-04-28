package com.keitaiwiki.music;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class SequencePlayer {
    private ArrayList<Event>   events;        // Pending events
    private HashSet<Integer>   evtKeys;       // Key events enabled by key
    private boolean            evtPlayback;   // Playback events are enabled
    protected Sampler.Instance sampler;       // Sample generator
    protected float            sampleRate;    // Output sampling rate
    private long               position;      // Sequencer position in frames
    protected float            pendingFrames; // Output frames to process
    protected boolean          seeking;       // Processing setTime()
    protected boolean          finished;      // Sequencer has no more events



    //////////////////////////////// Constants ////////////////////////////////

    /**
     * Event type that notifies when a non-looping sequence finishes.
     * @see Event
     */
    public static final int EVENT_END = 0;

    /**
     * Event type that notifies when a sequence loops.
     * @see Event
     */
    public static final int EVENT_LOOP = 1;

    /**
     * Event type that notifies when a particular key is played.
     * @see Event
     */
    public static final int EVENT_KEY = 2;

    ////////////////////////////////// Event //////////////////////////////////

    /**
     * Notifies of a scenario that arises during playback. When configured, the
     * {@code render()} methods will terminate early any time an event
     * condition is satisfied. Events are obtained by the caller and
     * acknowledged via {@link MLDPlayer#getEvents()}.
     * @see MLDPlayer#getEvents()
     */
    public class Event {

        /**
         * Additional event data, if relevant. For {@code EVENT_KEY} events,
         * this will be the key number.
         */
        public int data;

        /**
         * Time in seconds since the beginning of playback when the event was
         * raised.
         */
        public double time;

        /**
         * Indicates the type of event that was raised: {@code EVENT_END},
         * {@code EVENT_KEY} or {@code EVENT_LOOP}.
         */
        public int type;

        // Internal constructor
        protected Event(double time, int type, int data) {
            this.data = data;
            this.time = time;
            this.type = type;
        }

    }

    public SequencePlayer(Sampler sampler, float sampleRate) {
        this(sampler, 10, sampleRate);
    }

    public SequencePlayer(Sampler sampler, int numChannels, float sampleRate) {
        if (sampler == null)
            throw new NullPointerException("A sampler is required.");
        if (!Float.isFinite(sampleRate) || sampleRate <= 0.0f)
            throw new IllegalArgumentException("Invalid sampling rate.");

        this.sampler    = sampler.instance(numChannels, sampleRate);
        this.sampleRate = sampleRate;

        events          = new ArrayList<>();
        evtKeys         = new HashSet<>();
        evtPlayback     = false;
        seeking         = false;
    }

    protected void reset() {
        position      = 0;
        pendingFrames = 0;

        // Initialize sampler
        sampler.reset();

        events.clear();
    }

    ///////////////////////////// Public Methods //////////////////////////////

    /**
     * Retrieve the total number of seconds played back so far.
     * @return The number of seconds processed, relative to the start of the
     * sequence.
     * @see MLDPlayer#setTime(double)
     * @see MLD#getDuration(boolean)
     */
    public double getTime() {
        return (double) position / sampleRate;
    }

    /**
     * Determine whether playback has completed. The sequence is considered
     * finished when all of its events have been processed and the last note
     * has stopped generating samples.
     * @return {@code true} if all playback has completed.
     */
    public abstract boolean isFinished();

    /**
     * Registers a key to raise events for during rendering. Key number 0 is
     * the note A<sub>4</sub>.
     * @param key A key number to register.
     * @see Event
     * @see MLDPlayer#getEvents()
     */
    public void addEventKey(int key) {
        evtKeys.add(key);
    }

    /**
     * Registers multiple keys to raise events for during rendering. Key number
     * 0 is the note A<sub>4</sub>.
     * @param keys A list of key numbers to register.
     * @exception NullPointerException if {@code keys} is {@code null}.
     * @see Event
     * @see MLDPlayer#getEvents()
     */
    public void addEventKeys(int[] keys) {
        if (keys == null)
            throw new NullPointerException("Key array is required.");
        for (int key : keys)
            evtKeys.add(key);
    }

    /**
     * Unregisters a keys from raising events during rendering.
     * @param key A key number to unregister.
     * @see Event
     * @see MLDPlayer#getEvents()
     */
    public void removeEventKey(int key) {
        evtKeys.remove(key);
    }

    /**
     * Unregisters multiple keys from raising events during rendering.
     * @param keys A list of key numbers to unregister.
     * @exception NullPointerException if {@code keys} is {@code null}.
     * @see Event
     * @see MLDPlayer#getEvents()
     */
    public void removeEventKeys(int[] keys) {
        if (keys == null)
            throw new NullPointerException("Key array is required.");
        for (int key : keys)
            evtKeys.remove(key);
    }

    /**
     * Specify whether or not to raise playback events. Playback events include
     * {@code EVENT_END} and {@code EVENT_LOOP}.
     * @param enabled Whether or not playback events can be raised during
     * rendering.
     * @see Event
     * @see MLDPlayer#getEvents()
     */
    public void setPlaybackEventsEnabled(boolean enabled) {
        evtPlayback = enabled;
    }

    public boolean getPlaybackEventsEnabled() {
        return evtPlayback;
    }

    /**
     * Retrieve and acknowledge all pending events. If this method is not
     * called, events will remain in the queue and prevent samples from being
     * rendered.
     * @return An array of all pending events, now acknowledged.
     * @see Event
     * @see MLDPlayer#addEventKey(int)
     * @see MLDPlayer#addEventKeys(int[])
     * @see MLDPlayer#setPlaybackEventsEnabled(boolean)
     */
    public Event[] getEvents() {
        Event[] ret = events.toArray(new Event[0]);
        events.clear();
        return ret;
    }

    /**
     * Specify the playback position of the sequence in seconds. The resulting
     * position in the sequence will be the earliest internal time at or after
     * {@code seconds}.<br><br>
     * If the end of the sequence is encountered during seeking, this method
     * will return {@code true}. When this happens, it is possible that the
     * position in the sequence retrieved by subsequent calls to
     * {@code getTime()} may be less than {@code seconds}.
     * @param seconds The number of seconds from the beginning of the sequence.
     * @return {@code true} if the end of the sequence was encountered during
     * the operation.
     * @exception IllegalArgumentException if {@code seconds} is a non-number
     * or is negative.
     * @see MLDPlayer#getTime()
     * @see MLD#getDuration(boolean)
     */
    public boolean setTime(double seconds) {

        // Error checking
        if (!Double.isFinite(seconds) || seconds < 0)
            throw new IllegalArgumentException("Invalid seconds.");

        // Compute the target number of frames
        long target = (long) Math.ceil(seconds * sampleRate);

        // Already at the target
        if (target == position)
            return isFinished();

        // Target is earlier than the current frame
        if (target < position)
            reset();

        // Seek forward to the target time
        seeking = true;
        render((float[]) null, 0,
                (int) (target - position), 0.0f, 0.0f, false, false);
        seeking = false;
        return isFinished();
    }


    /**
     * Generate output samples. This method is equivalent to
     * {@code render(samples, offset, frames, 1.0f, 1.0f, true, true)}.<br><br>
     * For information regarding the operations of this method, see
     * {@link Sampler.Instance#render(float[],int,int,float,float,boolean,boolean)}.
     * @param samples Output sample buffer.
     * @param offset Index in {@code samples} of the first audio frame to
     * output.
     * @param frames The number of audio frames to output.
     * @exception NullPointerException if {@code samples} is {@code null}.
     * @exception ArrayIndexOutOfBoundsException if {@code offset} is
     * negative, or if {@code offset + frames * 2 > samples.length}.
     * @exception IllegalArgumentException if {@code frames} is negative.
     * @return The number of samples generated, or -1 if playback has finished.
     * May be less than {@code frames} if playback of the underlying sequence
     * completes before all frames have been processed.
     * @see MLDPlayer#render(float[],int,int,float,float,boolean,boolean)
     * @see Sampler.Instance#render(float[],int,int,float,float,boolean,boolean)
     */
    public int render(float[] samples, int offset, int frames) {
        return render(samples, offset, frames, 1.0f, 1.0f, true, true);
    }

    /**
     * Generate output samples. This method is equivalent to
     * {@code render(samples, offset, frames, amplitude, amplitude,
     * true, true)}.<br><br>
     * For information regarding the operations of this method, see
     * {@link Sampler.Instance#render(float[],int,int,float,float,boolean,boolean)}.
     * @param samples Output sample buffer.
     * @param offset Index in {@code samples} of the first audio frame to
     * output.
     * @param frames The number of audio frames to output.
     * @param amplitude A multiplier that is applied to all samples
     * generated.
     * @return The number of samples generated, or -1 if playback has finished.
     * May be less than {@code frames} if playback of the underlying sequence
     * completes before all frames have been processed.
     * @exception NullPointerException if {@code samples} is {@code null}.
     * @exception ArrayIndexOutOfBoundsException if {@code offset} is
     * negative, or if {@code offset + frames * 2 > samples.length}.
     * @exception IllegalArgumentException if {@code frames} is negative, or if
     * {@code amplitude} is a non-number or is negative.
     * @see MLDPlayer#render(float[],int,int,float,float,boolean,boolean)
     * @see Sampler.Instance#render(float[],int,int,float,float,boolean,boolean)
     */
    public int render(float[] samples,int offset,int frames,float amplitude) {
        return render(samples,offset,frames,amplitude,amplitude,true,true);
    }

    /**
     * Generate output samples. This method is equivalent to
     * {@code render(samples, offset, frames, left, right, true, true)}.
     * <br><br>
     * For information regarding the operations of this method, see
     * {@link Sampler.Instance#render(float[],int,int,float,float,boolean,boolean)}.
     * @param samples Output sample buffer.
     * @param offset Index in {@code samples} of the first audio frame to
     * output.
     * @param frames The number of audio frames to output.
     * @param left A multiplier that is applied to all left-stereo samples
     * generated.
     * @param right A multiplier that is applied to all right-stereo samples
     * generated.
     * @return The number of samples generated, or -1 if playback has finished.
     * May be less than {@code frames} if playback of the underlying sequence
     * completes before all frames have been processed.
     * @exception NullPointerException if {@code samples} is {@code null}.
     * @exception ArrayIndexOutOfBoundsException if {@code offset} is
     * negative, or if {@code offset + frames * 2 > samples.length}.
     * @exception IllegalArgumentException if {@code frames} is negative, or if
     * {@code left} or {@code right} is a non-number or is negative.
     * @see MLDPlayer#render(float[],int,int,float,float,boolean,boolean)
     * @see Sampler.Instance#render(float[],int,int,float,float,boolean,boolean)
     */
    public int render(float[] samples, int offset, int frames,
                      float left, float right) {
        return render(samples, offset, frames, left, right, true, true);
    }

    /**
     * Generate output samples. <br><br>
     * For information regarding the operations of this method, see
     * {@link Sampler.Instance#render(float[],int,int,float,float,boolean,boolean)}.
     * <br><br>
     * If an event is raised during playback, rendering will stop and return
     * before generating any more samples. When this happens, the return value
     * may be less than {@code frames}. {@link MLDPlayer#getEvents()} should be called
     * after every call to {@code render()} while events are enabled.
     * @param samples Output sample buffer.
     * @param offset Index in {@code samples} of the first audio frame to
     * output.
     * @param frames The number of audio frames to output.
     * @param left A multiplier that is applied to all left-stereo samples
     * generated.
     * @param right A multiplier that is applied to all right-stereo samples
     * generated.
     * @param erase Replace the buffer contents when {@code true}, or add
     * to them when {@code false}
     * @param clamp Specifies whether to restrict the sample buffer values
     * to -1.0f to +1.0f inclusive.
     * @return The number of samples generated, or -1 if playback has finished.
     * May be less than {@code frames} if playback of the underlying sequence
     * completes before all frames have been processed.
     * @exception NullPointerException if {@code samples} is {@code null}.
     * @exception ArrayIndexOutOfBoundsException if {@code offset} is
     * negative, or if {@code offset + frames * 2 > samples.length}.
     * @exception IllegalArgumentException if {@code frames} is negative, or if
     * {@code left} or {@code right} is a non-number or is negative.
     * @see Sampler.Instance#render(float[],int,int,float,float,boolean,boolean)
     * @see MLDPlayer#getEvents()
     * @see MLDPlayer#render(float[],int,int)
     * @see MLDPlayer#render(float[],int,int,float)
     * @see MLDPlayer#render(float[],int,int,float,float)
     */
    public int render(float[] samples, int offset, int frames,
                      float left, float right, boolean erase, boolean clamp) {
        int ret = 0; // Total frames output so far

        // Error checking
        if (!seeking) {
            if (samples == null)
                throw new NullPointerException("A sample buffer is required.");
            if (frames < 0)
                throw new IllegalArgumentException("Invalid frames.");
            if (offset < 0 || offset + frames * 2 > samples.length) {
                throw new ArrayIndexOutOfBoundsException(
                        "Invalid range in sample buffer.");
            }
            if (!Float.isFinite(left ) || left  < 0.0f)
                throw new IllegalArgumentException("Invalid left amplitude.");
            if (!Float.isFinite(right) || right < 0.0f)
                throw new IllegalArgumentException("Invalid right amplitude.");
        }

        // Sequencer is not playing
        if (finished)
            return -1;

        // Process all output frames
        while (frames > 0) {

            // Events are pending
            if (events.size() != 0)
                return ret;

            // Process output frames
            while (pendingFrames > 0) {

                // Render the samples
                int f = Math.min(frames, (int) Math.floor(pendingFrames));
                if (!seeking)
                    sampler.render(samples,offset,f,left,right,erase,clamp);

                // State management
                frames        -= f;
                offset        += f * 2;
                pendingFrames -= f;
                position      += f;
                ret           += f;

                // All output frames have been processed
                if (frames == 0)
                    return ret;
            }

            if (!sequencerProcess()) {
                finished = true;
                return ret;
            }
        }

        return ret;
    }

    protected abstract boolean sequencerProcess();

    protected void raiseKeyEvent(int key) {
        if (evtKeys.contains(key))
            events.add(new Event(getTime(), EVENT_KEY, key));
    }

    protected void raisePlaybackEvent(double time, int type, int data) {
        if (evtPlayback) {
            events.add(new Event(time, type, data));
        }
    }
}
