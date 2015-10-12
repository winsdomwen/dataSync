package com.gci.quartz.cron.exception;

/**
 * Base exception class
 *
 */
public class BaseException extends RuntimeException {

    private String code;

    private transient String[] values;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public BaseException(String message, Throwable cause, String code,
                         String[] values) {
        super(message, cause);
        this.code = code;
        this.values = values;
    }

}
