package org.egov.pdf.service;

import java.math.BigDecimal;
import java.util.List;

import com.jayway.jsonpath.JsonPath;

public class PDFUtill {

	public Object sum(Object inputData, String path) {
		String sum = "NaN";
		try {
			List<String> list = JsonPath.read(inputData, path);
			return sum(list);
		} catch (Exception e) {
			e.printStackTrace();
			sum = e.getMessage();
		}
		return sum;
	}

	public Object sum(Object inputData, String path, int scale) {
		String sum = "NaN";
		try {
			List<String> list = JsonPath.read(inputData, path);
			return sum(list, scale);
		} catch (Exception e) {
			e.printStackTrace();
			sum = e.getMessage();
		}
		return sum;
	}

	public BigDecimal sum(List list) {
		BigDecimal decimalSum = BigDecimal.ZERO;
		for (Object o : list) {
			BigDecimal decimal = null;
			if (o instanceof String) {
				decimal = new BigDecimal((String) o);
			} else if (o instanceof Integer) {
				decimal = new BigDecimal((Integer) o);
			}
			decimalSum = decimalSum.add(decimal);
		}
		return decimalSum;
	}

	public BigDecimal sum(List list, int scale) {
		return sum(list).setScale(scale, BigDecimal.ROUND_HALF_UP);
	}
}
