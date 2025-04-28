package com.keitaiwiki.music;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SMAF {
    private static final int FOURCC_MMMD = 0x4D4D4D44; // "MMMD"
    private static final int FOURCC_CNTI = 0x434E5449; // "CNTI"
    private static final int FOURCC_MTR = 0x4D545200; // "MTR\0"
    private static final int FOURCC_MsqI = 0x4D737149; // "MsqI"
    private static final int FOURCC_Mtsu = 0x4D747375; // "Mtsu"
    private static final int FOURCC_Mtsq = 0x4D747371; // "Mtsq"

    public static final byte EVENT_CLASS_SHORT_EXPRESSION = 0;
    public static final byte EVENT_CLASS_SHORT_MODULATION = 2;
    public static final byte EVENT_CLASS_LONG = 3;

    private static final byte EVENT_CLASS_SHORT_PITCHBEND = 1;

    public static final byte EVENT_ID_PROGRAM_CHANGE = 0;
    public static final byte EVENT_ID_BANK_SELECT = 1;
    public static final byte EVENT_ID_OCTAVE_SHIFT = 2;
    public static final byte EVENT_ID_MODULATION = 3;
    public static final byte EVENT_ID_PITCHBEND = 4;
    public static final byte EVENT_ID_VOLUME = 7;
    public static final byte EVENT_ID_PAN = 10;
    public static final byte EVENT_ID_EXPRESSION = 11;

    // Header subchunks
    int contentsClass;
    int contentsType;
    int contentsCodeType;
    int copyStatus;
    int copyCounts;
    String contentsTags;

    ArrayList<Track> tracks = new ArrayList<>();

    abstract static class Event {

    }

    static class NoteEvent extends Event {
        byte channel;
        int key;
        int gateTime;
    }

    static class NoOpEvent extends Event {
    }

    static class ExclusiveEvent extends Event {
        int maker;
        int format;
        byte[] data;
    }

    static class ParamEvent extends Event {
        byte channel;
        byte eventClass;
        byte eventId;
        int value;
    }

    static class EndOfSequenceEvent extends Event {
    }

    static class SequenceEvent {
        int duration;
        Event event;
    }

    class Track {
        int sequenceType;

        // timebases are in milliseconds
        int timebaseD;
        int timebaseG;

        int channelStatus;

        private int tmpOctaveShift = 0;

        int index = 0;
        ArrayList<Event> setupEvents = new ArrayList<>();
        ArrayList<SequenceEvent> sequenceEvents = new ArrayList<>();

    }

    ////////////////////////////// Constructors ///////////////////////////////

    /**
     * Decode from a byte array. Same as invoking
     * {@code SMAF(data, 0, data.length)}.
     *
     * @param data A byte array contining the SMAF resource.
     * @throws NullPointerException if {@code data} is {@code null}.
     * @throws FormatException      if an error occurs during decoding.
     * @see SMAF(byte[],int,int)
     */
    public SMAF(byte[] data) throws IOException, FormatException {
        this(data, 0, data.length);
    }

    /**
     * Decode from a byte array. If the {@code length} argument specifies bytes
     * beyond the end of the SMAF resource, the extra bytes will not be
     * processed.
     *
     * @param data   A byte array contining the SMAF resource.
     * @param offset The position in {@code data} of the first byte of the SMAF
     *               resource.
     * @param length The number of bytes to consider when decoding the SMAF
     *               resource. Must be greater than or equal to the size of the SMAF.
     * @throws NullPointerException           if {@code data} is {@code null}.
     * @throws IllegalArgumentException       if {@code length} is negative.
     * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative
     *                                        or {@code offset + length > data.length}.
     * @throws FormatException                if an error occurs during decoding.
     */
    public SMAF(byte[] data, int offset, int length) throws IOException, FormatException {

        // Error checking
        if (data == null)
            throw new NullPointerException("A byte buffer is required.");
        if (length < 0)
            throw new IllegalArgumentException("Invalid length.");
        if (offset < 0 || length >= 0 && offset + length > data.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "Invalid range in byte buffer.");
        }

        // Parse the data
        try (ByteArrayInputStream stream =
                     new ByteArrayInputStream(data, offset, length)) {
            parse(new DataInputStream(stream));
        }
    }

    /**
     * Decode from an input stream. The data at the current position in the
     * stream must be an SMAF resource.<br><br>
     * After returning, the stream will be at the position of the byte
     * following the SMAF data. If an error occurs during decoding, the stream
     * position will be indeterminate.
     *
     * @param in The stream to decode from.
     * @throws FormatException if an error occurs during decoding.
     * @throws IOException     if a stream access error occurs.
     */
    public SMAF(InputStream in) throws IOException, FormatException {
        parse(in instanceof DataInputStream ?
                (DataInputStream) in : new DataInputStream(in));
    }

    /**
     * Decode from an {@code Path}. The data at the start of the referenced
     * file must be an SMAF resource.
     *
     * @param path The path to decode from.
     * @throws FormatException if an error occurs during decoding.
     * @throws IOException     if a path access error occurs.
     */
    public SMAF(Path path) throws IOException, FormatException {
        parse(new DataInputStream(Files.newInputStream(path)));
    }

    // Parse a SMAF file
    private void parse(DataInputStream stream) throws IOException, FormatException {

        // File signature
        if (stream.readInt() != FOURCC_MMMD)
            throw new FormatException("Missing \"MMMD\" signature.");

        // File length
        int length = stream.readInt();
        if (length < 0)
            throw new FormatException("Unsupported file length.");

        // Read the file into a byte array
        byte[] data = new byte[8 + length];
        int offset = 8;
        while (offset < data.length) {
            int readed = stream.read(data, offset, data.length - offset);
            if (readed == -1)
                throw new FormatException("Unexpected EOF.");
            offset += readed;
        }

        // Working variables
        // TODO: last 2 bytes are CRC16, check it.
        Reader reader = new Reader(data, 8, length - 2);

        // Parse the file
        while (!reader.isEOF()) {
            int id = reader.u32();
            Reader chunk = reader.reader(reader.u32());
            if (id == FOURCC_CNTI) {
                headerCNTI(chunk);
            } else if ((id & 0xFFFFFF00) == FOURCC_MTR) {
                tracks.add(track(id & 0xFF, chunk));
            }
        }

        // Measure the duration and tick counters
        // inspect();
    }

    // Parse a track
    private SMAF.Track track(int index, Reader reader) throws FormatException {
        // Working variables
        SMAF.Track ret = new SMAF.Track();
        ret.index = index;

        int formatType = reader.u8();
        if (formatType != 0) {
            throw new FormatException("Only Handy-Phone Standard MMF files are currently supported.");
        }

        ret.tmpOctaveShift = 0;
        ret.sequenceType = reader.u8();
        ret.timebaseD = convertTimebase(reader.u8());
        ret.timebaseG = convertTimebase(reader.u8());

        ret.channelStatus = reader.u16();

        while (!reader.isEOF()) {
            int id = reader.u32();
            Reader chunk = reader.reader(reader.u32());

            // TODO: MsqI for cuepoints
            if (id == FOURCC_Mtsu) {
                while (!chunk.isEOF()) {
                    ret.setupEvents.add(event(chunk, ret));
                }
            } else if (id == FOURCC_Mtsq) {
                while (!chunk.isEOF()) {
                    ret.sequenceEvents.add(sequenceEvent(chunk, ret));
                }
            }
        }
        return ret;
    }

    private static final int[] TIMEBASE_TABLE = {1, 2, 4, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 20, 40, 50};

    private int convertTimebase(int timebase) throws FormatException {
        if (timebase > TIMEBASE_TABLE.length || TIMEBASE_TABLE[timebase] == 0) {
            throw new FormatException("Invalid timebase value");
        }
        return TIMEBASE_TABLE[timebase];
    }

    ///////////////////////// Header Parsing Methods //////////////////////////

    // Parse a header "CNTI" subchunk
    private void headerCNTI(Reader reader) {
        contentsClass = reader.u8();
        contentsType = reader.u8();
        contentsCodeType = reader.u8();
        copyStatus = reader.u8();
        copyCounts = reader.u8();
        contentsTags = new String(reader.bytes(reader.length - reader.offset), StandardCharsets.ISO_8859_1);
    }

    ////////////////////////// Event Parsing Methods //////////////////////////

    // Parse an event
    private Event event(Reader reader, Track track) throws FormatException {
        int firstByte = reader.u8();
        if (firstByte == 0xFF) {
            int begin = reader.u8();
            if (begin == 0) {
                // no-op event
                return new NoOpEvent();
            } else if (begin == 0xF0) {
                int eventSize = reader.u8();

                ExclusiveEvent event = new ExclusiveEvent();
                event.maker = reader.u8();
                event.format = reader.u8();
                event.data = reader.bytes(eventSize - 3);

                int end = reader.u8();
                if (end != 0xF7) {
                    throw new FormatException("Wrong beginning byte for ExclusiveEvent.");
                }

                return event;
            } else {
                throw new FormatException("Wrong beginning byte for ExclusiveEvent.");
            }
        } else if (firstByte == 0) {
            int secondByte = reader.u8();
            if (secondByte == 0) {
                if (reader.u8() == 0) {
                    return new EndOfSequenceEvent();
                } else {
                    throw new FormatException("Wrong byte for EndOfSequenceEvent.");
                }
            }

            byte channel = (byte) (secondByte >> 6);
            byte eventClass = (byte) ((secondByte >> 4) & 0x3);
            byte eventId = (byte) (secondByte & 0xF);
            if (eventClass == 3) {
                ParamEvent event = new ParamEvent();
                event.channel = channel;
                event.eventClass = eventClass;
                event.eventId = eventId;
                event.value = reader.u8();

                if (eventId == 2) {
                    // octave shift
                    if (event.value < 0x80) {
                        track.tmpOctaveShift = event.value;
                    } else {
                        track.tmpOctaveShift = -(event.value - 0x80);
                    }
                }

                return event;
            } else {
                if (eventClass == EVENT_CLASS_SHORT_PITCHBEND) {
                    ParamEvent event = new ParamEvent();

                    event.channel = channel;
                    event.eventClass = EVENT_CLASS_LONG;
                    event.eventId = EVENT_ID_PITCHBEND;
                    event.value = eventId * 8;

                    return event;
                }

                ParamEvent event = new ParamEvent();
                event.channel = channel;
                event.eventClass = eventClass;
                event.eventId = 0;
                event.value = eventId;

                return event;
            }
        } else {
            NoteEvent noteEvent = new NoteEvent();
            noteEvent.channel = (byte) (firstByte >> 6);

            byte octave = (byte) ((firstByte >> 4) & 0x3);
            byte noteNumber = (byte) (firstByte & 0xF);

            noteEvent.key = noteNumber - 0x9; // A is 0x9
            noteEvent.key += ((octave + track.tmpOctaveShift) - 0x2) * 12; // octave 4 is 0x2


            noteEvent.gateTime = decodeVarInt(reader);

            return noteEvent;
        }
    }

    private SequenceEvent sequenceEvent(Reader reader, Track track) throws FormatException {
        SequenceEvent event = new SequenceEvent();
        event.duration = decodeVarInt(reader);
        event.event = event(reader, track);
        return event;
    }

    private int decodeVarInt(Reader reader) {
        int firstByte = reader.u8();
        if ((firstByte & 0x80) == 0) {
            return firstByte;
        } else {
            int secondByte = reader.u8();
            return (((firstByte & 0x7F) << 7) | secondByte) + 128;
        }
    }

}
