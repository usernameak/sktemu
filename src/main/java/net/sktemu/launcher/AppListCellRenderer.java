package net.sktemu.launcher;

import net.sktemu.ams.AppModel;

import javax.swing.*;
import java.awt.*;

public class AppListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (c instanceof JLabel) {
            ((JLabel) c).setText(((AppModel) value).getAppTitle());
        }

        return c;
    }
}
