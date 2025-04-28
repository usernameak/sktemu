package com.keitaiwiki.music;

import java.util.ArrayList;

public class SMAFPlayer extends SequencePlayer {
    private final SMAF smaf;
    private float framesPerTick; // Output frames in one tick
    private int pendingTicks;  // Sequencer ticks to process
    private long tickNow;       // Sequencer position in ticks
    private Track[] tracks;        // Sequencer state


    //////////////////////////// Private Constants ////////////////////////////

    private static final int A4 = 80; // Key index bias

    ///////////////////////////////// Classes /////////////////////////////////

    // Playback channel
    private class Channel {
        Note[] notesOn;  // All notes currently on keys
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
        boolean finished; // Track has no more events
        int index;    // Index within sequencer
        SMAF.Track smaf;     // Event list
        int offset;   // Current event offset
        int ticks;    // Event ticks until next event

        Channel[] channels;

        public Track() {
            // Channels
            channels = new Channel[4];
            for (int x = 0; x < channels.length; x++) {
                Channel chan = channels[x] = new Channel();
                chan.notesOn = new Note[143]; // C#-2 .. C10
                chan.notesOut = new ArrayList<>();
            }
        }
    }

    public SMAFPlayer(SMAF smaf, Sampler sampler, float sampleRate) {
        super(sampler, smaf.tracks.size() * 4, sampleRate);

        // Instance fields
        this.smaf = smaf;
        tracks = new Track[smaf.tracks.size()];
        framesPerTick = (sampleRate * .001f);

        // Tracks
        for (int x = 0; x < tracks.length; x++) {
            Track track = tracks[x] = new Track();
            track.index = x;
            track.smaf = smaf.tracks.get(x);
        }

        // Prepare for playback
        reset();
    }

    @Override
    protected void reset() {
        super.reset();

        // Instance fields
        pendingTicks = 0;
        tickNow = 0;

        // Tracks
        for (SMAFPlayer.Track track : tracks) {
            track.offset = 0;
            track.ticks = 0;
            track.finished = track.offset == track.smaf.sequenceEvents.size();
            for (SMAFPlayer.Channel chan : track.channels) {
                for (int x = 0; x < chan.notesOn.length; x++) {
                    chan.notesOn[x] = null;
                }
                chan.notesOut.clear();
            }
        }

        // Initialize playback
        finished = false;
        for (SMAFPlayer.Track track : tracks) {
            setupProcess(track);
        }
        finished = true;
        for (SMAFPlayer.Track track : tracks) {
            process(track, 0);
            finished = finished && track.finished;
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    protected boolean sequencerProcess() {
        // Process event ticks
        if (pendingTicks > 0) {

            // Sequencer
            tickNow += pendingTicks;

            // Notes
            for (SMAFPlayer.Track track : tracks) {
                for (SMAFPlayer.Channel chan : track.channels) {
                    for (SMAFPlayer.Note note : chan.notesOut) {
                        note.gateTime -= pendingTicks;
                    }
                }
            }

            // Tracks
            for (SMAFPlayer.Track track : tracks) {
                process(track, pendingTicks);
            }

            // Remove expired notes
            for (SMAFPlayer.Track track : tracks) {
                for (SMAFPlayer.Channel chan : track.channels) {
                    for (int x = 0; x < chan.notesOut.size(); x++) {
                        SMAFPlayer.Note note = chan.notesOut.get(x);
                        if (note.gateTime != 0)
                            continue;
                        sampler.keyOff(note.channel, note.key);
                        chan.notesOut.remove(x--);
                        chan.notesOn[A4 + note.key] = null;
                    }
                }
            }

        }

        // Determine how many ticks and frames can be processed next
        int untilTrack = untilTrack();
        if (untilTrack == -1) {
            finished = true;
            return false;
        }
        int untilNote = untilNote();
        pendingTicks = untilNote == -1 ?
                untilTrack : Math.min(untilTrack, untilNote);
        pendingFrames += (float) Math.floor(pendingTicks * framesPerTick);
        return true;
    }

    private void setupProcess(Track track) {
        // TODO: handle setup sysex events
    }

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
            SMAF.SequenceEvent sequenceEvent = track.smaf.sequenceEvents.get(track.offset);

            // Process the event
            if (sequenceEvent.event instanceof SMAF.NoteEvent) {
                evtNote(track, (SMAF.NoteEvent) sequenceEvent.event);
            } else if (sequenceEvent.event instanceof SMAF.ParamEvent) {
                evtParam(track, (SMAF.ParamEvent) sequenceEvent.event);
            } else {
                setTrackOffset(track, track.offset + 1);
            }

            // Stop processing events
            if (track.finished)
                return;

            // Schedule the next event
            track.ticks = track.smaf.sequenceEvents.get(track.offset).duration * track.smaf.timebaseD;
        }

    }

    private int getVirtualChannelIndex(Track track, int channel) {
        return track.index * 4 + channel;
    }

    // note
    private void evtNote(SMAFPlayer.Track track, SMAF.NoteEvent event) {
        // Common processing
        setTrackOffset(track, track.offset + 1);

        SMAFPlayer.Channel chan = track.channels[event.channel];

        if (event.key < -A4 || event.key >= -A4 + chan.notesOn.length) {
            return;
        }

        SMAFPlayer.Note note = chan.notesOn[A4 + event.key];

        // Raise an event
        raiseKeyEvent(event.key);

        int channel = getVirtualChannelIndex(track, event.channel);

        // Velocity not zero is regarded as key-on
        if (!seeking) {
            sampler.keyOn(channel, event.key, 1.0f);
        }

        // Get or create the note for this key
        if (note == null) {
            note = new SMAFPlayer.Note();
            note.channel = channel;
            note.gateTime = 0;
            note.key = event.key;
            chan.notesOn[A4 + event.key] = note;
            chan.notesOut.add(note);
        }

        // Reconfigure the note
        note.gateTime = event.gateTime * track.smaf.timebaseG;
    }

    // param event
    private void evtParam(SMAFPlayer.Track track, SMAF.ParamEvent e) {
        if (e.eventClass == SMAF.EVENT_CLASS_LONG) {
            switch (e.eventId) {
                case SMAF.EVENT_ID_PROGRAM_CHANGE:
                    sampler.programChange(getVirtualChannelIndex(track, e.channel), e.value);
                    break;
                case SMAF.EVENT_ID_BANK_SELECT: {
                    int ch = getVirtualChannelIndex(track, e.channel);
                    sampler.drumMode(ch, (e.value & 0x80) == 0x80 ? Sampler.DRUM_MODE_SMAF : Sampler.DRUM_MODE_NONE);
                    sampler.bankChange(ch, (e.value & 0x7F) * 2);
                }
                break;
                case SMAF.EVENT_ID_VOLUME:
                    sampler.volume(getVirtualChannelIndex(track, e.channel), e.value / 127.f, true);
                    break;
                case SMAF.EVENT_ID_PAN: {
                    float panValue = e.value < 0x40 ? e.value / 64.f - 1 : (e.value - 64) / 63.f;
                    sampler.panpot(getVirtualChannelIndex(track, e.channel), panValue);
                }
                break;
                case SMAF.EVENT_ID_PITCHBEND: {
                    float bendValue = e.value < 0x40 ? e.value / 64.f - 1 : (e.value - 64) / 63.f;
                    sampler.pitchBend(getVirtualChannelIndex(track, e.channel), bendValue);
                }
                // TODO: modulation and expression
            }
        } else if (e.eventClass == SMAF.EVENT_CLASS_SHORT_EXPRESSION) {
            // TODO: modulation and expression
        } else if (e.eventClass == SMAF.EVENT_CLASS_SHORT_MODULATION) {
            // TODO: modulation and expression
        }
        setTrackOffset(track, track.offset + 1);
    }


    // Specify the event offset of a track
    private void setTrackOffset(SMAFPlayer.Track track, int offset) {
        // Configure the track
        track.offset = offset;
        track.finished = offset >= track.smaf.sequenceEvents.size();

        // Raise an event
        if (!track.finished || !getPlaybackEventsEnabled())
            return;
        boolean finished = true;
        for (SMAFPlayer.Track other : tracks) {
            finished = finished && other.finished;
        }
        if (finished) {
            raisePlaybackEvent(getTime(), EVENT_END, 0);
        }
    }

    // Determine how many ticks can be processed until a note expires
    private int untilNote() {
        int ret = -1;
        for (SMAFPlayer.Track track : tracks) {
            for (SMAFPlayer.Channel chan : track.channels) {
                for (SMAFPlayer.Note note : chan.notesOut) {
                    if (ret == -1 || note.gateTime < ret)
                        ret = note.gateTime;
                }
            }
        }
        return ret;
    }

    // Determine how many ticks can be processed until the next event
    private int untilTrack() {
        int ret = -1;
        for (SMAFPlayer.Track track : tracks) {
            if (track.finished)
                continue;
            if (ret == -1 || track.ticks < ret)
                ret = track.ticks;
        }
        return ret;
    }

}
