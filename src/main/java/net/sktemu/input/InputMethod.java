package net.sktemu.input;

public interface InputMethod {
    void keyPress(IInputTarget target, int key);
    void startNewCharacter(IInputTarget target);
}
