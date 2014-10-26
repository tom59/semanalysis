package com.hcomemea.sem.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MathUtils {

	public static final Double average(List<Double> scores) {
		BigDecimal total = new BigDecimal(0);
		for (Double score : scores) {
			total = total.add(BigDecimal.valueOf(score));
		}
		if (scores.size() > 0) {
			total = total.divide(BigDecimal.valueOf(scores.size()), RoundingMode.DOWN);
		}
		return total.doubleValue();
	}
		
}
