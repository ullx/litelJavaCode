package com.games;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Utils {

	public static String mapToString(Map m) {
		if (m == null) {
			throw new NullPointerException("Map can't be null");
		}

		Iterator it = m.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		Object o = null;
		if (it.hasNext()) {
			o = it.next();
		}

		while (o != null) {
			sb.append("{" + o);
			Object value = m.get(o);
			if (value instanceof Object[]) {
				sb.append("[");
				Object[] array = (Object[]) value;
				for (int i = 0; i < array.length; i++) {
					sb.append(array[i]);
					if (i + 1 < array.length) {
						sb.append(",");
					}
				}
				sb.append("]");
			}
			
			if(value instanceof List) {
				sb.append("[");
				List list = (List) value;
				for (int i = 0; i < list.size(); i++) {
					sb.append(list.get(i));
					if (i + 1 < list.size()) {
						sb.append(",");
					}
				}
				sb.append("]");
			}

			if (it.hasNext()) {
				sb.append("}, ");
				o = it.next();
			} else {
				sb.append("}");
				o = null;
			}
		}
		return sb.toString();
	}

}
