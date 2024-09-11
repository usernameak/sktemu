package net.sktemu.launcher;

import net.miginfocom.swing.MigLayout;
import net.sktemu.ams.AmsException;
import net.sktemu.ams.AppInstance;
import net.sktemu.ams.AppModel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class LauncherFrame extends JFrame {
    private final AppDatabase appDatabase = new AppDatabase();
    private final AppListModel appListModel = new AppListModel();

    public LauncherFrame() {
        setTitle("SKTemu Launcher");
        setMinimumSize(new Dimension(320, 240));

        JMenuBar menuBar = new JMenuBar();

        JMenu menuApp = new JMenu("File");
        menuApp.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menuApp);

        JMenuItem menuItemInstallApp = new JMenuItem("Install App...", KeyEvent.VK_I);
        menuItemInstallApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
        menuItemInstallApp.addActionListener(ev -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("ZIP archive", "zip");
            fileChooser.setFileFilter(filter);

            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    appListModel.addApp(appDatabase.installAppFromZip(fileChooser.getSelectedFile()));
                } catch (AmsException e) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Error installing app: " + e.getMessage(),
                            "SKTemu error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        menuApp.add(menuItemInstallApp);

        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout("fillx,filly", "[grow,fill]", "[grow,fill]"));

        JList<AppModel> appsList = createAppsList();
        mainPanel.add(appsList);

        setContentPane(mainPanel);
        pack();

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        refreshApps();
    }

    private JList<AppModel> createAppsList() {
        JList<AppModel> appsList = new JList<>();
        appsList.setCellRenderer(new AppListCellRenderer());
        JPopupMenu appPopupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                int row = appsList.locationToIndex(new Point(x, y));
                if (row != -1) {
                    appsList.setSelectedIndex(row);
                }
                super.show(invoker, x, y);
            }
        };

        appsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() != 2) return;

                int index = appsList.locationToIndex(evt.getPoint());
                appsList.setSelectedIndex(index);

                AppModel appModel = appListModel.getElementAt(appsList.getSelectedIndex());
                launchApp(appModel);
            }
        });

        appsList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) return;

                AppModel appModel = appListModel.getElementAt(appsList.getSelectedIndex());
                launchApp(appModel);
            }
        });

        JMenuItem menuItemLaunch = new JMenuItem("Launch", KeyEvent.VK_L);
        menuItemLaunch.addActionListener(actionEvent -> {
            AppModel appModel = appListModel.getElementAt(appsList.getSelectedIndex());
            launchApp(appModel);
        });
        appPopupMenu.add(menuItemLaunch);

        JMenuItem menuItemOptions = new JMenuItem("Options...", KeyEvent.VK_O);
        menuItemOptions.addActionListener(actionEvent -> {
            AppModel appModel = appListModel.getElementAt(appsList.getSelectedIndex());
            new DeviceProfileEditor(this, appModel).setVisible(true);
        });
        appPopupMenu.add(menuItemOptions);

        appsList.setComponentPopupMenu(appPopupMenu);
        appsList.setModel(appListModel);
        return appsList;
    }

    private void launchApp(AppModel appModel) {
        try {
            AppInstance.launchApp(appModel);
        } catch (AmsException | IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Error launching app: " + e.getMessage(),
                    "SKTemu error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        dispose();
    }

    private void refreshApps() {
        try {
            appDatabase.rescanApps();
            appListModel.reloadApps(appDatabase);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error scanning app database: " + e.getMessage(),
                    "SKTemu error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
