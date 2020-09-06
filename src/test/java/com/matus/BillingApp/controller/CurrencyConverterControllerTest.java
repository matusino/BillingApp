package com.matus.BillingApp.controller;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.matus.BillingApp.controller.ExchangeRateController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)
class CurrencyConverterControllerTest {

    @Mock
    ExchangeRateService exchangeRateService;

    @InjectMocks
    CurrencyConverterController converterController;

    private MockMvc mockMvc;

    private DateTimeFormatter dtf;

    private  LocalDateTime now;

    private ExchangeRate exchangeRateFromUsd;

    private ExchangeRate exchangeRateFromZar;

    private List<ExchangeRate> list;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(converterController).build();

        exchangeRateFromUsd = new ExchangeRate("1", 12.0,"08/20/2020", Currency.USD, Currency.ZAR);
        exchangeRateFromZar = new ExchangeRate("1", 12.0,"08/20/2020", Currency.ZAR, Currency.USD);

        list = new ArrayList<>();

        dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        now = LocalDateTime.now();
    }

    @Test
    void convertUsdToZarNotFoundAnyTest() throws Exception {
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(list);

        mockMvc.perform(get("/convert/usd-to-zar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT_CURRENCY_CONVERTER_USD_TO_ZAR));
    }

    @Test
    void convertUsdToZarFoundDateTest() throws Exception {
        //given
        list.add(exchangeRateFromUsd);

        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(list);

        mockMvc.perform(get("/convert/usd-to-zar")
                .param("amount", "12"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("exchangeRate"))
                .andExpect(model().attributeExists("converter"))
                .andExpect(model().attributeExists("lisOfRates"))
                .andExpect(model().attributeExists("average"))
                .andExpect(model().attributeExists("currency"))
                .andExpect(view().name(CONVERT_FROM_USD_TO_ZAR));
    }
    @Test
    void convertUsdToZarNotFoundUsd() throws Exception {
        list.add(exchangeRateFromUsd);

        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(list);

        mockMvc.perform(get("/convert/usd-to-zar")
                .param("amount", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT_CURRENCY_CONVERTER_USD_TO_ZAR));
    }

    @Test
    void convertZarToUsdNotFoundAnyTest() throws Exception {
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(list);

        mockMvc.perform(get("/convert/zar-to-usd"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT_CURRENCY_CONVERTER_ZAR_TO_USD));
    }

    @Test
    void convertZarToUsdFoundDateTest() throws Exception {
        //given
        list.add(exchangeRateFromZar);

        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(list);

        mockMvc.perform(get("/convert/zar-to-usd")
                .param("amount", "12"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("exchangeRate"))
                .andExpect(model().attributeExists("converter"))
                .andExpect(model().attributeExists("lisOfRates"))
                .andExpect(model().attributeExists("average"))
                .andExpect(model().attributeExists("currency"))
                .andExpect(view().name(CONVERT_FROM_ZAR_TO_USD));
    }
    @Test
    void convertZarToUsdNotFound() throws Exception {
        list.add(exchangeRateFromZar);

        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(list);

        mockMvc.perform(get("/convert/zar-to-usd")
                .param("amount", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT_CURRENCY_CONVERTER_ZAR_TO_USD));
    }
}