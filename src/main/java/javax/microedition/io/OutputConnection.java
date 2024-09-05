package javax.microedition.io;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputConnection {
    OutputStream openOutputStream() throws IOException;

    OutputStream openDataOutputStream() throws IOException;
}
