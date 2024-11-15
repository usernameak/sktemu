package net.sktemu.doja.io;

import javax.microedition.io.StreamConnection;
import java.io.*;

public class ScratchpadConnection implements StreamConnection {
    private ScratchpadManager scratchpadManager;

    private int index;
    private int pos;
    private int length;

    public ScratchpadConnection(ScratchpadManager scratchpadManager, int index, int pos, int length) {
        this.scratchpadManager = scratchpadManager;

        this.index = index;
        this.pos = pos;
        this.length = length;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return scratchpadManager.getInputStream(index, pos, length);
    }

    @Override
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return null;
    }

    @Override
    public DataOutputStream openDataOutputStream() throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
