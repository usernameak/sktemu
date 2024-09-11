package com.xce.io;

import net.sktemu.ams.AppInstance;
import net.sktemu.debug.FeatureNotImplementedError;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class XFile {
    public static final int STDSTREAM = 0;
    public static final int NORMAL = 1;
    public static final int DIRECTORY = 2;
    public static final int FILE_JAR = 3;

    public static final int STDIN = 0;
    public static final int STDOUT = 1;
    public static final int STDERR = 2;

    public static final int SEEK_SET = 0;
    public static final int SEEK_CUR = 1;
    public static final int SEEK_END = 2;

    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int READ_WRITE = 3;
    public static final int READ_DIRECTORY = 4;
    public static final int READ_RESOURCE = 8;

    private final RandomAccessFile raf;

    public XFile(String name, int mode) throws IOException {
        String modeStr;
        if (mode == READ) {
            modeStr = "r";
        } else if (mode == WRITE) {
            modeStr = "rw";
        } else if (mode == READ_WRITE) {
            modeStr = "rw";
        } else {
            throw new IllegalArgumentException("invalid XFile mode " + mode);
        }
        raf = new RandomAccessFile(convertFilePath(name), modeStr);
    }

    public int available() throws IOException {
        return (int) (raf.length() - raf.getFilePointer());
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return raf.read(b, off, len);
    }

    public int write(byte[] b, int off, int len) throws IOException {
        raf.write(b, off, len);
        return len;
    }

    public int seek(int n, int whence) throws IOException {
        if (whence == SEEK_SET) {
            raf.seek(n);
        } else if (whence == SEEK_CUR) {
            raf.seek(raf.getFilePointer() + n);
        } else if (whence == SEEK_END) {
            raf.seek(raf.length() - n);
        }
        return 0;
    }

    public void flush() throws IOException {

    }

    public void close() throws IOException {
        raf.close();
    }

    static File convertFilePath(String path) {
        return new File(AppInstance.appInstance.getAppModel().getDataDir(), path);
    }

    public static boolean exists(String name) throws IOException {
        return convertFilePath(name).exists();
    }

    public static int filesize(String name) throws IOException {
        return (int) convertFilePath(name).length();
    }

    public static int unlink(String name) throws IOException {
        return convertFilePath(name).delete() ? 0 : -1;
    }

    public String readdir() throws IOException {
        throw new FeatureNotImplementedError("readdir");
    }

    public static int fsused() {
        return 0;
    }

    public static int fsavail() {
        return 0x10000;
    }
}
