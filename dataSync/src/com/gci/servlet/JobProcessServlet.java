package com.gci.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

import com.gci.Constant;
import com.gci.base.BaseServlet;
import com.gci.datax.common.constants.Constants;
import com.gci.service.SimpleService;
import com.gci.util.http.FileDownloader;
import com.gci.util.http.FileDownloader.Result;
import com.gci.util.http.HttpHelper;
import com.gci.quartz.service.SchedulerService;

/**
 * Servlet implementation class JobProcessServlet
 */
public class JobProcessServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
	private SchedulerService schedulerService;
	private SimpleService simpleService;


	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		schedulerService = (SchedulerService) ctx.getBean("schedulerService");
		simpleService = (SimpleService)ctx.getBean("simpleService");
	}

	/**
	 * 添加Simple Trigger
	 * 
	 * @param request
	 * @param response
	 */
	private void addSimpleTrigger(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 获取界面以p_参数
		Map<String, String> filterMap = WebUtils.getParametersStartingWith(request, "p_");
		if (StringUtils.isEmpty(filterMap.get(Constant.STARTTIME))) {
			response.getWriter().println(1);
		}

		// 添加任务调试
		schedulerService.schedule(filterMap);

		// response.setContentType("text/xml;charset=utf-8");
		response.getWriter().println(0);

	}

	/**
	 * 根据Cron表达式添加Cron Trigger，
	 * 
	 * @param request
	 * @param response
	 */
	private void addCronTriggerByExpression(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 获取界面以参数
		String triggerName = request.getParameter("triggerName");
		String cronExpression = request.getParameter("cronExpression");
		if (StringUtils.isEmpty(triggerName) || StringUtils.isEmpty(cronExpression)) {
			response.getWriter().println(1);
		}

		// 添加任务调试
		schedulerService.schedule(triggerName, cronExpression);

		// response.setContentType("text/xml;charset=utf-8");
		response.getWriter().println(0);

	}

	/**
	 * 根据添加Cron Trigger，
	 * 
	 * @param request
	 * @param response
	 */
	private void addCronTriggerBy(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 获取界面以参数
		String triggerName = request.getParameter("triggerName");
		String val = request.getParameter("val");
		String selType = request.getParameter("selType");
		if (StringUtils.isEmpty(triggerName) || StringUtils.isEmpty(val) || NumberUtils.toLong(val) < 0 || NumberUtils.toLong(val) > 59) {
			response.getWriter().println(1);
		}

		String expression = null;
		if (StringUtils.equals(selType, "second")) {
			// 每多秒执行一次
			expression = "0/" + val + " * * ? * * *";
		} else if (StringUtils.equals(selType, "minute")) {
			// 每多少分执行一次
			expression = "0 0/" + val + " * ? * * *";
		}

		// 添加任务调试
		schedulerService.schedule(triggerName, expression);

		// response.setContentType("text/xml;charset=utf-8");
		response.getWriter().println(0);

	}
	
	public void  stat(HttpServletRequest request,HttpServletResponse response) throws IOException {
		Map<String,String[]> readOnlyMap =   request.getParameterMap();
		Map<String,String> writeAbleMap = new HashMap<String,String>();
		for (Entry<String, String[]> entry : readOnlyMap.entrySet()) {
			writeAbleMap.put(entry.getKey(), entry.getValue()[0]);
		}
		writeAbleMap.remove("action");
		System.out.println(writeAbleMap);		
		response.getWriter().println(schedulerService.addStat(writeAbleMap));
	}

	/**
	 * 取得所有Trigger
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Map<String, Object>> results = this.schedulerService.getQrtzTriggers();
		request.setAttribute("list", results);
		request.setAttribute("num", results.size());
		request.getRequestDispatcher("jsp/list.jsp").forward(request, response);
	}

	/**
	 * 根据名称和组别暂停Tigger
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void pause(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// request.setCharacterEncoding("UTF-8");
		String triggerName = URLDecoder.decode(request.getParameter("triggerName"), "utf-8");
		String group = URLDecoder.decode(request.getParameter("group"), "utf-8");

		schedulerService.pauseTrigger(triggerName, group);
		response.getWriter().println(0);
	}

	/**
	 * 恢复Tigger
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void resume(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// request.setCharacterEncoding("UTF-8");
		String triggerName = URLDecoder.decode(request.getParameter("triggerName"), "utf-8");
		String group = URLDecoder.decode(request.getParameter("group"), "utf-8");

		schedulerService.resumeTrigger(triggerName, group);
		response.getWriter().println(0);
	}
	
	
	/**
	 * 立即执行任务
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String triggerName = URLDecoder.decode(request.getParameter("triggerName"), "utf-8");
		String group = URLDecoder.decode(request.getParameter("group"), "utf-8");
		
		simpleService.testMethod(triggerName, group);
		
	}

	/**
	 * 根据名称和组别删除Tigger
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// request.setCharacterEncoding("UTF-8");
		String triggerName = URLDecoder.decode(request.getParameter("triggerName"), "utf-8");
		String group = URLDecoder.decode(request.getParameter("group"), "utf-8");

		boolean rs = schedulerService.removeTrigdger(triggerName, group);
		if (rs) {
			deleteFile(request,triggerName);
			response.getWriter().println(0);
		} else {
			response.getWriter().println(1);
		}
	}
	
	public void batchDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean result;
		String param = URLDecoder.decode(request.getParameter("param"), "utf-8");
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> list = objectMapper.readValue(param, List.class);
		for (Map<String, Object> map : list) {
			result=schedulerService.removeTrigdger(String.valueOf(map.get("name")), String.valueOf(map.get("group")));
			if (result) {
				deleteFile(request,String.valueOf(map.get("name")));
				
			} else {
				response.getWriter().println(1);
				return;
			}
		}
		response.getWriter().println(0);
	}
	
	public void downloadCsv(HttpServletRequest request,HttpServletResponse response) throws IOException {
		String triggerName = URLDecoder.decode(request.getParameter("triggerName"), "utf-8");
		String csvFilePath=HttpHelper.getRequestRealPath(request, "upload")+File.separator+triggerName.substring(triggerName.indexOf("&")+1);
		//String csvFilePath="upload"+HttpHelper.URL_PATH_SEPARATOR+triggerName.substring(triggerName.indexOf("&")+1);
		// 创建 FileDownloader 对象
        FileDownloader fdl    = new FileDownloader(csvFilePath);
        // 执行下载
        Result result = fdl.download(request, response);
        // 检查下载结果
        if(result != Result.SUCCESS)
        {
        	//
        }
        
	}
	
	public void downloadCsvTemplet(HttpServletRequest request,HttpServletResponse response) throws IOException {
		String csvFilePath=HttpHelper.getRequestRealPath(request, "csv")+File.separator+"oracle_to_oracle.csv";
		// 创建 FileDownloader 对象
        FileDownloader fdl    = new FileDownloader(csvFilePath);
        // 执行下载
        Result result = fdl.download(request, response);
        // 检查下载结果
        if(result != Result.SUCCESS)
        {
        	//
        }
        
	}
	
	public void log(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		String date = request.getParameter("date");
		String jobName=URLDecoder.decode(request.getParameter("job"), "utf-8");
		String logPath=HttpHelper.getRequestRealPath(request, "logs")+File.separator+date+File.separator+jobName+".log";
		BufferedReader reader = null;
		StringBuffer strBuffer=new StringBuffer();
		try {
			File file = new File(logPath);
			if (!file.exists()){
				this.writeText(response, "0");
				return;
			}

			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				strBuffer.append(tempString);
				strBuffer.append("\r\n");
				line++;
			}
			reader.close();
			this.writeText(response, strBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		} 
	}
	
	private void deleteFile(HttpServletRequest request,String fileName){
		try {
			String sysPath=HttpHelper.getRequestRealPath(request, File.separator);
			String xmlFilePath = sysPath + "jobs"+File.separator + fileName + ".xml";
			String csvFilePath = sysPath + "upload"+File.separator+fileName.substring(fileName.indexOf("&")+1);
			File xmlFile = new File(xmlFilePath);
			if (xmlFile.exists()){
				xmlFile.delete();
			}
			File csvFile = new File(csvFilePath);
			if (csvFile.exists()){
				csvFile.delete();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
