package com.gci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constant {
	public static final String 	TRIGGERNAME = "triggerName";
	public static final String 	TRIGGERGROUP = "triggerGroup";
	public static final String STARTTIME = "startTime";
	public static final String ENDTIME = "endTime";
	public static final String REPEATCOUNT = "repeatCount";
	public static final String REPEATINTERVEL = "repeatInterval";
	public static ArrayList<String> jobList= new ArrayList<String>();
	public static int jobState;
	
	public static final Map<String,String> status = new HashMap<String,String>();
	static{
		status.put("ACQUIRED", "运行中");
		status.put("PAUSED", "已暂停");
		status.put("WAITING", "等待中");	
	}
}
