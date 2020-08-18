package com.matus.BillingApp.service;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.repository.ExchangeRateRepository;
import com.matus.BillingApp.service.impl.ExchangeRateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {

    @Mock
    ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    ExchangeRateServiceImpl exchangeRateService;

    private ExchangeRate exchangeRate;

    @BeforeEach
    void setUp() {
        exchangeRate = new ExchangeRate();
    }

    @Test
    void findByDateTest(){
        //given
        exchangeRate.setDate("12/12/2121");
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        exchangeRates.add(exchangeRate);
        given(exchangeRateRepository.findByDate("12/12/2121")).willReturn(exchangeRates);

        //when
        List<ExchangeRate> foundRates = exchangeRateService.findByDate("12/12/2121");

        //then
        then(exchangeRateRepository).should().findByDate("12/12/2121");
        assertEquals(1, foundRates.size());
    }

    @Test
    void saveNewExchangeRateTest(){
        //given
        given(exchangeRateRepository.save(any(ExchangeRate.class))).willReturn(exchangeRate);

        //when
        ExchangeRate savedExchangeRate = exchangeRateService.saveNewExchangeRate(new ExchangeRate());

        //then
        then(exchangeRateRepository).should().save(any(ExchangeRate.class));
        assertNotNull(savedExchangeRate);
    }

    @Test
    void findByCurrencyTest(){
        //given
        exchangeRate.setCurrencyFrom(Currency.USD);
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        exchangeRates.add(exchangeRate);
        given(exchangeRateRepository.findByCurrencyFrom(Currency.USD)).willReturn(exchangeRates);
        //when
        List<ExchangeRate> foundExchangeRates = exchangeRateService.findByCurrency(Currency.USD);
        //then
        then(exchangeRateRepository).should().findByCurrencyFrom(any(Currency.class));
        assertEquals(1, foundExchangeRates.size());
        assertEquals(Currency.USD, foundExchangeRates.get(0).getCurrencyFrom());
    }

    @Test
    void findAverageExchangeRateTest(){
        //given
        ExchangeRate exchangeRate = new ExchangeRate();
        ExchangeRate exchangeRate2 = new ExchangeRate();
        exchangeRate.setExchangeRateValue(1.0);
        exchangeRate2.setExchangeRateValue(5.0);
        Double testResult = (exchangeRate.getExchangeRateValue() + exchangeRate2.getExchangeRateValue()) / 2;

        List<ExchangeRate> listOfValues = new ArrayList<>();
        listOfValues.add(exchangeRate);
        listOfValues.add(exchangeRate2);

        //when
        Double valueFromMethod = exchangeRateService.findAverageExchangeRate(listOfValues);

        //then
        assertEquals(testResult, valueFromMethod);
    }

    @Test
    void getNewExchangeRate(){
        //given
        String path ="C:/Users/Matus/Desktop/box/test.xlsx";
        exchangeRate.setDate("08/15/2020");
        exchangeRate.setCurrencyFrom(Currency.USD);
        exchangeRate.setCurrencyTo(Currency.ZAR);
        exchangeRate.setExchangeRateValue(16.60530);

        //when
        List<ExchangeRate> exchangeRatesFromFile = exchangeRateService.getNewExchangeRate(path);
        assertEquals(1, exchangeRatesFromFile.size());
        assertEquals(exchangeRate, exchangeRatesFromFile.get(0));
    }

    @Test
    void setManualExchangeRateTest(){
        //given
        ExchangeRate testExchangeRate = new ExchangeRate();
        testExchangeRate.setDate("12/12/2021");
        testExchangeRate.setCurrencyFrom(Currency.USD);
        testExchangeRate.setCurrencyTo(Currency.ZAR);
        //when
        ExchangeRate changedExchangeRate =
                exchangeRateService.setManualExchangeRate(exchangeRate,"12/12/2021", Currency.USD, Currency.ZAR);
        //then
        assertEquals(testExchangeRate, changedExchangeRate);
    }

    @Test
    void deleteExchangeRateByIdTest(){
        //when
        exchangeRateService.deleteExchangeRate("1");
        //then
        then(exchangeRateRepository).should(times(1)).deleteById("1");
    }

    @Test
    void getExhcangeRateFromLastMonth(){
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        ExchangeRate testRate = new ExchangeRate();
        ExchangeRate testRate2 = new ExchangeRate();
        testRate.setDate("08/12/2021");
        testRate2.setDate("07/12/2021");
        exchangeRates.add(testRate);
        exchangeRates.add(testRate2);

        //when
        List<ExchangeRate> rates = exchangeRateService.getExchangeRateForLastMonth(exchangeRates);

        //then
        assertEquals(1, rates.size());


    }

    //otestuj este aj medody ktore failnu
}