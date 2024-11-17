package net.sktemu.input;

import javax.microedition.lcdui.Canvas;

public class LatinInputMethod implements InputMethod {
    private static final char[][] charTable = new char[][]{
            {' ', '0'},
            {'.', '!', '?', '1'},
            {'a', 'b', 'c', '2'},
            {'d', 'e', 'f', '3'},
            {'g', 'h', 'i', '4'},
            {'j', 'k', 'l', '5'},
            {'m', 'n', 'o', '6'},
            {'p', 'q', 'r', 's', '7'},
            {'t', 'u', 'v', '8'},
            {'w', 'x', 'y', 'z', '9'},
    };

    private char currentChar;


    @Override
    public void keyPress(IInputTarget target, int key) {
        if (key == Canvas.KEY_STAR) {
            startNewCharacter(target);
        }

        if (key >= Canvas.KEY_NUM0 && key <= Canvas.KEY_NUM9) {
            int idx = key - Canvas.KEY_NUM0;

            if (currentChar == 0) {
                currentChar = charTable[idx][0];
                target.generateInputCharacter(currentChar);
            } else {
                boolean found = false;
                for (int i = 0; i < charTable[idx].length; i++) {
                    if (charTable[idx][i] == currentChar) {
                        found = true;
                        if (i + 1 >= charTable[idx].length) {
                            target.discardInputCharacter();
                            currentChar = charTable[idx][0];
                            target.generateInputCharacter(currentChar);
                        } else {
                            target.discardInputCharacter();
                            currentChar = charTable[idx][i + 1];
                            target.generateInputCharacter(currentChar);
                        }
                        break;
                    }
                }
                if (!found) {
                    startNewCharacter(target);
                    currentChar = charTable[idx][0];
                    target.generateInputCharacter(currentChar);
                }
            }
        } else if (key == Canvas.KEY_CLR) {
            target.discardInputCharacter();
            startNewCharacter(target);
        }
    }

    @Override
    public void startNewCharacter(IInputTarget target) {
        currentChar = 0;
    }
}
