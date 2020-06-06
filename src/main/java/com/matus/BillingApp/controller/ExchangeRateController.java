package com.matus.BillingApp.controller;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.service.ExchangeRateService;
import com.matus.BillingApp.util.CurrencyConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @RequestMapping(value = "/main")
    public String viewMainPage(){
        return "main";
    }

    @RequestMapping(value = "/get-new-exchange")
    public String getTodaysExchangeRate(){
        //tu dat moznost userovi dat vlastnu adresu k filu...ooo je ale az nakonci uplne..
        String filePath = "C:/Users/Matus/Desktop/box/exchange_rates_to_USD.xlsx";
        List<ExchangeRate> todayRates = exchangeRateService.getNewExchangeRate(filePath);
        if(todayRates.size() > 0){
            for (ExchangeRate exchangeRate : todayRates){
                exchangeRateService.saveNewExchangeRate(exchangeRate);
            }
        }
        else {
            return "missingfile";
        }
        return "redirect:/currency-converter/usd-to-zar";
    }

    @RequestMapping(value = "/currency-converter/usd-to-zar")
    public String viewUsdToZar(Model model){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> exchangeRateDB = exchangeRateService.findByDate(dtf.format(now));
        if(exchangeRateDB.isEmpty()){
            return "main";
        }else {
            for (ExchangeRate exchangeRate : exchangeRateDB){
                if(exchangeRate.getCurrencyFrom().equals(Currency.USD)){
                    model.addAttribute("exchangeRate", exchangeRate);
                    model.addAttribute("converter", new CurrencyConverter());
                    return "usdtozar";
                }
            }
        }
        return "usdtozar";
    }

    @RequestMapping(value = "/currency-converter/zar-to-usd")
    public String viewZarToUsd(Model model) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> exchangeRateDB = exchangeRateService.findByDate(dtf.format(now));
        for (ExchangeRate exchangeRate : exchangeRateDB) {
            if (exchangeRate.getCurrencyFrom().equals(Currency.ZAR)) {
                model.addAttribute("exchangeRate", exchangeRate);
                model.addAttribute("converter", new CurrencyConverter());
                return "zartousd";
            }
        }
        return "zartousd";
    }
}
