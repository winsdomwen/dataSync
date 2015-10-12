package com.gci.datax.common.util;

import java.io.IOException;
import java.lang.reflect.Field;

import com.gci.datax.common.constants.Constants;

/**
 * 系统环境变量工具
 * 
 */
public final class EnvUtils {
	static {
		//System.load(Constants.DATAX_LOCATION + "/common/libcommon.so");
		System.load(Constants.DATAX_LOCATION + "/c++/build/libcommon.so");
	}

	private EnvUtils() {

	}

	public native static String getEnv(String key);

	public native static int putEnv(String key, String value);

	public static synchronized int addLibraryPath(String s) throws IOException {
		if (null == s) {
			throw new IllegalArgumentException("Path cannot be null .");
		}

		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);
            for (String path : paths) {
                if (s.equals(path)) {
                    return 0;
                }
            }
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 0, paths.length);
			tmp[paths.length] = s;
			field.set(null, tmp);
		
		} catch (IllegalAccessException e) {
			throw new IOException(
					"Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException(
					"Failed to get field handle to set library path");
		}

		return 0;
	}

}
