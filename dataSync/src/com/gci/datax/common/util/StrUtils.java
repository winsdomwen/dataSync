package com.gci.datax.common.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gci.datax.common.exception.DataExchangeException;

/**
 * 字符串处理工具类. 
 * 
 *@see ArrayUtils
 *
 */
public class StrUtils {
	private static final Logger log = Logger.getLogger(StrUtils.class);

	private static final Pattern MAIL_PATTERN = Pattern
			.compile("(?i)^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");

	private static final Pattern VARIABLE_PATTERN = Pattern
			.compile("(\\$)\\{?(\\w+)\\}?");

	private static String SYSTEM_ENCODING = System.getProperty("file.encoding");

	static {
		if (SYSTEM_ENCODING == null) {
			SYSTEM_ENCODING = "UTF-8";
		}
	}

	private StrUtils() {
	}
	/**
	 * 转换日期字符串为指定格式
	 * @param	dateString
	 *            
	 * @param	srcFormat
	 *            
	 * @param	destFormat
	 *            
	 * @return 
	 * 
	 */
	public static String changeDateFormat(String dateString, String srcFormat,
			String destFormat) {
		if (srcFormat == null || destFormat == null || srcFormat.isEmpty()
				|| destFormat.isEmpty()) {
			return "";
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(srcFormat);

		if (dateString == null || dateString.isEmpty()) {
			return "";
		}

		java.util.Date dateVal = dateFormat.parse(dateString,
				new ParsePosition(0));

		dateFormat = new SimpleDateFormat(destFormat);

		return dateFormat.format(dateVal);
	}

	public static String convertTimeStampToDate(String dateString,
			String srcFormat, String destFormat) {
		if (srcFormat == null || destFormat == null || srcFormat.isEmpty()
				|| destFormat.isEmpty()) {
			return "";
		}

		if (dateString == null || dateString.isEmpty()) {
			return "";
		}
		String[] tmpDate = dateString.split(" ");
		return changeDateFormat(tmpDate[0], srcFormat, destFormat);
	}

	public static String changeDateFormat(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        return dateFormat.format(date);
	}
	
	public static String replaceChars(String old, char[] rchars) {
		if (null == rchars)
			return old;
		
		int oldLen = old.length();
		int rLen = rchars.length;
		
		StringBuilder sb = new StringBuilder(oldLen);
		char[] oldArrays = old.toCharArray();
		boolean found;
		char c1;
		
		for (int i = 0; i < oldLen; i++) {
			found = false;
			c1 = oldArrays[i];
			for (int j = 0; j < rLen; j += 2) {
				if (c1 == rchars[j]) {
					if (rchars[j + 1] != 0) {
						sb.append(rchars[j + 1]);
					}
					found = true;
				}
			}
			if (!found) {
				sb.append(c1);
			}
		}
		
		return sb.toString();
	}

	public static char changeChar(String str) {
		char out = '\001';
		if (str != null) {
			if (str.equals("\\t"))
				out = '\t';
			else if (str.equals("\\n"))
				out = '\n';
			else if (str.equals("\\001"))
				out = '\001';
			else if (str.equals("\\009"))
				out = '\t';
			else {
				char[] ch = str.toCharArray();
				if (ch.length == 1)
					out = ch[0];
				else if (ch.length > 1) {
					if (str.indexOf("\\u") > -1 && str.length() == 6) {
						try {
							out = (char) Integer.valueOf(str.substring(2))
									.intValue();
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException(e);
						}
					} else {
						throw new IllegalArgumentException(String.format(
								"Cannot convert literal %s to char type", str));
					}
				}
			}
		}
		return out;
	}

	public static String replaceString(String param) {
		Matcher matcher = VARIABLE_PATTERN.matcher(param);
		String param1 = param;
		List<String> re = new ArrayList<String>();
		int i = 0;
		while (matcher.find()) {
			param1 = StringUtils.replace(param1, matcher.group(),
					System.getProperty(matcher.group(2), matcher.group()));
			if (param1.equals(param)) {
				i++;
				param1 = StringUtils.replace(param1, matcher.group(),
						"@replace" + i);
				re.add(matcher.group());
			}
			log.debug(param1);
			param = param1;
			matcher = VARIABLE_PATTERN.matcher(param1);
		}
		for (; i > 0; i--) {
			param1 = StringUtils.replace(param1, "@replace" + i, re.get(i - 1));
		}
		log.debug(param1);
		return param1;
	}

	public static int getIntParam(String param, int defaultvalue) {
		return getIntParam(param, defaultvalue, Integer.MIN_VALUE,
				Integer.MAX_VALUE);
	}

	public static int getIntParam(String param, int defaultvalue, int min,
			int max) {
		if (param != null) {
			try {
				Integer value = Integer.valueOf(param);
				if (value < min || value > max) {
					throw new DataExchangeException(String.format(
							"the [%s]'value is out of range{min=%d,max=%d}",
							param, min, max));
				} else
					return value;
			} catch (NumberFormatException e) {
				log.error(String
						.format("converting [%s]'value to numeric types failed,so use the default value[%d].",
								param, defaultvalue));
				return defaultvalue;
			}
		} else
			return defaultvalue;
	}

	public static String removeSpace(final String str, final String sep) {
		assert str != null;
		assert sep != null;

		StringBuilder sb = new StringBuilder(str.length());

		String[] items = str.trim().split(sep);
		for (String item : items) {
			sb.append(item.trim()).append(sep);
		}

		return sb.substring(0, sb.lastIndexOf(sep));
	}

//	public static void main(String[] args) {
//		System.setProperty("PATH", "UNIX");
//		System.out.println(replaceString("${PATH}"));
//
//		System.out.println("abcd\001a asdfsd\001");
//		System.out.println(replaceChars("abcd\001a asdfsd\001", new char[] {'\n', ' ', '\001', 0}));
//		return;
//	}

}
