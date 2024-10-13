package org.tbank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.dto.convert.CurrencyConversionRequest;
import org.tbank.dto.convert.CurrencyConversionResponse;
import org.tbank.dto.currency.CurrencyRateDTO;
import org.tbank.service.ConversionService;
import org.tbank.service.CurrencyRateService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class CurrencyRateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurrencyRateService currencyService;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private CurrencyRateController currencyRateController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = standaloneSetup(currencyRateController).build();
    }

    @Test
    public void getCurrencyRate_validCode_returnsCurrencyRateDTO() throws Exception {
        // Arrange
        CurrencyRateDTO mockedDto = new CurrencyRateDTO(10L, "USD", 94.15);
        when(currencyService.getCurrencyRate("USD")).thenReturn(mockedDto);

        // Act, Assert
        mockMvc.perform(get("/api/v1/currencies/rates/USD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.rate").value(94.15));
    }

    @Test
    public void convertCurrency_validRequest_returnsCurrencyConversionResponse() throws Exception {
        // Arrange
        CurrencyConversionResponse conversionResponse = new CurrencyConversionResponse("USD", "EUR", 65.5);
        CurrencyConversionRequest conversionRequest = new CurrencyConversionRequest("USD", "EUR", 70.5);
        when(conversionService.convert(any())).thenReturn(conversionResponse);

        // Act, Assert
        mockMvc.perform(post("/api/v1/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromCurrency\": \"USD\", \"toCurrency\": \"EUR\", \"amount\": 70.5}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"))
                .andExpect(jsonPath("$.convertedAmount").value(65.5));
    }
}
