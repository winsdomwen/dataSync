package com.gci.datax.engine.storage;

import com.gci.datax.common.exception.DataExchangeException;

/**
 * storage工厂类
 * 
 */
public class StorageFactory {
	private StorageFactory(){
	}
	
	/**
	 * 根据类名生成具体Storage类
	 * 
	 */
	public static Storage product(String className) {
		try {
			return (Storage) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new DataExchangeException(e.getCause());
		}
	}
}
