package com.gci.quartz.cron.exception;


public class ExpressionException extends BaseException {

    public ExpressionException(String message, Throwable cause, String code,
                               String[] values) {
        super(message, cause, code, values);
    }

    public ExpressionException(Throwable throwable) {
        super(null, throwable, null, null);
    }

    public ExpressionException(String string, Throwable throwable) {
        super(string, throwable, null, null);
    }

    public ExpressionException(String string) {
        super(string, null, null, null);
    }
}
