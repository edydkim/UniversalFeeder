package org.ufm.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Cryptor {
    private static final Logger logger = LoggerFactory.getLogger(Cryptor.class);

    private static Base64 base64 = new Base64();

    public Cryptor() {
    }

    public static String encrypt(String decoded) {
        return new String(base64.encode(decoded.getBytes()));
    }

    public static String decrypt(String encoded) {
        return new String(base64.decode(encoded.getBytes()));
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.error("Required plain text as a program argument..");
            return;
        }

        String decoded = args[0];

        String encoded = Cryptor.encrypt(decoded);
        logger.info(encoded);

        decoded = Cryptor.decrypt(encoded);
        logger.info(decoded);
    }
}
