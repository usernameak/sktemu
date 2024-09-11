package net.sktemu.launcher;

import net.sktemu.ams.AppModel;

import javax.swing.*;
import java.util.ArrayList;

public class AppListModel extends AbstractListModel<AppModel> {
    private final ArrayList<AppModel> apps = new ArrayList<>();

    public void reloadApps(AppDatabase database) {
        int oldSize = apps.size();

        apps.clear();
        apps.addAll(database.getApps());

        if (oldSize > 0) {
            fireIntervalRemoved(this, 0, oldSize - 1);
        }
        if (!apps.isEmpty()) {
            fireIntervalAdded(this, 0, apps.size() - 1);
        }
    }

    public void addApp(AppModel appModel) {
        apps.add(appModel);

        fireIntervalAdded(this, apps.size() - 1, apps.size() - 1);
    }

    @Override
    public int getSize() {
        return apps.size();
    }

    @Override
    public AppModel getElementAt(int index) {
        return apps.get(index);
    }
}
