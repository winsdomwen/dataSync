package com.gci.base;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 *servlet 基础类，用法客户端请求url=xxxx.action?action=login，调用的是其子类的login方法
 *@author hgq
 *
 */
public abstract class BaseServlet extends HttpServlet{

	private static final long serialVersionUID = 4098425118747983442L;

	private static final Logger logger = Logger.getLogger(BaseServlet.class);
	
	public HttpSession session = null;
	/**
	*  转发
	 */
	public void forward(HttpServletRequest request,
			HttpServletResponse response, String url) throws ServletException,
			IOException {
		request.getRequestDispatcher(url).forward(request, response);
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		session=request.getSession();
		try {
			handleRequest(request, response);
		} catch (Exception e) {
			logger.error("请求处理出现异常:", e);
			String cip = "请求地址:" + request.getRemoteAddr();
			request.setAttribute("error", "系统错误(" + cip + "，"
					+ new Date() + ")内部异常: " + e);
			forward(request, response, "/error.jsp");
		}
	}

	/**
	* 处理请求,反射到具体的处理类的函数处理相应的请求
	 */
	protected void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		/*response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);*/
		
		String action = request.getParameter("action");
		session = request.getSession();
		if (action != null) {
			Method m = null;
			m = this.getClass().getDeclaredMethod(action,
					HttpServletRequest.class, HttpServletResponse.class);
			m.invoke(this, request, response);
		} else {
			throw new Exception("method不能为空！");
		}
	}

//	protected void writeJSONObject(HttpServletResponse response,
//			JSONObject jsonObject) throws ServletException, IOException {
//		response.setContentType("application/json;charset=UTF-8");   
//		response.setCharacterEncoding("UTF-8");   
//		response.setHeader("Pragma", "No-cache");   
//		response.setHeader("Cache-Control", "no-cache");   
//		response.setDateHeader("Expires", 0);   
//
//		PrintWriter pw = response.getWriter();
//		pw.write(jsonObject.toString());
//		pw.flush();
//		pw.close();
//	}
//
//	protected void writeJSONArray(HttpServletResponse response,
//			JSONArray jsonArray) throws ServletException, IOException {
//		response.setContentType("application/json;charset=UTF-8");   
//		response.setCharacterEncoding("UTF-8");   
//		response.setHeader("Pragma", "No-cache");   
//		response.setHeader("Cache-Control", "no-cache");   
//		response.setDateHeader("Expires", 0);   
//		PrintWriter pw = response.getWriter();
//		pw.write(jsonArray.toString());
//		pw.flush();
//		pw.close();
//	}

	protected void writeText(HttpServletResponse response,String text) throws IOException
	{
		response.setContentType("text/html;charset=UTF-8");   
		response.setCharacterEncoding("UTF-8");   
		response.setHeader("Pragma", "No-cache");   
		response.setHeader("Cache-Control", "no-cache");   
		response.setDateHeader("Expires", 0);   
		PrintWriter pw = response.getWriter();
		pw.write(text);
		pw.flush();
		pw.close();
	}
}
