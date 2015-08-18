package org.ufm.enums;


public enum Status {
    CONTINUABLE(true),
    FINISHED(false);

    private final boolean continuable;

    private Status(boolean continuable) {
        this.continuable = continuable;
    }

    public static Status continueIf(boolean continuable) {
        return continuable?CONTINUABLE:FINISHED;
    }

    public boolean isContinuable() {
        return this == CONTINUABLE;
    }

    public Status and(boolean value) {
        return value && this.continuable?CONTINUABLE:FINISHED;
    }
}
