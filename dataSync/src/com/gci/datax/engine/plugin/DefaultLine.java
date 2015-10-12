package com.gci.datax.engine.plugin;

import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.PluginConst;

/**
 * 实现{@link Line}.
 * 
 * */
public class DefaultLine implements Line {
	private String[] fieldList;

	private int length = 0;

	private int fieldNum = 0;

	public DefaultLine() {
		this.fieldList = new String[PluginConst.LINE_MAX_FIELD];
	}

	public void clear() {
		length = 0;
		fieldNum = 0;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public boolean addField(String field) {
		fieldList[fieldNum] = field;
		fieldNum++;
		if (field != null)
			length += field.length();
		return true;
	}

	@Override
	public boolean addField(String field, int index) {
		fieldList[index] = field;
		if (fieldNum < index + 1)
			fieldNum = index + 1;
		if (field != null)
			length += field.length();
		return true;
	}

	@Override
	public int getFieldNum() {
		return fieldNum;
	}

	@Override
	public String getField(int idx) {
		return fieldList[idx];
	}
	
	public String checkAndGetField(int idx) {
		if (idx < 0 ||
				idx >= fieldNum) {
			return null;
		}
		return fieldList[idx];
	}

	@Override
	public StringBuffer toStringBuffer(char separator) {
		StringBuffer tmp = new StringBuffer();
		tmp.append(fieldNum);
		tmp.append(":\t");
		for (int i = 0; i < fieldNum; i++) {
			tmp.append(fieldList[i]).append(separator);
		}
		return tmp;
	}
	
	@Override
	public String toString(char separator) {
		return this.toStringBuffer(separator).toString();
	}

	@Override
	public Line fromString(String lineStr, char separator) {
		return null;
	}
}
