package net.sktemu.utils;

import java.nio.charset.Charset;

public abstract class SharedConstants {
    public static final Charset CP949 = Charset.forName("CP949");
    public static final Charset SHIFT_JIS = Charset.forName("Shift_JIS");

    private SharedConstants() {}
}
