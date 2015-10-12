package com.gci.quartz.cron.util;

public class Util {

    private Util() {
    }

    public static String array2String(Object[] array) {

        if (array == null) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, len = array.length; i < len; i++) {
            if (i < (len - 1))
                buffer.append(array[i].toString().concat(","));
            else
                buffer.append(array[i].toString());
        }
        return buffer.toString();
    }
}
