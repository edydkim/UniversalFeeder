package org.ufm.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public enum DataSourcePropEnum {
    DRIVER("datasource.driver")
    , URL("datasource.url")
    , USER("datasource.user")
    , PASSWORD("datasource.password")
    ;

    private static final Logger logger = LoggerFactory.getLogger(DataSourcePropEnum.class);

    private String value;

    DataSourcePropEnum(String code) {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            String filename = "datasource.properties";
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
