package net.sktemu.doja.io;

import net.sktemu.ams.AmsException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ScratchpadManager {
    private byte[][] scratchpads;

    public void initializeScratchpad(String spSizeValue) throws AmsException {
        if (spSizeValue == null) {
            return;
        }

        String[] spSizes = spSizeValue.split(",");

        try {
            scratchpads = new byte[spSizes.length][];
            for (int i = 0; i < spSizes.length; i++) {
                scratchpads[i] = new byte[Integer.parseInt(spSizes[i])];
            }
        } catch (NumberFormatException e) {
            throw new AmsException("Invalid SPsize: " + spSizeValue);
        }
    }

    public InputStream getInputStream(int index, int pos, int length) throws IOException {
        return new ByteArrayInputStream(scratchpads[index], pos, length);
    }

    public int getSize(int index) {
        if (index >= scratchpads.length) {
            return 0;
        }
        return scratchpads[index].length;
    }
}
