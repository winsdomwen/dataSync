package com.gci.datax.common.constants;

public abstract class Constants {
	public static final String SYSTEM_LOCATION = System.getProperty("user.dir");
	//public static final String DATAX_LOCATION = SYSTEM_LOCATION.substring(0,Constants.SYSTEM_LOCATION.length()-3)+"webapps/dataSync";
	public static final String DATAX_LOCATION = getAppLocation();
	public static final String ENGINEXML = getAppLocation() + "/conf/engine.xml";
	public static final String JOBSXML = getAppLocation() + "/conf/jobs.xml";
	public static final String JOBSXMLDIR = getAppLocation() + "/jobs/{0}.xml";
    public static final String PLUGINSXML = getAppLocation() + "/conf/plugins.xml";
    public static final String PARAMCONFIG = getAppLocation() + "/conf/ParamsKey.java";
    public static final String LOG4JPRO=getAppLocation()+"/conf/log4j.properties";
    
    private static String getAppLocation(){
    	System.out.println( "system_loaction="+SYSTEM_LOCATION);
 
    	if(SYSTEM_LOCATION.equals("/"))
    		return  "/opt/tomcat/apache-tomcat-6.0.35-7777/webapps/dataSync"; 
    	 else if(SYSTEM_LOCATION.contains("bin"))
    		return SYSTEM_LOCATION.substring(0,Constants.SYSTEM_LOCATION.length()-3)+"webapps/dataSync";
    	else
    		return SYSTEM_LOCATION+"/webapps/dataSync";
    }
}