package fw.jbiz.ext.json;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ZSimpleJsonArray {

	private List<Object> fields;

	public ZSimpleJsonArray() {
		fields = new ArrayList<Object>();
	}

	public void add(Object object) {

		fields.add(object);
	}

	

	public String toString() {

		StringBuffer buffer = new StringBuffer("[");
		for (int i = 0; i < fields.size(); i++) {
			buffer.append(fields.get(i).toString());
			buffer.append(",");
		}
		if (fields.size() > 0) {
			buffer.deleteCharAt(buffer.toString().length() - 1);
		}
		buffer.append("]");
		return buffer.toString();
	}
}
