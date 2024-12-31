package net.sktemu.utils;

import java.nio.charset.Charset;

public abstract class SharedConstants {
    public static final Charset CP949 = Charset.forName("CP949");
    public static final Charset CP932 = Charset.forName("CP943C");

    private SharedConstants() {}
}
