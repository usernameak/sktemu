package com.xce.io;

import net.sktemu.debug.FeatureNotImplementedError;

import java.io.IOException;
import java.io.OutputStream;

public class FileOutputStream extends OutputStream {
    public FileOutputStream(String name) throws IOException {
        System.out.println(name);
    }

    @Override
    public void write(int b) throws IOException {
        throw new FeatureNotImplementedError("FileOutputStream::write");
    }
}
