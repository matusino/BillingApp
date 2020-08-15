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

import static com.matus.BillingApp.controller.ExchangeRateController.CONVERT_FROM_USD_TO_ZAR;
import static com.matus.BillingApp.controller.ExchangeRateController.CONVERT_FROM_ZAR_TO_USD;

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
            return "redirect:/currency-converter/usd-to-zar";
        }else {
            for (ExchangeRate exchangeRate : exchangeRateDB){
                if(exchangeRate.getCurrencyFrom().equals(Currency.USD)){
                    if(!converter.getAmount().isEmpty()){
                        double amount = Double.parseDouble(converter.getAmount());
                        double roundOff = (double) Math.round((amount*exchangeRate.getExchangeRateValue()) * 100) / 100;
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
                        return "redirect:/currency-converter/usd-to-zar";
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
            return "redirect:/currency-converter/zar-to-usd";
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
                        return "redirect:/currency-converter/zar-to-usd";
                    }
                }
            }
        }
        return CONVERT_FROM_ZAR_TO_USD;
    }
}
