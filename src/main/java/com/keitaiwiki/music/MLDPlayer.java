package com.keitaiwiki.music;

import java.util.*;

/**
 * i-melody MLD sequence player. Uses a {@code Sampler} to generate output to a
 * sample buffer.
 * @see MLD
 * @see Sampler
 */
public class MLDPlayer extends SequencePlayer {
    // Instance fields
    private Channel[]        channels;      // Playback channels
    private float            framesPerTick; // Output frames in one tick
    private MLD              mld;           // Sequence resource
    private int              pendingTicks;  // Sequencer ticks to process
    private long             tickNow;       // Sequencer position in ticks
    private Track[]          tracks;        // Sequencer state


    //////////////////////////// Private Constants ////////////////////////////

    private static final int A4 = 48; // Key index bias



    ///////////////////////////////// Classes /////////////////////////////////

    // Playback channel
    private class Channel {
        Note[]          notesOn;  // All notes currently on keys
        ArrayList<Note> notesOut; // All notes that are generating output
    }

    // Music note
    private class Note {
        int channel;  // Output channel
        int gateTime; // Ticks before note expires
        int key;      // Key index
    }

    // Event list state
    private class Track {
        int       cuepoint; // Starting cuepoint
        boolean   finished; // Track has no more events
        int       index;    // Index within sequencer
        MLD.Track mld;      // Event list
        int       offset;   // Current event offset
        int       ticks;    // Event ticks until next event
    }



    ////////////////////////////// Constructors ///////////////////////////////

    /**
     * Begin MLD playback. Instances of a {@code Sampler} are used in
     * conjunction with the given sampling rate to render the sequence to a
     * sample buffer.
     * @param mld The MLD sequence to play.
     * @param sampler A {@code Sampler} from which instances will be taken to
     * generate output.
     * @param sampleRate The samples per second of the output.
     * @exception NullPointerException if {@code mld} or {@code sampler} is
     * {@code null}.
     * @exception IllegalArgumentException if {@code sampleRate} is a
     * non-number or is less than or equal to zero.
     * @see MLD
     * @see Sampler
     */
    public MLDPlayer(MLD mld, Sampler sampler, float sampleRate) {
        super(sampler, sampleRate);

        // Error checking
        if (mld == null)
            throw new NullPointerException("An MLD is required.");

        // Instance fields
        channels        = new Channel[16];
        this.mld        = mld;
        tracks          = new Track[mld.tracks.length];

        // Channels
        for (int x = 0; x < channels.length; x++) {
            Channel chan  = channels[x] = new Channel();
            chan.notesOn  = new Note[99]; // A0 .. C6
            chan.notesOut = new ArrayList<Note>();
        }

        // Tracks
        for (int x = 0; x < tracks.length; x++) {
            Track track = tracks[x] = new Track();
            track.index = x;
            track.mld   = mld.tracks[x];
        }

        // Prepare for playback
        reset();
    }



    ///////////////////////////// Public Methods //////////////////////////////

    /**
     * Determine the total length of the sequence in seconds. Equivalent to
     * invoking {@code getDuration(withoutLoops)} on the underlying {@code MLD}
     * object.
     * @param withoutLooping Whether or not to consider looping in the return
     * value.
     * @return If the sequence does not loop, the number of seconds in the
     * sequence. If the sequence loops and {@code withoutLooping} is
     * {@code false}, returns {@code Double.POSITIVE_INFINITY}. If the sequence
     * loops and {@code withoutLooping} is {@code true}, returns the number of
     * seconds in the sequence up until the first loop occurs.
     * @see MLD#getDuration(boolean)
     */
    public double getDuration(boolean withoutLooping) {
        return mld.getDuration(withoutLooping);
    }

    /**
     * Retrieve the current playback position in the sequence. The range of
     * values represents the start of the sequence at 0.0 and either the end of
     * the sequence or the point where looping occurs at 1.0.
     * @return The proportion of the total sequence for the current playback
     * position.
     */
    public double getPosition() {
        return (double) tickNow / mld.tickEnd;
    }

    @Override
    public boolean isFinished() {
        if (!sampler.isFinished())
            return false;
        for (Track track : tracks) {
            if (!track.finished)
                return false;
        }
        return true;
    }

    ///////////////////////////// Protected Methods ///////////////////////////

    @Override
    protected boolean sequencerProcess() {
        // Process event ticks
        if (pendingTicks > 0) {

            // Sequencer
            tickNow += pendingTicks;

            // Notes
            for (MLDPlayer.Channel chan : channels) {
                for (MLDPlayer.Note note : chan.notesOut)
                    note.gateTime -= pendingTicks;
            }

            // Tracks
            for (MLDPlayer.Track track : tracks) {
                process(track, pendingTicks);
            }

            // Remove expired notes
            for (MLDPlayer.Channel chan : channels) {
                for (int x = 0; x < chan.notesOut.size(); x++) {
                    MLDPlayer.Note note = chan.notesOut.get(x);
                    if (note.gateTime != 0)
                        continue;
                    sampler.keyOff(note.channel, note.key);
                    chan.notesOut.remove(x--);
                    chan.notesOn[A4 + note.key] = null;
                }
            }

        }

        // Determine how many ticks and frames can be processed next
        int untilTrack = untilTrack();
        if (untilTrack == -1) {
            finished = true;
            return false;
        }
        int untilNote  = untilNote();
        pendingTicks   = untilNote == -1 ?
                untilTrack : Math.min(untilTrack, untilNote);
        pendingFrames += (float) Math.floor(pendingTicks * framesPerTick);
        return true;
    }


    ///////////////////////////// Private Methods /////////////////////////////

    // Process events on a track
    private void process(Track track, int ticks) {

        // The track has finished
        if (track.finished)
            return;

        // Update state
        track.ticks -= ticks;
        if (track.ticks > 0)
            return;

        // Process all events this tick
        while (track.ticks == 0) {
            MLD.Event event = track.mld.get(track.offset);

            // Process the event
            switch (event.type) {
                case MLD.EVENT_TYPE_NOTE    : evtNote   (track, event); break;
                case MLD.EVENT_TYPE_EXT_B   : evtExtB   (track, event); break;
                case MLD.EVENT_TYPE_EXT_INFO: evtExtInfo(track, event); break;
                default: setTrackOffset(track, track.offset + 1);
            }

            // Stop processing events
            if (track.finished)
                return;

            // Schedule the next event
            track.ticks = track.mld.get(track.offset).delta;
        }

    }

    // Initialize state in preparation for playback
    @Override
    protected void reset() {
        super.reset();

        // Instance fields
        pendingTicks  = 0;
        tickNow       = 0;
        setTempo(48, 125);

        // Channels
        for (Channel chan : channels) {
            for (int x = 0; x < chan.notesOn.length; x++)
                chan.notesOn[x] = null;
            chan.notesOut.clear();
        }

        // Tracks
        for (Track track : tracks) {
            track.cuepoint = -1;
            track.offset   = track.mld.cue;
            track.ticks    =  0;
            track.finished = track.offset >= track.mld.size();
        }

        // Initialize playback
        finished = true;
        for (Track track : tracks) {
            process(track, 0);
            finished = finished && track.finished;
        }
    }

    // Compute the number of output frames in one event tick
    private void setTempo(int timebase, int tempo) {
        framesPerTick = (60 * sampleRate) / (timebase * tempo);
    }

    // Specify the event offset of a track
    private void setTrackOffset(Track track, int offset) {

        // Configure the track
        track.offset   = offset;
        track.finished = offset >= track.mld.size();

        // Raise an event
        if (!track.finished || !getPlaybackEventsEnabled())
            return;
        boolean finished = true;
        for (Track other : tracks)
            finished = finished && other.finished;
        if (finished) {
            raisePlaybackEvent(getTime(), EVENT_END, 0);
        }
    }

    // Determine how many ticks can be processed until a note expires
    private int untilNote() {
        int ret = -1;
        for (Channel chan : channels)
        for (Note note : chan.notesOut) {
            if (ret == -1 || note.gateTime < ret)
                ret = note.gateTime;
        }
        return ret;
    }

    // Determine how many ticks can be processed until the next event
    private int untilTrack() {
        int ret = -1;
        for (Track track : tracks) {
            if (track.finished)
                continue;
            if (ret == -1 || track.ticks < ret)
                ret = track.ticks;
        }
        return ret;
    }



    ////////////////////////////// Event Methods //////////////////////////////

    // bank-change
    private void evtBankChange(Track track, MLD.Event event) {
        sampler.bankChange(event.channel, event.bank);
        setTrackOffset(track, track.offset + 1);
    }

    // cuepoint
    private void evtCuepoint(Track track, MLD.Event event) {

        // cuepoint-end
        if (event.cuepoint == MLD.CUEPOINT_END && tracks[0].cuepoint != -1) {
            for (Track t : tracks) {
                setTrackOffset(t, t.cuepoint);
            }
            raisePlaybackEvent(getTime(), EVENT_LOOP, 0);
            return;
        }

        // Common processing
        setTrackOffset(track, track.offset + 1);

        // cuepoint-start
        if (event.cuepoint == MLD.CUEPOINT_START) {
            for (Track t : tracks)
                t.cuepoint = t.offset;
        }

    }

    // drum-enable
    private void evtDrumEnable(Track track, MLD.Event event) {
        sampler.drumMode(event.channel, event.enable ? Sampler.DRUM_MODE_MFI : Sampler.DRUM_MODE_NONE);
        setTrackOffset(track, track.offset + 1);
    }

    // end-of-track
    private void evtEndOfTrack(Track track, MLD.Event event) {
        track.finished = true;
    }

    // ext-B event
    private void evtExtB(Track track, MLD.Event e) {
        switch (e.id) {
            case MLD.EVENT_BANK_CHANGE    : evtBankChange   (track, e); break;
            case MLD.EVENT_CUEPOINT       : evtCuepoint     (track, e); break;
            case MLD.EVENT_END_OF_TRACK   : evtEndOfTrack   (track, e); break;
            case MLD.EVENT_MASTER_VOLUME  : evtMasterVolume (track, e); break;
            case MLD.EVENT_MASTER_TUNE    : evtMasterTune   (track, e); break;
            case MLD.EVENT_PANPOT         : evtPanPot       (track, e); break;
            case MLD.EVENT_PITCHBEND      : evtPitchBend    (track, e); break;
            case MLD.EVENT_PITCHBEND_RANGE: evtPitchRange   (track, e); break;
            case MLD.EVENT_PROGRAM_CHANGE : evtProgramChange(track, e); break;
            case MLD.EVENT_TIMEBASE_TEMPO : evtTimebaseTempo(track, e); break;
            case MLD.EVENT_VOLUME         : evtVolume       (track, e); break;
            case MLD.EVENT_X_DRUM_ENABLE  : evtDrumEnable   (track, e); break;

            // Not implemented
            //case EVENT_JUMP:
            //case EVENT_CHANNEL_ASSIGN:
            //case EVENT_NOP:
            //case EVENT_PART_CONFIGURATION:
            //case EVENT_PAUSE:
            //case EVENT_RESET:
            //case EVENT_STOP:
            //case EVENT_WAVE_CHANNEL_VOLUME:
            //case EVENT_WAVE_CHANNEL_PANPOT:

            // Unrecognized events
            default: setTrackOffset(track, track.offset + 1);
        }
    }

    // ext-info event
    private void evtExtInfo(Track track, MLD.Event e) {
        sampler.sysEx(e.data);
        setTrackOffset(track, track.offset + 1);
    }

    // note
    private void evtNote(Track track, MLD.Event event) {
        Channel chan = channels[event.channel];
        Note    note = chan.notesOn[A4 + event.key];

        // Common processing
        setTrackOffset(track, track.offset + 1);

        // Raise an event
        raiseKeyEvent(event.key);

        // Velocity 0 is regarded as key-off
        if (event.velocity == 0) {
            sampler.keyOff(event.channel, event.key);
            if (note != null) {
                chan.notesOn[A4 + event.key] = null;
                chan.notesOut.remove(note);
            }
            return;
        }

        // Velocity not zero is regarded as key-on
        if (!seeking)
            sampler.keyOn(event.channel, event.key, event.velocity);

        // Get or create the note for this key
        if (note == null) {
            note = new Note();
            note.channel  = event.channel;
            note.gateTime = 0;
            note.key      = event.key;
            chan.notesOn[A4 + event.key] = note;
            chan.notesOut.add(note);
        }

        // Reconfigure the note
        note.gateTime = event.gateTime;
    }

    // master-volume
    private void evtMasterVolume(Track track, MLD.Event event) {
        sampler.masterVolume(event.volume);
        setTrackOffset(track, track.offset + 1);
    }

    // master-tune
    private void evtMasterTune(Track track, MLD.Event event) {
        sampler.masterTune(event.semitones);
        setTrackOffset(track, track.offset + 1);
    }

    // panpot
    private void evtPanPot(Track track, MLD.Event event) {
        sampler.panpot(event.channel, event.panpot);
        setTrackOffset(track, track.offset + 1);
    }

    // pitchbend
    private void evtPitchBend(Track track, MLD.Event event) {
        sampler.pitchBend(event.channel, event.semitones);
        setTrackOffset(track, track.offset + 1);
    }

    // pitchbend-range
    private void evtPitchRange(Track track, MLD.Event event) {
        sampler.pitchBendRange(event.channel, event.range);
        setTrackOffset(track, track.offset + 1);
    }

    // program-change
    private void evtProgramChange(Track track, MLD.Event event) {
        sampler.programChange(event.channel, event.program);
        setTrackOffset(track, track.offset + 1);
    }

    // timebase-tempo
    private void evtTimebaseTempo(Track track, MLD.Event event) {
        if (event.timebase == -1)
            return;
        float prev = framesPerTick;
        setTempo(event.timebase, event.tempo);
        pendingFrames = pendingFrames * framesPerTick / prev;
        setTrackOffset(track, track.offset + 1);
    }

    // volume
    private void evtVolume(Track track, MLD.Event event) {
        sampler.volume(event.channel, event.volume);
        setTrackOffset(track, track.offset + 1);
    }

}
