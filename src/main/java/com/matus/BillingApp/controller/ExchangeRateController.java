package com.matus.BillingApp.controller;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.service.ExchangeRateService;
import com.matus.BillingApp.util.CurrencyConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
                if(exchangeRate.getCurrencyFrom().equals(Currency.ZAR)){
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

    @RequestMapping(value = "/history-of-rates/ZAR")
    public String adjustHistoryOfExchangeRateForZar(Model model){
        List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.ZAR));
        Double average =  exchangeRateService.findAverageExchangeRate(historyOfRates);

        model.addAttribute("currency", Currency.ZAR);
        model.addAttribute("average", average);
        model.addAttribute("rates", historyOfRates);
        model.addAttribute("exchangeRate", new ExchangeRate());

        return "historyofexchangerates";
    }

    @RequestMapping(value = "/history-of-rates/USD")
    public String adjustHistoryOfExchangeRateForUSD(Model model){
        List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.USD));
        Double average =  exchangeRateService.findAverageExchangeRate(historyOfRates);

        model.addAttribute("currency", Currency.USD);
        model.addAttribute("average", average);
        model.addAttribute("rates", historyOfRates);
        model.addAttribute("exchangeRate", new ExchangeRate());

        return "historyofexchangerates";
    }

    @RequestMapping(value = "/add-new-er/{currency}")
    public String addExhangeRateZar(@ModelAttribute ExchangeRate exchangeRate, @PathVariable Currency currency) {
//alebo to dat len dojedneho controleru tu hore to by mozno bolo lepsie
// dorob dalsie checky teda len skopiruj tie co uz mame, ci uz su pozuiti ten datum alebo ne a dat nejkau hlasku ak je
        //dorobit tu este lahky crud na delete

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");

        List<ExchangeRate> exchangeRateFrombB;
        try {
            exchangeRateFrombB = exchangeRateService.findByDate(myFormat.format(fromUser.parse(exchangeRate.getDate())));
            List<ExchangeRate> usdFromDB = exchangeRateFrombB.stream().filter( er -> er.getCurrencyFrom().equals(Currency.USD)).collect(Collectors.toList());
            List<ExchangeRate> zarFromDB = exchangeRateFrombB.stream().filter( er -> er.getCurrencyFrom().equals(Currency.ZAR)).collect(Collectors.toList());
            if(exchangeRate.getExchangeRateValue() != null && !exchangeRate.getDate().isEmpty()) {//check if object is not empty
                    try {
                        if(zarFromDB.isEmpty() && currency.equals(Currency.ZAR)){
                            String reformatedDate = myFormat.format(fromUser.parse(exchangeRate.getDate()));//reformat date
                            exchangeRate.setDate(reformatedDate);
                            exchangeRate.setCurrencyFrom(Currency.ZAR);
                            exchangeRate.setCurrencyTo(Currency.USD);
                            exchangeRateService.saveNewExchangeRate(exchangeRate);
                            return "redirect:/history-of-rates/ZAR";
                        }else if(usdFromDB.isEmpty() && currency.equals(Currency.USD)) {
                            String reformatedDate = myFormat.format(fromUser.parse(exchangeRate.getDate()));
                            exchangeRate.setDate(reformatedDate);
                            exchangeRate.setCurrencyFrom(Currency.USD);
                            exchangeRate.setCurrencyTo(Currency.ZAR);
                            exchangeRateService.saveNewExchangeRate(exchangeRate);
                            return "redirect:/history-of-rates/USD";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(currency.equals(Currency.ZAR)){
            return "redirect:/history-of-rates/ZAR";
        }else
            return "redirect:/history-of-rates/USD";
    }
}
