package net.sktemu.ams;

import java.io.IOException;
import java.io.InputStream;

public class FullyInputStream extends InputStream {
    private final InputStream stream;

    public FullyInputStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int numRead = 0;
        while (numRead < len) {
            int curNumRead = stream.read(b, off + numRead, len - numRead);
            if (curNumRead < 0) {
                return numRead == 0 ? -1 : numRead;
            }
            numRead += curNumRead;
        }
        return numRead;
    }

    @Override
    public long skip(long len) throws IOException {
        long numRead = 0;
        while (numRead < len) {
            long curNumRead = stream.skip(len - numRead);
            if (curNumRead <= 0) {
                return numRead;
            }
            numRead += curNumRead;
        }
        return numRead;
    }

    @Override
    public int available() throws IOException {
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        stream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        stream.reset();
    }

    @Override
    public boolean markSupported() {
        return stream.markSupported();
    }
}
