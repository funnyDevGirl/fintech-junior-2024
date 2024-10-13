package org.tbank.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tbank.dto.currency.CurrencyRateCreateDTO;
import org.tbank.dto.currency.CurrencyRateDTO;
import org.tbank.exception.CurrencyNotFoundException;
import org.tbank.exception.CurrencyServiceUnavailableException;
import org.tbank.mapper.CurrencyRateMapper;
import org.tbank.model.CurrencyRate;
import org.tbank.repository.CurrencyRateRepository;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceTest {

    @Mock
    private CurrencyRateMapper mapper;

    @Mock
    private CurrencyRateRepository repository;

    @InjectMocks
    private CurrencyRateService currencyRateService;

    private CurrencyRate currencyRate;
    private CurrencyRateDTO currencyRateDTO;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    void setUp() {
        currencyRate = new CurrencyRate(); // Initialize with necessary values
        currencyRateDTO = new CurrencyRateDTO(); // Initialize as necessary

        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut); // Reset System.out back to original
        repository.deleteAll();
    }

    @Test
    void getCurrencyRate_validCode_returnsDTO() {
        String code = "USD";
        when(repository.findByCurrency(code)).thenReturn(Optional.of(currencyRate));
        when(mapper.map(currencyRate)).thenReturn(currencyRateDTO);

        CurrencyRateDTO result = currencyRateService.getCurrencyRate(code);

        assertEquals(currencyRateDTO, result);
        verify(repository).findByCurrency(code);
        verify(mapper).map(currencyRate);
    }

    @Test
    void getCurrencyRate_invalidCode_throwsCurrencyNotFoundException() {
        String code = "INVALID_CODE";
        when(repository.findByCurrency(code)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CurrencyNotFoundException.class, () ->
                currencyRateService.getCurrencyRate(code));

        assertEquals("CurrencyRate with code: 'INVALID_CODE' not found", exception.getMessage());
    }

    @Test
    void parseCurrency() throws IOException, URISyntaxException {
        CurrencyRateCreateDTO dto = new CurrencyRateCreateDTO("GBP", 125.6995); // одна из валют из xml

        File mockFile = new File(getClass().getClassLoader().getResource("xml.xml").toURI()); // получаею путь к файлу-заглушке
        Document doc = Jsoup.parse(mockFile, "UTF-8"); // использую Jsoup для чтения файла

        assertNotNull(doc);

        List<CurrencyRateCreateDTO> rates = new ArrayList<>();

        Elements currencies = doc.select("Valute");
        for (Element currency : currencies) {
            String charCode = currency.select("CharCode").text();
            Double vunitRate = Double.parseDouble(currency.select("VunitRate").text().replace(",", "."));

            CurrencyRateCreateDTO rate = new CurrencyRateCreateDTO(charCode, vunitRate);

            rates.add(rate);
        }

        assertEquals(rates.size(), 43);
        assertTrue(rates.contains(dto));
    }

    @Test
    void fallBackMethod_logsError() {
        // Arrange
        Throwable throwable = new RuntimeException("Simulated error");

        // Act
        CurrencyServiceUnavailableException exception = assertThrows(
                CurrencyServiceUnavailableException.class,
                () -> currencyRateService.fallBackMethod(throwable));

        // Assert
        assertEquals("The currency rate service is unavailable. Try again later.", exception.getMessage());

        // Verify that the log message was printed
        String capturedOutput = outputStreamCaptor.toString().trim();
        assertTrue(capturedOutput.contains("Error receiving currency rates from the Central Bank service"));
    }
}
