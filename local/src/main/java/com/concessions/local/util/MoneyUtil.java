package com.concessions.local.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtil {

	private static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

	public MoneyUtil() {
		// TODO Auto-generated constructor stub
	}

	public static String formatAsMoney (BigDecimal amount) {
        return currencyFormatter.format(amount);
    }
}
