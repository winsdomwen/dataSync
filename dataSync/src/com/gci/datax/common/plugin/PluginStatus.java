package com.gci.datax.common.plugin;

/**
 * {@link Pluginable}状态.
 * 
 * */
public enum PluginStatus {
	FAILURE(-1),
	SUCCESS(0),
	CONNECT(1),
	READ(2),
	READ_OVER(3),
	WRITE(4),
	WRITE_OVER(5),	
	WAITING(6);
	
	private int status;

	public int value() {
		return status;
	}

	private PluginStatus(int status) {
		this.status = status;
	}
	
	public static void main(String[] args) {
		System.out.println(PluginStatus.FAILURE);
		System.out.println(PluginStatus.FAILURE.value());
	}
}
