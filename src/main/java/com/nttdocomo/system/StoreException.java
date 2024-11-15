package com.nttdocomo.system;

public class StoreException extends Exception {
    public static final int UNDEFINED = 0;
    public static final int STORE_FULL = 1;
    public static final int NOT_FOUND = 2;

    private final int status;

    public StoreException() {
        this.status = UNDEFINED;
    }

    public StoreException(int status) {
        this.status = status;
    }

    public StoreException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
 