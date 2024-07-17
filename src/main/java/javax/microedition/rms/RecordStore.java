package javax.microedition.rms;

import net.sktemu.ams.AppModel;

public class RecordStore {
    private int storeID;

    private RecordStore(int storeID) {
        this.storeID = storeID;
    }

    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
            throws RecordStoreException {
        return new RecordStore(
                AppModel.appModelInstance.getRmsManager().getRecordStoreID(recordStoreName, createIfNecessary)
        );
    }

    public static String[] listRecordStores() {
        try {
            return AppModel.appModelInstance.getRmsManager().listRecordStores();
        } catch (RecordStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteRecordStore(String recordStoreName) throws RecordStoreException {
        AppModel.appModelInstance.getRmsManager().deleteRecordStore(recordStoreName);
    }

    public void closeRecordStore() throws RecordStoreException {
        // do nothing
    }
}
