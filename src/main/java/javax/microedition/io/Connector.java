package javax.microedition.io;

import net.sktemu.ams.AppInstance;
import net.sktemu.ams.doja.DojaAppInstance;
import net.sktemu.doja.io.ScratchpadConnection;
import net.sktemu.doja.io.ScratchpadManager;

import java.io.*;

public class Connector {
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int READ_WRITE = READ | WRITE;

    public static Connection open(String name) throws IOException {
        return open(name, READ_WRITE);
    }

    public static Connection open(String name, int mode) throws IOException {
        return open(name, mode, false);
    }

    public static Connection open(String name, int mode, boolean timeouts) throws IOException {
        int schemeColonIndex = name.indexOf(':');
        if (schemeColonIndex == -1) {
            throw new ConnectionNotFoundException("missing URL scheme in " + name);
        }

        int paramsSemicolonIndex = name.indexOf(';', schemeColonIndex + 1);

        String scheme = name.substring(0, schemeColonIndex);
        String target = name.substring(
                schemeColonIndex + 1,
                paramsSemicolonIndex == -1 ? name.length() : paramsSemicolonIndex);
        if (scheme.equals("scratchpad")) {
            if (!(AppInstance.appInstance instanceof DojaAppInstance)) {
                throw new ConnectionNotFoundException("trying to get scratchpad on a non-DoJa app instance");
            }

            int scratchpadIndexOffset = 0;
            for (int i = 0; i < target.length(); i++) {
                if (target.charAt(i) == '/') {
                    scratchpadIndexOffset = i + 1;
                } else {
                    break;
                }
            }

            int scratchpadIndex;
            try {
                scratchpadIndex = Integer.parseInt(target.substring(scratchpadIndexOffset));
            } catch (NumberFormatException e) {
                throw new ConnectionNotFoundException("invalid scratchpad URL: " + name);
            }

            ScratchpadManager scratchpadManager = ((DojaAppInstance) AppInstance.appInstance).getScratchpadManager();

            int pos = 0;
            int length = scratchpadManager.getSize(scratchpadIndex);

            if (paramsSemicolonIndex != -1) {
                String[] params = name.substring(paramsSemicolonIndex + 1).split(",");
                for (int i = 0; i < params.length; i++) {
                    int equateIndex = params[i].indexOf('=');
                    if (equateIndex == -1) {
                        continue;
                    }
                    String key = params[i].substring(0, equateIndex);
                    String value = params[i].substring(equateIndex + 1);

                    if (key.equals("pos")) {
                        try {
                            pos = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            throw new ConnectionNotFoundException("invalid scratchpad position: " + value);
                        }
                    } else if (key.equals("length")) {
                        try {
                            length = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            throw new ConnectionNotFoundException("invalid scratchpad length: " + value);
                        }
                    }
                }
            }

            return new ScratchpadConnection(scratchpadManager, scratchpadIndex, pos, length);
        } else {
            throw new ConnectionNotFoundException("unknown URL scheme in " + name);
        }
    }

    public static DataInputStream openDataInputStream(String name) throws IOException {
        Connection connection = open(name, READ);
        try {
            if (!(connection instanceof InputConnection)) {
                throw new ConnectionNotFoundException();
            }
            InputConnection conn = (InputConnection) connection;
            return conn.openDataInputStream();
        } finally {
            connection.close();
        }
    }

    public static DataOutputStream openDataOutputStream(String name) throws IOException {
        Connection connection = open(name, WRITE);
        try {
            if (!(connection instanceof OutputConnection)) {
                throw new ConnectionNotFoundException();
            }
            OutputConnection conn = (OutputConnection) connection;
            return conn.openDataOutputStream();
        } finally {
            connection.close();
        }
    }

    public static InputStream openInputStream(String name) throws IOException {
        Connection connection = open(name, READ);
        try {
            if (!(connection instanceof InputConnection)) {
                throw new ConnectionNotFoundException();
            }
            InputConnection conn = (InputConnection) connection;
            return conn.openInputStream();
        } finally {
            connection.close();
        }
    }

    public static OutputStream openOutputStream(String name) throws IOException {
        Connection connection = open(name, WRITE);
        try {
            if (!(connection instanceof OutputConnection)) {
                throw new ConnectionNotFoundException();
            }
            OutputConnection conn = (OutputConnection) connection;
            return conn.openOutputStream();
        } finally {
            connection.close();
        }
    }
}
