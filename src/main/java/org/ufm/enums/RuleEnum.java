package org.ufm.enums;

// For multi-rules
public enum RuleEnum {
    AXIAL_FOR_DISPATCHER("basic.drl")
    , AXIAL_FOR_PROCESSOR("transaction.drl");

    private String filename;

    RuleEnum(String filename) {
        this.filename = filename;
    }

    public String filename() {
        return this.filename;
    }
}
