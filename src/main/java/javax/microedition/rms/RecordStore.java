package javax.microedition.rms;

import net.sktemu.ams.AppInstance;

public class RecordStore {
    private int storeID;
    private boolean isClosed = false;

    private RecordStore(int storeID) {
        this.storeID = storeID;
    }

    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
            throws RecordStoreException {
        return new RecordStore(
                AppInstance.appInstance.getRmsManager().getRecordStoreID(recordStoreName, createIfNecessary)
        );
    }

    public static String[] listRecordStores() {
        try {
            return AppInstance.appInstance.getRmsManager().listRecordStores();
        } catch (RecordStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteRecordStore(String recordStoreName) throws RecordStoreException {
        AppInstance.appInstance.getRmsManager().deleteRecordStore(recordStoreName);
    }

    public void closeRecordStore() throws RecordStoreException {
        isClosed = true;
    }

    public byte[] getRecord(int recordID) throws RecordStoreException {
        if (isClosed) {
            throw new RecordStoreNotOpenException();
        }

        throw new RecordStoreException("RecordStore.getRecord() not implemented");
    }
}
