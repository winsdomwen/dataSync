package com.gci.datax.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 数组帮助类
 * 
 */
public abstract class ArrayUtils {
	
	public static int[] copy(int[] iarr) {
		int[] oarr = null;
		if (iarr != null) {
			oarr = new int[iarr.length];
			System.arraycopy(iarr, 0, oarr, 0, oarr.length);
		}
		return oarr;
	}

	public static String[] copy(String[] iarr) {
		String[] oarr = null;
		if (iarr != null) {
			oarr = new String[iarr.length];
			System.arraycopy(iarr, 0, oarr, 0, oarr.length);
		}
		return oarr;
	}
	
	public static<T> List<List<T>> spitList(final List<T> all, final int length) {
		List<List<T>> batches = new ArrayList<List<T>>();
		for (int i = 0, s = 0, t = 0; s < all.size(); s = t, i++) {
			t = s + length;
			if (t > all.size()) {
				t = all.size();
			}
			batches.add(all.subList(s, t));
		}
		return batches;
	}
	
}
