package com.gci.datax.common.constants;

public enum ExitStatus {
	FAILED(2), SUCCESSFUL(0);

	private int status;

	private ExitStatus(int status) {
		this.status = status;
	}

	public int value() {
		return status;
	}

}