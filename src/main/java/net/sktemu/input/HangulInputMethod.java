package net.sktemu.input;

import javax.microedition.lcdui.Canvas;

public class HangulInputMethod implements InputMethod {
    private char choseong = 0;
    private char jungseong = 0;
    private char jongseong0 = 0;
    private char jongseong1 = 0;

    private static final char[][] choseongTable = new char[][]{
            {'ㄱ', 'ㅋ', 'ㄲ'},
            {'ㄴ', 'ㄹ'},
            {'ㄷ', 'ㅌ', 'ㄸ'},
            {'ㅂ', 'ㅍ', 'ㅃ'},
            {'ㅅ', 'ㅎ', 'ㅆ'},
            {'ㅈ', 'ㅊ', 'ㅉ'},
            {'ㅇ', 'ㅁ'},
    };

    private final char[] resultChars = new char[3];
    private int resultNumChars = 0;

    @Override
    public void keyPress(IInputTarget target, int key) {
        if (key == Canvas.KEY_STAR) {
            startNewCharacter(target);
        }
        int jaeumIndex = getJaeumGroupIndex(key);
        if (jaeumIndex != -1) {
            inputJaeum(target, jaeumIndex);
        } else if (key >= Canvas.KEY_NUM1 && key <= Canvas.KEY_NUM3) {
            inputJungseong(target, key - Canvas.KEY_NUM1);
        } else if (key == Canvas.KEY_CLR) {
            if (jongseong1 != 0) {
                jongseong1 = 0;
                updateCharacter(target);
            } else if (jongseong0 != 0) {
                jongseong0 = 0;
                updateCharacter(target);
            } else if (jungseong != 0) {
                jungseong = 0;
                updateCharacter(target);
            } else if (choseong != 0) {
                choseong = 0;
                updateCharacter(target);
            } else {
                target.discardInputCharacter();
                startNewCharacter(target);
            }
        }
    }

    @Override
    public void startNewCharacter(IInputTarget target) {
        choseong = 0;
        jungseong = 0;
        jongseong0 = 0;
        jongseong1 = 0;
        resultNumChars = 0;
    }

    private void inputJaeum(IInputTarget target, int idx) {
        char ch;
        if (jungseong != 0) {
            if (choseong == 0) {
                startNewCharacter(target);
                ch = choseong;
            } else {
                ch = jongseong0;
            }
        } else {
            ch = choseong;
        }

        if (ch == 0) {
            ch = choseongTable[idx][0];
        } else {
            boolean found = false;
            for (int i = 0; i < choseongTable[idx].length; i++) {
                if (choseongTable[idx][i] == ch) {
                    found = true;
                    if (i + 1 >= choseongTable[idx].length) {
                        ch = choseongTable[idx][0];
                    } else {
                        ch = choseongTable[idx][i + 1];
                    }
                    break;
                }
            }
            if (!found) {
                startNewCharacter(target);
                ch = choseongTable[idx][0];
            }
        }

        if (jungseong != 0) {
            jongseong0 = ch;
        } else {
            choseong = ch;
        }

        updateCharacter(target);
    }

    private void inputJungseong(IInputTarget target, int idx) {
        if (jongseong1 != 0) {
            char oldJongseong1 = jongseong1;
            jongseong1 = 0;
            updateCharacter(target);

            startNewCharacter(target);
            choseong = oldJongseong1;
            updateCharacter(target);
        } else if (jongseong0 != 0) {
            char oldJongseong0 = jongseong0;
            jongseong0 = 0;
            updateCharacter(target);

            startNewCharacter(target);
            choseong = oldJongseong0;
            updateCharacter(target);
        }
        if (idx == 0) {
            if (jungseong == 0) {
                jungseong = 'ㅣ';
            } else if (jungseong == 'ᆞ') {
                jungseong = 'ㅓ';
            } else if (jungseong == '：') {
                jungseong = 'ㅕ';
            } else if (jungseong == 'ㅓ') {
                jungseong = 'ㅔ';
            } else if (jungseong == 'ㅕ') {
                jungseong = 'ㅖ';
            } else if (jungseong == 'ㅏ') {
                jungseong = 'ㅐ';
            } else if (jungseong == 'ㅑ') {
                jungseong = 'ㅒ';
            } else if (jungseong == 'ㅗ') {
                jungseong = 'ㅚ';
            } else if (jungseong == 'ㅘ') {
                jungseong = 'ㅙ';
            } else if (jungseong == 'ㅠ') {
                jungseong = 'ㅝ';
            } else if (jungseong == 'ㅝ') {
                jungseong = 'ㅞ';
            } else if (jungseong == 'ㅜ') {
                jungseong = 'ㅟ';
            } else if (jungseong == 'ㅡ') {
                jungseong = 'ㅢ';
            } else {
                startNewCharacter(target);
                inputJungseong(target, idx);
                return;
            }
        } else if (idx == 1) {
            if (jungseong == 0) {
                jungseong = 'ᆞ';
            } else if (jungseong == 'ᆞ') {
                jungseong = '：';
            } else if (jungseong == '：') {
                jungseong = 'ᆞ';
            } else if (jungseong == 'ㅣ') {
                jungseong = 'ㅏ';
            } else if (jungseong == 'ㅏ') {
                jungseong = 'ㅑ';
            } else if (jungseong == 'ㅑ') {
                jungseong = 'ㅏ';
            } else if (jungseong == 'ㅚ') {
                jungseong = 'ㅘ';
            } else if (jungseong == 'ㅡ') {
                jungseong = 'ㅜ';
            } else if (jungseong == 'ㅜ') {
                jungseong = 'ㅠ';
            } else {
                startNewCharacter(target);
                inputJungseong(target, idx);
                return;
            }
        } else if (idx == 2) {
            if (jungseong == 0) {
                jungseong = 'ㅡ';
            } else if (jungseong == 'ᆞ') {
                jungseong = 'ㅗ';
            } else if (jungseong == '：') {
                jungseong = 'ㅛ';
            } else {
                startNewCharacter(target);
                inputJungseong(target, idx);
                return;
            }
        } else {
            throw new IllegalArgumentException();
        }
        updateCharacter(target);
    }

    private int convertChoseong(char c) {
        // @formatter:off
        switch (c) {
            case 'ㄱ': return 0;
            case 'ㄲ': return 1;
            case 'ㄴ': return 2;
            case 'ㄷ': return 3;
            case 'ㄸ': return 4;
            case 'ㄹ': return 5;
            case 'ㅁ': return 6;
            case 'ㅂ': return 7;
            case 'ㅃ': return 8;
            case 'ㅅ': return 9;
            case 'ㅆ': return 10;
            case 'ㅇ': return 11;
            case 'ㅈ': return 12;
            case 'ㅉ': return 13;
            case 'ㅊ': return 14;
            case 'ㅋ': return 15;
            case 'ㅌ': return 16;
            case 'ㅍ': return 17;
            case 'ㅎ': return 18;
            default: return -1;
        }
        // @formatter:on
    }

    private int convertJongseong(char c) {
        // @formatter:off
        switch (c) {
            case 0: return 0;
            case 'ㄱ': return 1;
            case 'ㄲ': return 2;
            case 'ㄴ': return 4;
            case 'ㄷ': return 7;
            case 'ㄹ': return 8;
            case 'ㅁ': return 16;
            case 'ㅂ': return 17;
            case 'ㅅ': return 19;
            case 'ㅆ': return 20;
            case 'ㅇ': return 21;
            case 'ㅈ': return 22;
            case 'ㅊ': return 23;
            case 'ㅋ': return 24;
            case 'ㅌ': return 25;
            case 'ㅍ': return 26;
            case 'ㅎ': return 27;
            default: return -1;
        }
        // @formatter:on
    }

    private void updateCharacter(IInputTarget target) {
        boolean validJungseong = jungseong >= 0x314F && jungseong <= 0x3163;

        int jongseongIndex = convertJongseong(jongseong0);

        int idx = 0;
        if (!validJungseong && choseong != 0) {
            resultChars[idx++] = choseong;
        }
        if (jungseong != 0) {
            char ch = jungseong;

            if (validJungseong && choseong != 0) {
                int choseongConverted = convertChoseong(choseong);
                int jungseongIndex = jungseong - 0x314F;
                int jongseongIndexTmp = jongseongIndex != -1 ? jongseongIndex : 0;
                ch = (char) (0xAC00 + choseongConverted * 588 + jungseongIndex * 28 + jongseongIndexTmp);
            }

            resultChars[idx++] = ch;
        }

        if (jongseongIndex == -1) {
            resultChars[idx++] = jongseong0;
        }

        for (int i = 0; i < resultNumChars; i++) {
            target.discardInputCharacter();
        }

        resultNumChars = idx;
        for (int i = 0; i < resultNumChars; i++) {
            target.generateInputCharacter(resultChars[i]);
        }
    }

    private int getJaeumGroupIndex(int key) {
        if (key >= Canvas.KEY_NUM4 && key <= Canvas.KEY_NUM9) {
            return key - Canvas.KEY_NUM4;
        } else if (key == Canvas.KEY_NUM0) {
            return 6;
        } else {
            return -1;
        }
    }
}
