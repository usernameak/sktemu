package net.sktemu.input;

public interface IInputTarget {
    void generateInputCharacter(char ch);
    void discardInputCharacter();
}
