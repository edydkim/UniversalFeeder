package org.ufm.drools;

/**
 * Created by edydkim on 2015/08.
 */
public class AnotherVO implements VO<T> {
    private String ref;
    private String stringValue;
    private boolean booleanValue;
    
    public AnotherVO() {
    }
    
    @Override
    public String getRef() {
        return this.ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public boolean isBooleanValue() {
        return this.booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }
}
