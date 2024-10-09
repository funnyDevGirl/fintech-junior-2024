package org.tbank.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.tbank.dto.currency.CurrencyRateCreateDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class XmlParser {

    public List<CurrencyRateCreateDTO> parseCurrency(String url) {

       log.info("Getting a list of available currencies is starting.");

        List<CurrencyRateCreateDTO> rates = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements currencies = doc.select("Valute");

            for (Element currency : currencies) {
                String charCode = currency.select("CharCode").text();
                Double vunitRate = Double.parseDouble(
                        currency.select("VunitRate").text().replace(",", "."));

                log.info("The currency was received with CharCode: {} and VunitRate: {}", charCode, vunitRate);

                CurrencyRateCreateDTO rate = new CurrencyRateCreateDTO(charCode, vunitRate);

                log.info("Currency has been successfully extracted: {}", rate);

                rates.add(rate);
            }
        } catch (IOException e) {

            log.error("Error when getting a list of acceptable currencies.", e);
        }
        log.info("The list of available currencies has been successfully received.");

        return rates;
    }
}
