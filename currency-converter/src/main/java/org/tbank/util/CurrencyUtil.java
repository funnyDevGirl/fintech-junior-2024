package org.tbank.util;

import lombok.extern.slf4j.Slf4j;
import java.util.Currency;

@Slf4j
public class CurrencyUtil {

    public static boolean isCurrencyValid(String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            return currency != null;

        } catch (IllegalArgumentException e) {

            log.error("No currency with code: '{}'.", currencyCode, e);
            return false;
        }
    }
}
