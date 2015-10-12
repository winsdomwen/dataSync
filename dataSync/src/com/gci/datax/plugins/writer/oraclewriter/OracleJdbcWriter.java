//package com.gci.datax.plugins.writer.oraclewriter;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//
//import com.gci.datax.plugins.writer.oraclewriter.OracleConnection;
//
//import com.gci.datax.common.plugin.Writer;
//import com.gci.datax.common.plugin.PluginParam;
//import com.gci.datax.common.plugin.PluginStatus;
//import com.gci.datax.common.plugin.LineReceiver;
//import com.gci.datax.common.util.DumperHandler;
//
//public class OracleJdbcWriter extends Writer{
//	protected OracleConnection connection;
//
//	private Logger logger = Logger.getLogger(OracleWriter.class);
//	
//	@Override
//	public int init(){
//		return PluginStatus.SUCCESS.value();
//	}
//
//	private void setUrls(ArrayList<Map<String, String>> urls){
//		for(int i = 0; i < urls.size(); i ++){
//			connection.setUrl(urls.get(i).get("ip"),
//					urls.get(i).get("port"),
//					urls.get(i).get("sid"));
//		}
//	}
//	
//	@Override
//	public int connect(){
//		/*
//		if(connection == null){
//			connection = new OracleConnection();
//		}
//		int maxconnections;
//		int threadMax;
//		try{
//			OracleTns tns = new OracleTns(StringUtil.getStrParam(iParam.getParam(ParamsKey.OracleDumper.tnsfile)));
//			ArrayList<Map<String, String>> urls = tns.find(StringUtil.getStrParam(iParam.getParam(ParamsKey.OracleDumper.dbname)));
//			connection.setPassword(StringUtil.getStrParam(iParam.getParam(ParamsKey.OracleDumper.password)));
//			connection.setUsername(StringUtil.getStrParam(iParam.getParam(ParamsKey.OracleDumper.username)));
//			threadMax = StringUtil.getIntParam(iParam.getParam("threadMax"),1,1,99);
//			//maxconnections = StringUtil.getIntParam(iParam.getParam(ParamsKey.OracleReader.maxconnection),10,threadMax+1,100);
//			maxconnections = threadMax + 1;
//			setUrls(urls);
//		}catch (Exception e){
//			e.printStackTrace();
//			return FAILED;
//		}
//		
//		connection.setMaxConnectionNum(maxconnections);
//		connection.createDatabase();
//		//logger.debug(connection.getUrl());
//		*/ 
//		 
//		return PluginStatus.SUCCESS.value();
//	}
//
//	@Override
//	public int finish(){
//		if(connection!=null){
//			connection.close();
//			connection = null;
//		}
//		return PluginStatus.SUCCESS.value();
//	}
//	
//	@Override
//	public List<PluginParam> split(PluginParam param){
//		OracleWriterSplitter spliter = new OracleWriterSplitter();
//		spliter.setParam(param);
//		spliter.init();
//		return spliter.split();	
//	}
//
//	@Override
//	public int commit() {
//		return PluginStatus.SUCCESS.value();
//	}
//
//	@Override
//	public int startWrite(LineReceiver resultHandler){
//		if(this.connection == null){
//			logger.error("connection is null!");
//			return PluginStatus.FAILURE.value();
//		}
//		
//		DumperHandler hander = new DumperHandler(resultHandler, getMonitor(), getParam());
//		hander.init();
//		hander.handle(this.connection);
//		getMonitor().setStatus(PluginStatus.WRITE_OVER);
//		return PluginStatus.SUCCESS.value();
//	}
//}
