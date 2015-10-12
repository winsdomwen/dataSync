package com.gci.util.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gci.util.GeneralHelper;
import com.gci.util.KV;

/***
 * HTTP 帮助类
 * @author hgq
 *
 */
public class HttpHelper {
	/** URL scheme 指示符 */
	public static final String URL_SECHEME_SUFFIX = "://";
	/** URL 目录分隔符 */
	public static final String URL_PATH_SEPARATOR = "/";
	/** URL 端口分隔符 */
	public static final String URL_PORT_SEPARATOR = ":";
	/** HTTP URL 标识 */
	public static final String HTTP_SCHEME = "http";
	/** HTTPS URL 标识 */
	public static final String HTTPS_SCHEME = "https";
	/** HTTP 默认端口 */
	public static final int HTTP_DEFAULT_PORT = 80;
	/** HTTPS 默认端口 */
	public static final int HTTPS_DEFAULT_PORT = 443;
	/** 默认缓冲区大小 */
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	private static ServletContext servletContext;

	/** 获取 {@link ServletContext} */
	public static ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * 初始化 {@link HttpHelper} 的 {@link ServletContext} （只能初始化一次，通常在应用程序启动时执行）
	 * 
	 * @param servletContext
	 *            : 全局 {@link ServletContext} 对象
	 * @throws IllegalStateException
	 *             : 重复初始化时抛出
	 * @throws IllegalArgumentException
	 *             : servletContext 参数为 null 时抛出
	 * 
	 */
	synchronized public final static void initializeServletContext(
			ServletContext servletContext) {
		if (HttpHelper.servletContext != null)
			throw new IllegalStateException(
					"'HttpHelper.servletContext' had been initialized before");
		else if (servletContext == null)
			throw new IllegalArgumentException(
					"param 'servletContext' is null (not valid)");

		HttpHelper.servletContext = servletContext;
	}

	/** 释放 {@link HttpHelper} 的 {@link ServletContext} （通常在应用程序关闭时执行） */
	synchronized public final static void unInitializeServletContext() {
		HttpHelper.servletContext = null;
	}

	/** 获取 {@link HttpURLConnection} */
	public final static HttpURLConnection getHttpConnection(String url,
			KV<String, String>... properties) throws IOException {
		return getHttpConnection(url, null, properties);
	}

	/** 获取 {@link HttpURLConnection} */
	public final static HttpURLConnection getHttpConnection(String url,
			String method, KV<String, String>... properties) throws IOException {
		URL connUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) connUrl.openConnection();

		if (GeneralHelper.isStrNotEmpty(method))
			conn.setRequestMethod(method);

		conn.setDoInput(true);
		conn.setDoOutput(true);

		for (KV<String, String> kv : properties)
			conn.setRequestProperty(kv.getKey(), kv.getValue());

		return conn;
	}

	/** 向页面输出文本内容 */
	public final static void writeString(HttpURLConnection conn,
			String content, String charsetName) throws IOException {
		writeString(conn.getOutputStream(), content, charsetName);
	}

	/** 向页面输出文本内容 */
	public final static void writeString(HttpServletResponse res,
			String content, String charsetName) throws IOException {
		writeString(res.getOutputStream(), content, charsetName);
	}

	/** 向页面输出文本内容 */
	public final static void writeString(OutputStream os, String content,
			String charsetName) throws IOException {
		PrintWriter pw = new PrintWriter(
				new OutputStreamWriter(os, charsetName));

		pw.write(content);
		pw.flush();
		pw.close();
	}

	/** 向页面输出字节内容 */
	public final static void writeBytes(HttpURLConnection conn, byte[] content)
			throws IOException {
		writeBytes(conn.getOutputStream(), content);
	}

	/** 向页面输出字节内容 */
	public final static void writeBytes(HttpServletResponse res, byte[] content)
			throws IOException {
		writeBytes(res.getOutputStream(), content);
	}

	/** 向页面输出字节内容 */
	public final static void writeBytes(OutputStream os, byte[] content)
			throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(os);

		bos.write(content);
		bos.flush();
		bos.close();
	}

	/** 读取页面请求的文本内容 */
	public final static String readString(HttpURLConnection conn,
			boolean escapeReturnChar, String charsetName) throws IOException {
		return readString(conn.getInputStream(), escapeReturnChar, charsetName);
	}

	/** 读取页面请求的字节内容 */
	public final static String readString(HttpServletRequest request,
			boolean escapeReturnChar, String charsetName) throws IOException {
		return readString(request.getInputStream(), escapeReturnChar,
				charsetName);
	}

	/** 读取页面请求的文本内容 */
	public final static String readString(InputStream is,
			boolean escapeReturnChar, String charsetName) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is,
				charsetName));

		try {
			if (escapeReturnChar) {
				for (String line = null; (line = rd.readLine()) != null;)
					sb.append(line);
			} else {
				int count = 0;
				char[] array = new char[DEFAULT_BUFFER_SIZE];

				while ((count = rd.read(array)) != -1)
					sb.append(array, 0, count);
			}
		} finally {
			rd.close();
		}

		return sb.toString();
	}

	/** 读取页面请求的字节内容 */
	public final static byte[] readBytes(HttpURLConnection conn)
			throws IOException {
		return readBytes(conn.getInputStream(), conn.getContentLength());
	}

	/** 读取页面请求的字节内容 */
	public final static byte[] readBytes(HttpServletRequest request)
			throws IOException {
		return readBytes(request.getInputStream(), request.getContentLength());
	}

	/** 读取页面请求的字节内容 */
	public final static byte[] readBytes(InputStream is) throws IOException {
		return readBytes(is, 0);
	}

	/** 读取页面请求的字节内容 */
	public final static byte[] readBytes(InputStream is, int length)
			throws IOException {
		byte[] array = null;

		if (length > 0) {
			array = new byte[length];

			int read = 0;
			int total = 0;

			while ((read = is.read(array, total, array.length - total)) != -1)
				total += read;
		} else {
			List<byte[]> list = new LinkedList<byte[]>();
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

			int read = 0;
			int total = 0;

			for (; (read = is.read(buffer)) != -1; total += read) {
				byte[] e = new byte[read];
				System.arraycopy(buffer, 0, e, 0, read);
				list.add(e);
			}

			array = new byte[total];

			int write = 0;
			for (byte[] e : list) {
				System.arraycopy(e, 0, array, write, e.length);
				write += e.length;
			}
		}

		return array;
	}

	/** 确保 URL 路径的前后存在 URL 路径分隔符 */
	public static final String ensurePath(String path, String defPath) {
		if (GeneralHelper.isStrEmpty(path))
			path = defPath;
		if (!path.startsWith(URL_PATH_SEPARATOR))
			path = URL_PATH_SEPARATOR + path;
		if (!path.endsWith(URL_PATH_SEPARATOR))
			path = path + URL_PATH_SEPARATOR;

		return path;
	}

	/** 获取 {@link HttpServletRequest} 的指定属性值 */
	@SuppressWarnings("unchecked")
	public final static <T> T getRequestAttribute(HttpServletRequest request,
			String name) {
		return (T) request.getAttribute(name);
	}

	/** 设置 {@link HttpServletRequest} 的指定属性值 */
	public final static <T> void setRequestAttribute(
			HttpServletRequest request, String name, T value) {
		request.setAttribute(name, value);
	}

	/** 删除 {@link HttpServletRequest} 的指定属性值 */
	public final static void removeRequestAttribute(HttpServletRequest request,
			String name) {
		request.removeAttribute(name);
	}

	/** 获取 {@link HttpSession} 的指定属性值 */
	@SuppressWarnings("unchecked")
	public final static <T> T getSessionAttribute(HttpSession session,
			String name) {
		return (T) session.getAttribute(name);
	}

	/** 设置 {@link HttpSession} 的指定属性值 */
	public final static <T> void setSessionAttribute(HttpSession session,
			String name, T value) {
		session.setAttribute(name, value);
	}

	/** 删除 {@link HttpSession} 的指定属性值 */
	public final static void removeSessionAttribute(HttpSession session,
			String name) {
		session.removeAttribute(name);
	}

	/** 销毁 {@link HttpSession} */
	public final static void invalidateSession(HttpSession session) {
		session.invalidate();
	}

	/** 获取 {@link ServletContext} 的指定属性值 */
	@SuppressWarnings("unchecked")
	public final static <T> T getApplicationAttribute(String name) {
		return (T) getApplicationAttribute(servletContext, name);
	}

	/** 获取 {@link ServletContext} 的指定属性值 */
	@SuppressWarnings("unchecked")
	public final static <T> T getApplicationAttribute(
			ServletContext servletContext, String name) {
		return (T) servletContext.getAttribute(name);
	}

	/** 设置 {@link ServletContext} 的指定属性值 */
	public final static <T> void setApplicationAttribute(String name, T value) {
		setApplicationAttribute(servletContext, name, value);
	}

	/** 设置 {@link ServletContext} 的指定属性值 */
	public final static <T> void setApplicationAttribute(
			ServletContext servletContext, String name, T value) {
		servletContext.setAttribute(name, value);
	}

	/** 删除 {@link ServletContext} 的指定属性值 */
	public final static void removeApplicationAttribute(String name) {
		removeApplicationAttribute(servletContext, name);
	}

	/** 删除 {@link ServletContext} 的指定属性值 */
	public final static void removeApplicationAttribute(
			ServletContext servletContext, String name) {
		servletContext.removeAttribute(name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，并去除前后空格 */
	public final static String getParam(HttpServletRequest request, String name) {
		String param = getParamNoTrim(request, name);
		if (param != null)
			return param = param.trim();

		return param;
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值 */
	public final static String getParamNoTrim(HttpServletRequest request,
			String name) {
		return request.getParameter(name);
	}

	/** 获取 {@link HttpServletRequest} 的参数名称集合 */
	public final static List<String> getParamNames(HttpServletRequest request) {
		List<String> names = new ArrayList<String>();
		Enumeration<String> en = request.getParameterNames();

		while (en.hasMoreElements())
			names.add(en.nextElement());

		return names;
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值集合 */
	public final static List<String> getParamValues(HttpServletRequest request,
			String name) {
		String[] values = request.getParameterValues(name);
		return values != null ? Arrays.asList(values) : null;
	}

	/** 获取 {@link HttpServletRequest} 的所有参数名称和值 */
	public final static Map<String, String[]> getParamMap(
			HttpServletRequest request) {
		return request.getParameterMap();
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Boolean getBooleanParam(HttpServletRequest request,
			String name) {
		String s = getParam(request, name);
		return GeneralHelper.str2Boolean(s);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final static boolean getBooleanParam(HttpServletRequest request,
			String name, boolean def) {
		String s = getParam(request, name);
		return GeneralHelper.str2Boolean(s, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Byte getByteParam(HttpServletRequest request,
			String name) {
		String s = getParam(request, name);
		return GeneralHelper.str2Byte(s);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final static byte getByteParam(HttpServletRequest request,
			String name, byte def) {
		String s = getParam(request, name);
		return GeneralHelper.str2Byte(s, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Character getCharParam(HttpServletRequest request,
			String name) {
		String s = getParam(request, name);
		return GeneralHelper.str2Char(s);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final static char getCharParam(HttpServletRequest request,
			String name, char def) {
		String s = getParam(request, name);
		return GeneralHelper.str2Char(s, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Short getShortParam(HttpServletRequest request,
			String name) {
		String s = getParam(request, name);
		return GeneralHelper.str2Short(s);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final static short getShortParam(HttpServletRequest request,
			String name, short def) {
		String s = getParam(request, name);
		return GeneralHelper.str2Short(s, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Integer getIntParam(HttpServletRequest request,
			String name) {
		String s = getParam(request, name);
		return GeneralHelper.str2Int(s);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final static int getIntParam(HttpServletRequest request,
			String name, int def) {
		String s = getParam(request, name);
		return GeneralHelper.str2Int(s, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Long getLongParam(HttpServletRequest request,
			String name) {
		String s = getParam(request, name);
		return GeneralHelper.str2Long(s);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final static long getLongParam(HttpServletRequest request,
			String name, long def) {
		String s = getParam(request, name);
		return GeneralHelper.str2Long(s, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Float getFloatParam(HttpServletRequest request,
			String name) {
		String s = getParam(request, name);
		return GeneralHelper.str2Float(s);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final static float getFloatParam(HttpServletRequest request,
			String name, float def) {
		String s = getParam(request, name);
		return GeneralHelper.str2Float(s, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Double getDoubleParam(HttpServletRequest request,
			String name) {
		String s = getParam(request, name);
		return GeneralHelper.str2Double(s);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final static double getDoubleParam(HttpServletRequest request,
			String name, double def) {
		String s = getParam(request, name);
		return GeneralHelper.str2Double(s, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Date getDateParam(HttpServletRequest request,
			String name) {
		String str = getParam(request, name);
		return GeneralHelper.str2Date(str);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final static Date getDateParam(HttpServletRequest request,
			String name, String format) {
		String str = getParam(request, name);
		return GeneralHelper.str2Date(str, format);
	}

	/** 获取 {@link HttpSession} 对象，如果没有则进行创建。 */
	public final static HttpSession getSession(HttpServletRequest request) {
		return getSession(request, true);
	}

	/** 获取 {@link HttpSession} 对象，如果没有则根据参数决定是否创建。 */
	public final static HttpSession getSession(HttpServletRequest request,
			boolean create) {
		return request.getSession(create);
	}

	/** 创建 {@link HttpSession} 对象，如果已存在则返回原对象。 */
	public final static HttpSession createSession(HttpServletRequest request) {
		return getSession(request);
	}

	/** 获取所有 {@link Cookie} */
	public final static Cookie[] getCookies(HttpServletRequest request) {
		return request.getCookies();
	}

	/** 获取指定名称的 {@link Cookie} */
	public final static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(name)) {
					cookie = c;
					break;
				}
			}
		}

		return cookie;
	}

	/** 获取指定名称的 {@link Cookie} 值，失败返回 null */
	public final static String getCookieValue(HttpServletRequest request,
			String name) {
		String value = null;
		Cookie cookie = getCookie(request, name);

		if (cookie != null)
			value = cookie.getValue();

		return value;
	}

	/** 添加 {@link Cookie} */
	public final static void addCookie(HttpServletResponse response,
			Cookie cookie) {
		response.addCookie(cookie);
	}

	/** 添加 {@link Cookie} */
	public final static void addCookie(HttpServletResponse response,
			String name, String value) {
		addCookie(response, new Cookie(name, value));
	}

	/** 获取客户端 {@link Locale} */
	public final static Locale getRequestLocale(HttpServletRequest request) {
		return request.getLocale();
	}

	/** 获取客户端 {@link Locale} 列表 */
	public final static List<Locale> getRequestLocales(
			HttpServletRequest request) {
		return Collections.list(request.getLocales());
	}

	/** 获取 URL 的 BASE 路径 */
	public final static String getRequestBasePath(HttpServletRequest request) {
		String scheme = request.getScheme();
		int serverPort = request.getServerPort();
		StringBuilder sb = new StringBuilder(scheme).append(URL_SECHEME_SUFFIX)
				.append(request.getServerName());

		if (!((scheme.equals(HTTP_SCHEME) && serverPort == HTTP_DEFAULT_PORT) || (scheme
				.equals(HTTPS_SCHEME) && serverPort == HTTPS_DEFAULT_PORT)))
			sb.append(URL_PORT_SEPARATOR).append(request.getServerPort());

		sb.append(request.getContextPath()).append(URL_PATH_SEPARATOR);

		return sb.toString();
	}

	/**
	 * 获取 URL 地址在文件系统的绝对路径,
	 * 
	 * Servlet 2.4 以上通过 request.getServletContext().getRealPath() 获取, Servlet
	 * 2.4 以下通过 request.getRealPath() 获取。
	 * 
	 */
	@SuppressWarnings("deprecation")
	public final static String getRequestRealPath(HttpServletRequest request,
			String path) {
		if (servletContext != null)
			return servletContext.getRealPath(path);
		else {
			try {
				Method m = request.getClass().getMethod("getServletContext");
				ServletContext sc = (ServletContext) m.invoke(request);
				return sc.getRealPath(path);
			} catch (Exception e) {
				return request.getRealPath(path);
			}
		}
	}

	/** 获取发送请求的客户端浏览器所在的操作系统平台 */
	public final static String getRequestUserAgentPlatform(
			HttpServletRequest request) {
		int index = 1;
		String platform = null;
		String agent = request.getHeader("user-agent");

		if (GeneralHelper.isStrNotEmpty(agent)) {
			int i = 0;
			StringTokenizer st = new StringTokenizer(agent, ";");

			while (st.hasMoreTokens()) {
				String token = st.nextToken();

				if (i == 0) {
					if (token.toLowerCase().indexOf("compatible") != -1)
						index = 2;
				} else if (i == index) {
					int sep = token.indexOf(")");

					if (sep != -1)
						token = token.substring(0, sep);

					platform = GeneralHelper.safeTrimString(token);

					break;
				}

				++i;
			}
		}

		return platform;
	}

	/** 设置 HTTP 的 'Content-Type' 响应头 */
	public final static void setContentType(HttpServletResponse response,
			String contentType, String encoding) {
		StringBuilder sb = new StringBuilder(contentType);

		if (encoding != null)
			sb.append(";charset=").append(encoding);

		response.setContentType(sb.toString());
	}

	/** 禁止浏览器缓存当前页面 */
	public final static void setNoCacheHeader(HttpServletResponse response) {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
	}

	/** 检查请求是否来自非 Windows 系统的浏览器 */
	public final static boolean isRequestNotComeFromWidnows(
			HttpServletRequest request) {
		String agent = request.getHeader("user-agent");

		if (GeneralHelper.isStrNotEmpty(agent))
			return agent.toLowerCase().indexOf("windows") == -1;

		return false;
	}
}
