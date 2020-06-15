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
import java.util.ArrayList;
import java.util.List;

@Controller
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @RequestMapping(value = "/")
    public String viewMainPage(){
        return "redirect:/currency-converter/usd-to-zar";
    }

    @RequestMapping(value = "/get-new-exchange")
    public String getTodaysExchangeRate(){
        //tu dat moznost userovi dat vlastnu adresu k filu...ooo je ale az nakonci uplne..
        String filePath = "C:/Users/Matus/Desktop/box/exchange_rates_to_USD.xlsx";
        List<ExchangeRate> todayRates = exchangeRateService.getNewExchangeRate(filePath);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> exchangeRateFrombB = exchangeRateService.findByDate(dtf.format(now));

        if(!todayRates.isEmpty()) {//check whether we have something in file
            if(exchangeRateFrombB.isEmpty()) {//check if ER with today date is already in DB
                for (ExchangeRate todayExchangeRate : todayRates) {
                    if (todayExchangeRate.getDate().equals(dtf.format(now))) {//check if date in excel is updated to today
                        exchangeRateService.saveNewExchangeRate(todayExchangeRate);
                    }
                }
            }
        }else {
            return "missingfile";
        }
        return "redirect:/currency-converter/usd-to-zar";
    }

    @RequestMapping(value = "/currency-converter/usd-to-zar")
    public String viewUsdToZar(Model model){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.USD));
        List<ExchangeRate> exchangeRateDB = exchangeRateService.findByDate(dtf.format(now));

        Double average =  exchangeRateService.findAverageExchangeRate(historyOfRates);

        if(exchangeRateDB.isEmpty()){
            model.addAttribute("today", now);
            model.addAttribute("exchangeRate", new ExchangeRate());
            model.addAttribute("converter", new CurrencyConverter());
            model.addAttribute("lisOfRates", historyOfRates);
            model.addAttribute("average", average);
            return "usdtozar";
        }else {
            for (ExchangeRate exchangeRate : exchangeRateDB){
                if(exchangeRate.getCurrencyFrom().equals(Currency.USD)){
                    model.addAttribute("exchangeRate", exchangeRate);
                    model.addAttribute("converter", new CurrencyConverter());
                    model.addAttribute("lisOfRates", historyOfRates);
                    model.addAttribute("average", average);
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

        List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.ZAR));
        List<ExchangeRate> exchangeRateDB = exchangeRateService.findByDate(dtf.format(now));

        Double average =  exchangeRateService.findAverageExchangeRate(historyOfRates);

        if(exchangeRateDB.isEmpty()){
            model.addAttribute("today", now);
            model.addAttribute("exchangeRate", new ExchangeRate());
            model.addAttribute("converter", new CurrencyConverter());
            model.addAttribute("lisOfRates", historyOfRates);
            model.addAttribute("average", average);
            return "zartousd";
        }else {
            for (ExchangeRate exchangeRate : exchangeRateDB){
                if(exchangeRate.getCurrencyFrom().equals(Currency.USD)){
                    model.addAttribute("exchangeRate", exchangeRate);
                    model.addAttribute("converter", new CurrencyConverter());
                    model.addAttribute("lisOfRates", historyOfRates);
                    model.addAttribute("average", average);
                    return "zartousd";
                }
            }
        }
        return "zartousd";
    }

//    @RequestMapping(value = "/.....")
//    public String adjustHistoryOfExchangeRate(){
//
//    }
}
