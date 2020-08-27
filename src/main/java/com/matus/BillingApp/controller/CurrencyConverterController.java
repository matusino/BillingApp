package com.matus.BillingApp.controller;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.service.ExchangeRateService;
import com.matus.BillingApp.util.CurrencyConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.matus.BillingApp.controller.ExchangeRateController.*;

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
            return REDIRECT_CURRENCY_CONVERTER_USD_TO_ZAR;
        }else {
            for (ExchangeRate exchangeRate : exchangeRateDB){
                if(exchangeRate.getCurrencyFrom().equals(Currency.USD)){
                    if(!converter.getAmount().isEmpty()){
                        double amount = Double.parseDouble(converter.getAmount());
                        double roundOff = (double) Math.round((amount*exchangeRate.getExchangeRateValue()) * 100) / 100;//extract to method
                        List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.USD));
                        Double average =  exchangeRateService.findAverageExchangeRate(historyOfRates);
                        String result = String.valueOf(roundOff);
                        model.addAttribute("exchangeRate", exchangeRate);
                        model.addAttribute("converter", new CurrencyConverter(converter.getAmount(), converter.getExchangeRateValue(), result));
                        model.addAttribute("lisOfRates", historyOfRates);
                        model.addAttribute("average", average);
                        model.addAttribute("currency", Currency.USD);
                        return CONVERT_FROM_USD_TO_ZAR;
                    }else {
                        return REDIRECT_CURRENCY_CONVERTER_USD_TO_ZAR;
                    }
                }
            }
        }
        return CONVERT_FROM_USD_TO_ZAR;
    }

    @RequestMapping(value = "/convert/zar-to-usd")
    public String convertZarToUsd(@ModelAttribute CurrencyConverter converter, Model model){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> exchangeRateDB = exchangeRateService.findByDate(dtf.format(now));
        if(exchangeRateDB.isEmpty()){
            return REDIRECT_CURRENCY_CONVERTER_ZAR_TO_USD;
        }else {
            for (ExchangeRate exchangeRate : exchangeRateDB){
                if(exchangeRate.getCurrencyFrom().equals(Currency.ZAR)){
                    if(!converter.getAmount().isEmpty()){
                        double amount = Double.parseDouble(converter.getAmount());
                        double roundOff = (double) Math.round((amount*exchangeRate.getExchangeRateValue()) * 100) / 100;
                        List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.ZAR));
                        Double average =  exchangeRateService.findAverageExchangeRate(historyOfRates);
                        String result = String.valueOf(roundOff);
                        model.addAttribute("exchangeRate", exchangeRate);
                        model.addAttribute("converter", new CurrencyConverter(converter.getAmount(), converter.getExchangeRateValue(), result));
                        model.addAttribute("lisOfRates", historyOfRates);
                        model.addAttribute("average", average);
                        model.addAttribute("currency", Currency.ZAR);
                        return CONVERT_FROM_ZAR_TO_USD;
                    }else {
                        return REDIRECT_CURRENCY_CONVERTER_ZAR_TO_USD;
                    }
                }
            }
        }
        return CONVERT_FROM_ZAR_TO_USD;
    }
}
