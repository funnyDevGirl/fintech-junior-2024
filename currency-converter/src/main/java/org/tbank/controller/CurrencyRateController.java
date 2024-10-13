package org.tbank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tbank.dto.convert.CurrencyConversionRequest;
import org.tbank.dto.convert.CurrencyConversionResponse;
import org.tbank.dto.currency.CurrencyRateDTO;
import org.tbank.handler.ErrorResponse;
import org.tbank.service.ConversionService;
import org.tbank.service.CurrencyRateService;

@RestController
@RequestMapping("api/v1/currencies")
@AllArgsConstructor
public class CurrencyRateController {

    private final CurrencyRateService currencyService;
    private final ConversionService conversionService;


    @Operation(
            summary = "Get the currency rate by code",
            description = "Returns the currency exchange rate by its code."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency rate has been successfully received",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CurrencyRateDTO.class),
                            examples = {@ExampleObject(value = "{\"id\": \"1\", \"currency\": \"USD\", \"rate\": \"94.15\"}")})
            ),
            @ApiResponse(responseCode = "404", description = "Unsupported currency code",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"404\", \"message\": \"Unsupported currency code - THB\"}")})
            ),
            @ApiResponse(responseCode = "400", description = "Non-existent currency code format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"400\", \"message\": \"Non-existent currency code - LALA\"}")})
            ),
            @ApiResponse(responseCode = "503", description = "The currency rate service is unavailable",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"503\", \"message\": \"The currency rate service is unavailable\"}")}))
    })
    @GetMapping("/rates/{code}")
    @ResponseStatus(HttpStatus.OK)
    public CurrencyRateDTO getCurrencyRate(@PathVariable String code) {

        return currencyService.getCurrencyRate(code);
    }


    @Operation(
            summary = "Perform a currency conversion",
            description = "Exchanges one currency for another."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency exchange is successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CurrencyConversionResponse.class),
                            examples = {@ExampleObject(value = "{\"fromCurrency\": \"USD\", \"toCurrency\": \"EUR\", \"convertedAmount\": \"65.5\"}")})
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"400\", \"message\": \"Amount must be greater than zero\"}")})
            ),
            @ApiResponse(responseCode = "404", description = "Unsupported currency code",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"404\", \"message\": \"Unsupported currency code - THB\"}")})
            ),
            @ApiResponse(responseCode = "503", description = "The currency rate service is unavailable",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"503\", \"message\": \"The currency rate service is unavailable\"}")}))
    })
    @PostMapping("/convert")
    @ResponseStatus(HttpStatus.OK)
    public CurrencyConversionResponse convertCurrency(@RequestBody @Valid CurrencyConversionRequest request) {

        return conversionService.convert(request);
    }
}
