package org.tbank.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    RUB("rub"),
    EUR("eur"),
    USD("usd");

    private final String code;
}
