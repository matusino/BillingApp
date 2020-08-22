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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(converterController).build();
    }

    @Test
    void convertUsdToZarNotFoundAnyTest() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();
        List<ExchangeRate> list= new ArrayList<>();
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(list);

        mockMvc.perform(get("/convert/usd-to-zar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/currency-converter/usd-to-zar"));
    }

    @Test
    void convertUsdToZarFoundDateTest() throws Exception {
        //given
        List<ExchangeRate> list= new ArrayList<>();
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,"08/20/2020", Currency.USD, Currency.ZAR);
        list.add(exchangeRate);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(list);

        mockMvc.perform(get("/convert/usd-to-zar")
                .param("amount", "12"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("exchangeRate"))
                .andExpect(model().attributeExists("converter"))
                .andExpect(model().attributeExists("lisOfRates"))
                .andExpect(model().attributeExists("average"))
                .andExpect(model().attributeExists("currency"))
                .andExpect(view().name("usdtozar"));
    }
    @Test
    void convertUsdToZarNotFoundUsd() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> list= new ArrayList<>();
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,dtf.format(now), Currency.USD, Currency.ZAR);


        list.add(exchangeRate);

        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(list);

        mockMvc.perform(get("/convert/usd-to-zar")
                .param("amount", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/currency-converter/usd-to-zar"));
    }

    @Test
    void convertZarToUsd() {
    }
}