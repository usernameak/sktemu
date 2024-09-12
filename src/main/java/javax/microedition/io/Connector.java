package javax.microedition.io;

import java.io.*;

public class Connector {
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int READ_WRITE = READ | WRITE;

    public static Connection open(String name) throws IOException {
        // TODO: implement
        throw new ConnectionNotFoundException();
    }

    public static Connection open(String name, int mode) throws IOException {
        // TODO: implement
        throw new ConnectionNotFoundException();
    }

    public static Connection open(String name, int mode, boolean timeouts) throws IOException {
        // TODO: implement
        throw new ConnectionNotFoundException();
    }

    public static DataInputStream openDataInputStream(String name) throws IOException {
        // TODO: implement
        throw new ConnectionNotFoundException();
    }

    public static DataOutputStream openDataOutputStream(String name) throws IOException {
        // TODO: implement
        throw new ConnectionNotFoundException();
    }

    public static InputStream openInputStream(String name) throws IOException {
        // TODO: implement
        throw new ConnectionNotFoundException();
    }

    public static OutputStream openOutputStream(String name) throws IOException {
        // TODO: implement
        throw new ConnectionNotFoundException();
    }
}
