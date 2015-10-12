package com.gci.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.gci.base.BaseServlet;
import com.gci.datax.engine.tools.JobConfGenDriver;
import com.gci.util.csvhelper.CsvReader;
import com.gci.util.http.FileUploader;
import com.gci.util.http.FileUploader.FileInfo;
import com.gci.util.http.FileUploader.Result;
import com.gci.util.http.HttpHelper;
import com.gci.quartz.service.SchedulerService;

@SuppressWarnings("serial")
public class FileUploadServlet extends BaseServlet {
	// 上传路径
	private static final String UPLOAD_PATH = "upload";
	// 可接受的文件类型
	private static final String[] ACCEPT_TYPES = { "csv", "xml" };
	// 总上传文件大小限制
	private static final long MAX_SIZE = 1024 * 1024 * 100;
	// 单个传文件大小限制
	private static final long MAX_FILE_SIZE = 1024 * 1024 * 10;

	private ApplicationContext ctx;
	private SchedulerService schedulerService;
	
	public void init(ServletConfig config) throws ServletException {
		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		schedulerService = (SchedulerService) ctx.getBean("schedulerService");
	}

	public void uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// 创建 FileUploader 对象
		FileUploader fu = new FileUploader(UPLOAD_PATH, ACCEPT_TYPES, MAX_SIZE,
				MAX_FILE_SIZE);
		// 执行上传并获取操作结果
		Result result = fu.upload(request, response);

		// 检查操作结果
		if (result != FileUploader.Result.SUCCESS) {
			this.writeText(response, "上传失败");
			return;
		}

		// 获取上传文件信息
		Map<String, FileInfo[]> map = fu.getFileFields();
		FileInfo[] files = map.get("fileCsv");
		FileInfo finleInfo = files[0];
		// String fname=finleInfo.getUploadFileName();
		//String fname2 = finleInfo.getUploadFileSimpleName();
		String filePath = finleInfo.getSaveFile().getPath();
		// 读取csv文件
		readCsv(request,filePath);

		//this.writeText(response, "上传成功");
		response.sendRedirect(request.getHeader("Referer"));
	}

	private void readCsv(HttpServletRequest request,String filePath) throws Exception {
		CsvReader reader = null;
		Map<String, String> csvMap=new HashMap<String, String>();

		try {
			reader = new CsvReader(filePath,',',Charset.forName("GBK"));

			// 第一行
			if (reader.readHeaders()) {
				csvMap.put(reader.getHeader(0), reader.getHeader(1));
			}
			Map<String, String> readerMap = new LinkedHashMap<String, String>();
			Map<String, String> writerMap = new LinkedHashMap<String, String>();
			ArrayList<String> sourceFields=new ArrayList<String>();
			ArrayList<String> targetFields=new ArrayList<String>();
			while (reader.readRecord()) {
				if ("column".equals(reader.get(0).trim()) && !"".equals(reader.get(1))){ //列名
					sourceFields.add(reader.get(1));
					targetFields.add(reader.get(2));
				}
				else if (reader.get(1).trim()!="" && reader.get(2).trim()!=""){ //数据源的配置信息
					System.out.println(reader.get(0) + " : " + reader.get(1) + " _ > " + reader.get(2));
					readerMap.put(reader.get(0), reader.get(1));
					writerMap.put(reader.get(0), reader.get(2));
				}
				else {//其它信息
					csvMap.put(reader.get(0), reader.get(1));
				}
			}
			readerMap.put("sql", csvMap.get("sourcedb_select_sql"));
			readerMap.put("schema", readerMap.get("username"));
			//readerMap.put("tables", "");
			String strCols=sourceFields.toString();
			readerMap.put("columns", strCols.substring(1,strCols.length()-1));
			//readerMap.put("where", "");
			//readerMap.put("tnsfile", "");
			readerMap.put("encoding", "UTF-8");
			readerMap.put("split_mod", "0");
			readerMap.put("concurrency", "1");
			readerMap.put("racurl", csvMap.get("sourcedb_rac_url")==null ? "":csvMap.get("sourcedb_rac_url"));
			readerMap.remove("field");
			
			writerMap.put("insert", csvMap.get("targetdb_insert_sql"));
			writerMap.put("pre", csvMap.get("targetdb_before_sql"));
			writerMap.put("post", csvMap.get("targetdb_after_sql"));
			writerMap.put("schema", writerMap.get("username"));
			writerMap.put("encoding", "UTF-8");
			writerMap.put("limit", "1000");
			writerMap.put("dtfmt", "yyyy-mm-dd hh24:mi:ss");
			writerMap.put("concurrency", csvMap.get("concurrency")==null ? "10": csvMap.get("concurrency"));
			writerMap.put("racurl", csvMap.get("targetdb_rac_url")==null ? "":csvMap.get("targetdb_rac_url"));
			writerMap.put("commitCount", "50000");
			writerMap.remove("field");
			//writerMap.put("duplicatedThreshold", "10000");
			//writerMap.put("duplicatedKeyIndices", "0");
			writerMap.put("table", csvMap.containsKey("table") ? csvMap.get("table"):"?");
			String strTcols=targetFields.toString();
			writerMap.put("colorder", strTcols.substring(1,strTcols.length()-1));
			//writerMap.put("onDuplicatedSql", "delete from TB_NAME_HERE where KEY_COL_NAME_HERE=?");
			
			// 添加任务调试
			String csvFileName=filePath.substring(filePath.lastIndexOf(File.separator)+1);
			String jobName=csvMap.get("job_name").replace("&", "")+"&"+csvFileName;
		    String fileName=schedulerService.schedule(jobName, rtnCronExp(csvMap.get("job_executetime")));
			String jobPath=HttpHelper.getRequestRealPath(request, "jobs")+File.separator+fileName+".xml";
			//生成xml配置文件
			JobConfGenDriver.produceXmlConfByCsv(jobPath,readerMap, writerMap);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	
	/***
	 * 生成cron表达式
	 * @param strTime
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	private String rtnCronExp(String strTime) throws Exception{
		if (isCronExpression(strTime)) return strTime;
		//0 0 12 * * ?
		if ("".equals(strTime)){
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
			Date now=new Date();
			strTime=df.format(new Date(now.getTime()+5*1000));
		}
		String[] arr=strTime.split(":");
		if (arr.length != 3){
			throw new Exception("生成quartz的cron表达式异常！");
		}
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(arr[2]);
		strBuffer.append(" ");
		strBuffer.append(arr[1]);
		strBuffer.append(" ");
		strBuffer.append(arr[0]);
		strBuffer.append(" ? * *");
		return strBuffer.toString();
	}
	
	/***
	 * 判断是否为cron表达式
	 * 一个cron表达式有至少6个（也可能7个）有空格分隔的时间元素
	 * @param strTime
	 * @return
	 */
	private boolean isCronExpression(String strTime) {
		
		String[] arr = strTime.split(" ");
		return arr.length >= 6;
	}
}
