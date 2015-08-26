package org.ufm.enums;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by edydkim on 2015/08.
 */
public enum ExConfPropEnum {
    EXTERNAL_SYSTEM("external.system")
    , QN_EXTERNAL_WORKER("qn.external.worker")
    ;

    private String value;

    ExConfPropEnum(String code) {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            // TODO: define a real file...
            // <- String filename = "extra.properties";
            String filename = "application.properties";
            input = getClass().getClassLoader().getResourceAsStream(filename);
            prop.load(input);

            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                if (key.equals(code)) {
                    value = prop.getProperty(key);
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String value() {
        return this.value;
    }
}
