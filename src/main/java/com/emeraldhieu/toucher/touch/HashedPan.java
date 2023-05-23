package com.emeraldhieu.toucher.touch;

import org.apache.commons.codec.digest.DigestUtils;

public class HashedPan {

    public static String from(String fromPan, String toPan) {
        // Assume combining two PANs by an underscore
        String combined = fromPan + "_" + toPan;
        return DigestUtils.sha256Hex(combined);
    }
}
