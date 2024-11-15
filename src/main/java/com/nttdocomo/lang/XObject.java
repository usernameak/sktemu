package com.nttdocomo.lang;

public abstract class XObject {
    public final boolean equals(Object obj) {
        return this == obj;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public final String toString() {
        return super.toString();
    }
}