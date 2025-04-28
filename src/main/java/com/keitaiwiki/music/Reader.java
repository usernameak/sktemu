package com.keitaiwiki.music;

// Utility class for reading binary data
class Reader {
    byte[] data;   // Backing data store
    int    length; // Length of current segment
    int    offset; // Current input offset
    int    start;  // Offset of start of current segment

    // Constructor
    Reader(byte[] data, int start, int length) {
        this.data   = data;
        this.length = length;
        offset      = start;
        this.start  = start;
    }

    // Read a byte array
    byte[] bytes(int length) {
        if (offset + length > start + this.length)
            throw new RuntimeException("Unexpected EOF.");
        byte[] ret = new byte[length];
        System.arraycopy(data, offset, ret, 0, length);
        offset += length;
        return ret;
    }

    // Determine whether the stream has reached its end
    boolean isEOF() {
        return offset == start + length;
    }

    // Produce a new Reader to access a subset of this one
    Reader reader(int length) {
        Reader ret = new Reader(data, offset, length);
        skip(length);
        return ret;
    }

    // Advance the input
    void skip(int length) {
        if (offset + length > start + this.length)
            throw new RuntimeException("Unexpected EOF.");
        offset += length;
    }

    // Read an 8-bit unsigned integer
    int u8() {
        if (offset == start + length)
            throw new RuntimeException("Unexpected EOF.");
        return data[offset++] & 0xFF;
    }

    // Read a 16-bit unsigned integer
    int u16() {
        int ret = u8() << 8;
        return ret | u8();
    }

    // Read a 32-bit unsigned integer
    int u32() {
        int ret = u16() << 16;
        if (ret < 0)
            throw new RuntimeException("Unsupported U32 value.");
        return ret | u16();
    }

}
