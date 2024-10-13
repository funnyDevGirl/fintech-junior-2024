package org.tbank.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tbank.dto.convert.CurrencyConversionRequest;
import org.tbank.dto.convert.CurrencyConversionResponse;
import org.tbank.dto.currency.CurrencyRateDTO;

@ExtendWith(MockitoExtension.class)
public class ConversionServiceTest {

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private ConversionService conversionService;

    private CurrencyConversionRequest request;

    @BeforeEach
    public void setUp() {
        request = new CurrencyConversionRequest();
    }

    @Test
    public void testConvert_ValidAmountAndCurrency() {
        // Setup
        request.setAmount(100.0);
        request.setFromCurrency("USD");
        request.setToCurrency("EUR");

        CurrencyRateDTO usdRate = new CurrencyRateDTO(10L,"USD", 1.0);
        CurrencyRateDTO eurRate = new CurrencyRateDTO(20L,"EUR", 0.85);

        when(currencyRateService.getCurrencyRate("USD")).thenReturn(usdRate);
        when(currencyRateService.getCurrencyRate("EUR")).thenReturn(eurRate);

        // Act
        CurrencyConversionResponse response = conversionService.convert(request);

        // Assert
        assertEquals("USD", response.getFromCurrency());
        assertEquals("EUR", response.getToCurrency());
        assertEquals(118, Math.round(response.getConvertedAmount()));
    }

    @Test
    public void testConvert_InvalidAmount() {
        // Setup
        request.setAmount(0.0); // Неверное значение
        request.setFromCurrency("EUR");
        request.setToCurrency("USD");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convert(request);
        });

        assertEquals("Amount must be greater than zero", thrown.getMessage());
    }

    @Test
    public void testConvert_UnsupportedCurrency() {
        // Setup
        request.setAmount(100.0);
        request.setFromCurrency("USD");
        request.setToCurrency("INVALID"); // Неверный код валюты

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convert(request);
        });

        assertEquals("Unsupported currency code", thrown.getMessage());
    }

    @Test
    public void testConvert_MultipleInvalidCurrencies() {
        // Setup
        request.setAmount(100.0);
        request.setFromCurrency("INVALID_FROM");
        request.setToCurrency("INVALID_TO");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            conversionService.convert(request);
        });

        assertEquals("Unsupported currency code", thrown.getMessage());
    }

    @Test
    public void testConvert_ConvertsWithCachedExchangeRate() {
        // Setup
        request.setAmount(200.0);
        request.setFromCurrency("USD");
        request.setToCurrency("EUR");

        CurrencyRateDTO usdRate = new CurrencyRateDTO(10L,"USD", 1.0);
        CurrencyRateDTO eurRate = new CurrencyRateDTO(20L,"EUR", 0.5);

        when(currencyRateService.getCurrencyRate("USD")).thenReturn(usdRate);
        when(currencyRateService.getCurrencyRate("EUR")).thenReturn(eurRate);

        // Act
        CurrencyConversionResponse response = conversionService.convert(request);

        // Assert
        assertEquals("USD", response.getFromCurrency());
        assertEquals("EUR", response.getToCurrency());
        assertEquals(400.0, response.getConvertedAmount()); // 200 / 0.5
    }
}
