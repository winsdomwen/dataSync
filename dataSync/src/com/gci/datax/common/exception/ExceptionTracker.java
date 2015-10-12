package com.gci.datax.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionTracker {
	public static final int STRING_BUFFER = 1024;

	public static String trace(Exception ex) {
		StringWriter sw = new StringWriter(STRING_BUFFER);
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}
}
