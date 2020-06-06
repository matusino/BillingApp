package com.matus.BillingApp.controller;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.service.ExchangeRateService;
import com.matus.BillingApp.util.CurrencyConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class CurrencyConverterController {

    private final ExchangeRateService exchangeRateService;

    public CurrencyConverterController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @RequestMapping(value = "/convert/usd-to-zar")
    public String convertUsdToZar(@ModelAttribute CurrencyConverter converter, Model model){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> exchangeRateDB = exchangeRateService.findByDate(dtf.format(now));
        if(exchangeRateDB.isEmpty()){
            return "main";
        }else {
            for (ExchangeRate exchangeRate : exchangeRateDB){
                if(exchangeRate.getCurrencyFrom().equals(Currency.USD)){
                    Long amount = Long.parseLong(converter.getAmount());
                    double roundOff = (double) Math.round((amount*exchangeRate.getExchangeRateValue()) * 100) / 100;
                    String result = String.valueOf(roundOff);
                    model.addAttribute("exchangeRate", exchangeRate);
                    model.addAttribute("converter", new CurrencyConverter(converter.getAmount(), converter.getExchangeRateValue(), result));
                    return "usdtozar";
                }
            }
        }
        return "usdtozar";
    }

    @RequestMapping(value = "/convert/usd-to-zar")
    public String convertZarToUsd(@ModelAttribute CurrencyConverter converter, Model model){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> exchangeRateDB = exchangeRateService.findByDate(dtf.format(now));
        if(exchangeRateDB.isEmpty()){
            return "main";
        }else {
            for (ExchangeRate exchangeRate : exchangeRateDB){
                if(exchangeRate.getCurrencyFrom().equals(Currency.USD)){
                    Long amount = Long.parseLong(converter.getAmount());
                    double roundOff = (double) Math.round((amount*exchangeRate.getExchangeRateValue()) * 100) / 100;
                    String result = String.valueOf(roundOff);
                    model.addAttribute("exchangeRate", exchangeRate);
                    model.addAttribute("converter", new CurrencyConverter(converter.getAmount(), converter.getExchangeRateValue(), result));
                    return "zartousd";
                }
            }
        }
        return "zartousd";
    }

}
