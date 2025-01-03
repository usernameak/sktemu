package javax.microedition.lcdui;

import net.sktemu.debug.FeatureNotImplementedError;

public abstract class Displayable {
    public abstract boolean isShown();

    public void addCommand(Command cmd) {
        // throw new FeatureNotImplementedError("Displayable::addCommand");
    }

    public void removeCommand(Command cmd) {
        // throw new FeatureNotImplementedError("Displayable::removeCommand");
    }

    public void setCommandListener(CommandListener listener) {
        // throw new FeatureNotImplementedError("Displayable::setCommandListener");
    }
}
