package com.gci.datax.engine.conf;

/**
 * 插件配置类.
 * 
 * */
public class PluginConf {
	private String path;

	private String version;

	private String name;

	private String type;

	private String target;

	private String jar;

	private String className;

	private int maxthreadnum;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getJar() {
		return jar;
	}

	public void setJar(String jar) {
		this.jar = jar;
	}

	public int getMaxthreadnum() {
		return maxthreadnum;
	}

	public void setMaxthreadnum(int maxthreadnum) {
		this.maxthreadnum = maxthreadnum;
	}

}
