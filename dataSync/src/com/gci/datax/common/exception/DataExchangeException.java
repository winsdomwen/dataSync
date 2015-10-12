package com.gci.datax.common.exception;

/**
 * 在网络不好条件下，意想不到的连接失败等所抛出的一种异常
 * 该异常通常表明它很快就会恢复正常，所以如果抛出该异常，程序将尝试重新工作
 * @see UnRerunableException
 * 
 * */
public class DataExchangeException extends RuntimeException {
	
	private static final long serialVersionUID = -6896389644432598060L;
	
	private String msg;


    public DataExchangeException(final String message) {
        super(message);
    }

    public DataExchangeException(final Exception exception) {
    	super();
    	if (null != exception) {
    		msg = exception.getMessage();
    	}
	} 
    
    public DataExchangeException() {
		super();
	}

	public DataExchangeException(Throwable cause) {
		super(cause);
	}

	public DataExchangeException(final String msg,
			final Throwable cause) {
		super(msg, cause);
	}

	@Override
	public String getMessage() {
		return msg == null ? super.getMessage() : msg;
	}

	public void setMessage(final String message) {
		msg = message;
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
