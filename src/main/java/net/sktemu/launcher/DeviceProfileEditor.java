package net.sktemu.launcher;

import net.miginfocom.swing.MigLayout;
import net.sktemu.ams.AppDeviceProfile;
import net.sktemu.ams.AppModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class DeviceProfileEditor extends JDialog {
    public DeviceProfileEditor(Frame owner, AppModel appModel) {
        super(owner, "Device profile editor");

        AppDeviceProfile deviceProfile = appModel.getDeviceProfile();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout("", "[][80,grow,fill]", "[][][][nogrid]"));

        JTextField tfScreenWidth = new JTextField(Integer.toString(deviceProfile.getScreenWidth()));
        JTextField tfScreenHeight = new JTextField(Integer.toString(deviceProfile.getScreenHeight()));
        JCheckBox cbSecureUtilWorkaround = new JCheckBox("SecureUtil Workaround", deviceProfile.getSecureUtilWorkaround());

        mainPanel.add(new JLabel("Screen Width:"));
        mainPanel.add(tfScreenWidth, "wrap");
        mainPanel.add(new JLabel("Screen Height:"));
        mainPanel.add(tfScreenHeight, "wrap");
        mainPanel.add(cbSecureUtilWorkaround, "spanx 2,wrap");

        Action okAction = new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    deviceProfile.setScreenWidth(Integer.parseInt(tfScreenWidth.getText()));
                    deviceProfile.setScreenHeight(Integer.parseInt(tfScreenHeight.getText()));
                    deviceProfile.setSecureUtilWorkaround(cbSecureUtilWorkaround.isSelected());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            DeviceProfileEditor.this,
                            "Invalid input",
                            "SKTemu error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                try {
                    deviceProfile.saveDeviceProfile(appModel.getDeviceProfileFile());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            DeviceProfileEditor.this,
                            "Error saving device profile: " + ex.getMessage(),
                            "SKTemu error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                dispose();
            }
        };
        Action cancelAction = new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };

        JButton btnOk = new JButton(okAction);
        JButton btnCancel = new JButton(cancelAction);
        mainPanel.add(btnOk, "tag ok");
        mainPanel.add(btnCancel, "tag cancel");

        setContentPane(mainPanel);
        pack();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                "OK"
        );
        getRootPane().getActionMap().put("OK", okAction);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
                "Cancel"
        );
        getRootPane().getActionMap().put("Cancel", cancelAction);
    }
}
