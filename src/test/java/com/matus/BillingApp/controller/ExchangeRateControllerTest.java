package com.matus.BillingApp.controller;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.service.ExchangeRateService;
import com.matus.BillingApp.util.CurrencyConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.matus.BillingApp.controller.ExchangeRateController.MISSING_INPUT_FILE;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateControllerTest {

    @Mock
    ExchangeRateService exchangeRateService;

    @InjectMocks
    ExchangeRateController exchangeRateController;

    private MockMvc mockMvc;

    private DateTimeFormatter dtf;

    private LocalDateTime now;

    //given
    //when
    //then

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exchangeRateController).build();

        dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        now = LocalDateTime.now();
    }

    @Test
    void viewMainPageTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/currency-converter/usd-to-zar"));
    }

    @Test
    void getTodayExchangeRateMissingFileTest() throws Exception {
        mockMvc.perform(get("/get-new-exchange")
                .param("path", "/...."))
                .andExpect(status().isOk())
                .andExpect(view().name(MISSING_INPUT_FILE));
    }

    @Test
    void getTodayExchangeRateDummyFileTest() throws Exception {
        mockMvc.perform(get("/get-new-exchange")
                .param("path", "C:\\Users\\Matus\\Desktop\\box\\exchange_rates_to_USD.xlsx"))
                .andExpect(status().isOk())
                .andExpect(view().name(MISSING_INPUT_FILE));
    }

    @Test
    void getTodayExchangeRateSuccess() throws Exception {
        List<ExchangeRate> todayRate = new ArrayList<>();
        List<ExchangeRate> rateDB = new ArrayList<>();
        todayRate.add(new ExchangeRate());
        rateDB.add(new ExchangeRate("1", 12.0,dtf.format(now), Currency.USD, Currency.ZAR));

        given(exchangeRateService.getNewExchangeRate("C:/Users/Matus/Desktop/box/exchange_rates_to_USD.xlsx"))
                .willReturn(todayRate);
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(rateDB);

        mockMvc.perform(get("/get-new-exchange")
                .param("path", "C:/Users/Matus/Desktop/box/exchange_rates_to_USD.xlsx"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/currency-converter/usd-to-zar"));
    }

    @Test
    void getTodayExchangeRateSuccess2() throws Exception {//change this stupid name
        List<ExchangeRate> todayRate = new ArrayList<>();
        List<ExchangeRate> rateDB = new ArrayList<>();
        todayRate.add(new ExchangeRate("1", 12.0,dtf.format(now), Currency.USD, Currency.ZAR));

        given(exchangeRateService.getNewExchangeRate("C:/Users/Matus/Desktop/box/exchange_rates_to_USD.xlsx"))
                .willReturn(todayRate);
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(rateDB);

        mockMvc.perform(get("/get-new-exchange")
                .param("path", "C:/Users/Matus/Desktop/box/exchange_rates_to_USD.xlsx"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/currency-converter/usd-to-zar"));
    }

    @Test
    void getTodayExchangeRateNotUpdatedFileWithTodayDate() throws Exception {//change this stupid name
        List<ExchangeRate> todayRate = new ArrayList<>();
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,"12/23/2120", Currency.USD, Currency.ZAR);
        List<ExchangeRate> rateDB = new ArrayList<>();
        todayRate.add(exchangeRate);

        given(exchangeRateService.getNewExchangeRate("C:/Users/Matus/Desktop/box/exchange_rates_to_USD.xlsx"))
                .willReturn(todayRate);
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(rateDB);

        mockMvc.perform(get("/get-new-exchange")
                .param("path", "C:/Users/Matus/Desktop/box/exchange_rates_to_USD.xlsx"))
                .andExpect(status().isOk())
                .andExpect(view().name(MISSING_INPUT_FILE));
    }
    @Test
    void deleteExchangeRateByIdUsdTest() throws Exception {
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,"12/23/2120", Currency.USD, Currency.ZAR);

        mockMvc.perform(get("/delete/exchange-rate/{currency}/{exchangeRateId}", Currency.USD, "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/history-of-rates/USD"));
    }
    @Test
    void deleteExchangeRateByIdZarTest() throws Exception {
        mockMvc.perform(get("/delete/exchange-rate/{currency}/{exchangeRateId}", Currency.ZAR, "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/history-of-rates/ZAR"));
    }

    @Test
    void viewUsdToZarTest() throws Exception {
        //given
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,dtf.format(now), Currency.USD, Currency.ZAR);
        List<ExchangeRate> givenRates = Arrays.asList(exchangeRate);

        given(exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(exchangeRate.getCurrencyFrom())))
                .willReturn(givenRates);//for test it will return itself
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(givenRates);

        mockMvc.perform(get("/currency-converter/usd-to-zar"))
                .andExpect(status().isOk())
                .andExpect(view().name("usdtozar"))
                .andExpect(model().attributeExists("exchangeRate"))
                .andExpect(model().attributeExists("converter"))
                .andExpect(model().attributeExists("lisOfRates"))
                .andExpect(model().attributeExists("average"))
                .andExpect(model().attributeExists("currency"))
                .andExpect(model().attribute("exchangeRate", exchangeRate))
                .andExpect(model().attribute("lisOfRates",
                        hasItem(allOf(
                                hasProperty("id",is("1")),
                                hasProperty("exchangeRateValue",is(12.0)),
                                hasProperty("date",is(dtf.format(now))),
                                hasProperty("currencyFrom",is(Currency.USD)),
                                hasProperty("currencyTo",is(Currency.ZAR))
                ))));
    }

    @Test
    void viewUsdToZarEmptyHistoryTest() throws Exception {
        //given
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,dtf.format(now), Currency.USD, Currency.ZAR);
        List<ExchangeRate> givenRates = new ArrayList<>();

        given(exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(exchangeRate.getCurrencyFrom())))
                .willReturn(givenRates);//for test it will return itself
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(givenRates);

        mockMvc.perform(get("/currency-converter/usd-to-zar"))
                .andExpect(status().isOk())
                .andExpect(view().name("usdtozar"))
                .andExpect(model().attributeExists("exchangeRate"))
                .andExpect(model().attributeExists("today"))
                .andExpect(model().attributeExists("converter"))
                .andExpect(model().attributeExists("lisOfRates"))
                .andExpect(model().attributeExists("average"))
                .andExpect(model().attributeExists("currency"));
    }

    @Test
    void viewZarToUsdTest() throws Exception {
        //given
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,dtf.format(now), Currency.ZAR, Currency.USD);
        List<ExchangeRate> givenRates = Arrays.asList(exchangeRate);

        given(exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(exchangeRate.getCurrencyFrom())))
                .willReturn(givenRates);//for test it will return itself
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(givenRates);

        mockMvc.perform(get("/currency-converter/zar-to-usd"))
                .andExpect(status().isOk())
                .andExpect(view().name("zartousd"))
                .andExpect(model().attributeExists("exchangeRate"))
                .andExpect(model().attributeExists("converter"))
                .andExpect(model().attributeExists("lisOfRates"))
                .andExpect(model().attributeExists("average"))
                .andExpect(model().attributeExists("currency"))
                .andExpect(model().attribute("exchangeRate", exchangeRate))
                .andExpect(model().attribute("lisOfRates",
                        hasItem(allOf(
                                hasProperty("id",is("1")),
                                hasProperty("exchangeRateValue",is(12.0)),
                                hasProperty("date",is(dtf.format(now))),
                                hasProperty("currencyFrom",is(Currency.ZAR)),
                                hasProperty("currencyTo",is(Currency.USD))
                        ))));
    }

    @Test
    void viewZarToUsdEmptyHistoryTest() throws Exception {
        //given
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,dtf.format(now), Currency.ZAR, Currency.USD);
        List<ExchangeRate> givenRates = new ArrayList<>();

        given(exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(exchangeRate.getCurrencyFrom())))
                .willReturn(givenRates);//for test it will return itself
        given(exchangeRateService.findByDate(dtf.format(now))).willReturn(givenRates);

        mockMvc.perform(get("/currency-converter/zar-to-usd"))
                .andExpect(status().isOk())
                .andExpect(view().name("zartousd"))
                .andExpect(model().attributeExists("exchangeRate"))
                .andExpect(model().attributeExists("today"))
                .andExpect(model().attributeExists("converter"))
                .andExpect(model().attributeExists("lisOfRates"))
                .andExpect(model().attributeExists("average"))
                .andExpect(model().attributeExists("currency"));
    }

    @Test
    void adjustHistoryOfExchangeRateForUsdTest() throws Exception {
        //given
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,dtf.format(now), Currency.USD, Currency.ZAR);
        List<ExchangeRate> givenRates = Arrays.asList(exchangeRate);
        given(exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.USD))).willReturn(givenRates);
        given(exchangeRateService.findAverageExchangeRate(givenRates)).willReturn(exchangeRate.getExchangeRateValue());

        mockMvc.perform(get("/history-of-rates/{currency}", Currency.USD))
                .andExpect(status().isOk())
                .andExpect(view().name("historyofexchangerates"))
                .andExpect(model().attribute("rates", hasSize(1)));
    }

    @Test
    void adjustHistoryOfExchangeRateForZarTest() throws Exception {
        //given
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,dtf.format(now), Currency.ZAR, Currency.USD);
        List<ExchangeRate> givenRates = Arrays.asList(exchangeRate);
        given(exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.ZAR))).willReturn(givenRates);
        given(exchangeRateService.findAverageExchangeRate(givenRates)).willReturn(exchangeRate.getExchangeRateValue());

        mockMvc.perform(get("/history-of-rates/{currency}", exchangeRate.getCurrencyFrom()))
                .andExpect(status().isOk())
                .andExpect(view().name("historyofexchangerates"))
                .andExpect(model().attribute("rates", hasSize(1)));
    }

    @Test
    void addExchangeRateZarTest() throws Exception {
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,dtf.format(now), Currency.ZAR, Currency.USD);

        mockMvc.perform(post("/add-new-er/{currency}", exchangeRate.getCurrencyFrom())
                    .param("exchangeRateValue", String.valueOf(exchangeRate.getExchangeRateValue()))
                    .param("date", "2012-12-25")
                    .param("currencyFrom", String.valueOf(exchangeRate.getCurrencyFrom())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/history-of-rates/ZAR"));
    }

    @Test
    void addExchangeRateUsdTest() throws Exception {
        ExchangeRate exchangeRate = new ExchangeRate("1", 12.0,dtf.format(now), Currency.USD, Currency.ZAR);

        mockMvc.perform(post("/add-new-er/{currency}", exchangeRate.getCurrencyFrom())
                    .param("exchangeRateValue", String.valueOf(exchangeRate.getExchangeRateValue()))
                    .param("date", "2012-12-25")
                    .param("currencyFrom", String.valueOf(exchangeRate.getCurrencyFrom())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/history-of-rates/USD"));
    }

    @Test
    void addExchangeRateNullValuesTest() throws Exception {
        mockMvc.perform(post("/add-new-er/{currency}", Currency.USD)
                    .param("date", "2012-12-25"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/history-of-rates/USD"));
    }

    @Test
    void addExchangeParseExceptionTest() throws Exception {
        mockMvc.perform(post("/add-new-er/{currency}", Currency.USD)
                .param("date", dtf.format(now)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/history-of-rates/USD"));
    }
}