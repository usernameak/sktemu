package javax.microedition.rms;

import net.sktemu.ams.AppInstance;
import net.sktemu.ams.skvm.SkvmAppInstance;

public class RecordStore {
    private int storeID;
    private boolean isClosed = false;

    private RecordStore(int storeID) {
        this.storeID = storeID;
    }

    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
            throws RecordStoreException {
        SkvmAppInstance skvmAppInstance = (SkvmAppInstance) AppInstance.appInstance;
        return new RecordStore(
                skvmAppInstance.getRmsManager().getRecordStoreID(recordStoreName, createIfNecessary)
        );
    }

    public static String[] listRecordStores() {
        SkvmAppInstance skvmAppInstance = (SkvmAppInstance) AppInstance.appInstance;
        try {
            return skvmAppInstance.getRmsManager().listRecordStores();
        } catch (RecordStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteRecordStore(String recordStoreName) throws RecordStoreException {
        SkvmAppInstance skvmAppInstance = (SkvmAppInstance) AppInstance.appInstance;
        skvmAppInstance.getRmsManager().deleteRecordStore(recordStoreName);
    }

    public void closeRecordStore() throws RecordStoreException {
        isClosed = true;
    }

    public byte[] getRecord(int recordID) throws RecordStoreException {
        if (isClosed) {
            throw new RecordStoreNotOpenException();
        }

        SkvmAppInstance skvmAppInstance = (SkvmAppInstance) AppInstance.appInstance;
        return skvmAppInstance.getRmsManager().getRecord(storeID, recordID);
    }

    public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreException {
        if (isClosed) {
            throw new RecordStoreNotOpenException();
        }

        SkvmAppInstance skvmAppInstance = (SkvmAppInstance) AppInstance.appInstance;
        return skvmAppInstance.getRmsManager().addRecord(storeID, data, offset, numBytes);
    }
}
