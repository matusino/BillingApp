package com.matus.BillingApp.controller;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.service.ExchangeRateService;
import com.matus.BillingApp.util.CurrencyConverter;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ExchangeRateController {

    public static final String MISSING_INPUT_FILE = "missingfile";
    public static final String CONVERT_FROM_USD_TO_ZAR = "usdtozar";
    public static final String CONVERT_FROM_ZAR_TO_USD = "zartousd";
    public static final String REDIRECT_HISTORY_ZAR = "redirect:/history-of-rates/ZAR";
    public static final String REDIRECT_HISTORY_USD = "redirect:/history-of-rates/USD";
    public static final String REDIRECT_CURRENCY_CONVERTER_USD_TO_ZAR = "redirect:/currency-converter/usd-to-zar";
    public static final String REDIRECT_CURRENCY_CONVERTER_ZAR_TO_USD = "redirect:/currency-converter/zar-to-usd";

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @RequestMapping(value = "/")
    public String viewMainPage() {
        return REDIRECT_CURRENCY_CONVERTER_USD_TO_ZAR;
    }

    @RequestMapping(value = "/get-new-exchange")
    public String getTodaysExchangeRate(@RequestParam(value = "path", required = false) String path) {
        String filePath = path.replaceAll("\\\\", "/");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> todayRates = exchangeRateService.getNewExchangeRate(filePath);
        List<ExchangeRate> exchangeRateFrombB = exchangeRateService.findByDate(dtf.format(now));

        if (!todayRates.isEmpty()) {
            if (exchangeRateFrombB.isEmpty()) {//check if ER with today date is already in DB
                for (ExchangeRate todayExchangeRate : todayRates) {
                    if (todayExchangeRate.getDate().equals(dtf.format(now))) {//check if we have updated excel
                        exchangeRateService.saveNewExchangeRate(todayExchangeRate);
                    }else {
                        return MISSING_INPUT_FILE;
                    }
                }
            }
        } else {
            return MISSING_INPUT_FILE;
        }
        return REDIRECT_CURRENCY_CONVERTER_USD_TO_ZAR;
    }

    @RequestMapping(value = "/currency-converter/usd-to-zar")
    public String viewUsdToZar(Model model) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.USD));
        List<ExchangeRate> exchangeRateDB = exchangeRateService.findByDate(dtf.format(now));
        List<ExchangeRate> exchangeRatesDbUsd = exchangeRateDB
                .stream()
                .filter(e -> e.getCurrencyFrom().equals(Currency.USD))
                .collect(Collectors.toList());

        Double average = exchangeRateService.findAverageExchangeRate(historyOfRates);

        if (exchangeRatesDbUsd.isEmpty()) {
            model.addAttribute("today", now);
            model.addAttribute("exchangeRate", new ExchangeRate());
            model.addAttribute("converter", new CurrencyConverter());
            model.addAttribute("lisOfRates", historyOfRates);
            model.addAttribute("average", average);
            model.addAttribute("currency", Currency.USD);
            return CONVERT_FROM_USD_TO_ZAR;
        } else {
            for (ExchangeRate exchangeRate : exchangeRatesDbUsd) {
                if (exchangeRate.getCurrencyFrom().equals(Currency.USD)) {
                    model.addAttribute("exchangeRate", exchangeRate);
                    model.addAttribute("converter", new CurrencyConverter());
                    model.addAttribute("lisOfRates", historyOfRates);
                    model.addAttribute("average", average);
                    model.addAttribute("currency", Currency.USD);
                    return CONVERT_FROM_USD_TO_ZAR;
                }
            }
        }
        return CONVERT_FROM_USD_TO_ZAR;
    }

    @RequestMapping(value = "/currency-converter/zar-to-usd")
    public String viewZarToUsd(Model model) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime now = LocalDateTime.now();

        List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.ZAR));
        List<ExchangeRate> exchangeRateDB = exchangeRateService.findByDate(dtf.format(now));

        List<ExchangeRate> exchangeRatesDbZar = exchangeRateDB
                .stream()
                .filter(e -> e.getCurrencyFrom().equals(Currency.ZAR))
                .collect(Collectors.toList());

        Double average = exchangeRateService.findAverageExchangeRate(historyOfRates);

        if (exchangeRatesDbZar.isEmpty()) {
            model.addAttribute("today", now);
            model.addAttribute("exchangeRate", new ExchangeRate());
            model.addAttribute("converter", new CurrencyConverter());
            model.addAttribute("lisOfRates", historyOfRates);
            model.addAttribute("average", average);
            model.addAttribute("currency", Currency.ZAR);
            return CONVERT_FROM_ZAR_TO_USD;
        } else {
            for (ExchangeRate exchangeRate : exchangeRatesDbZar) {
                if (exchangeRate.getCurrencyFrom().equals(Currency.ZAR)) {
                    model.addAttribute("exchangeRate", exchangeRate);
                    model.addAttribute("converter", new CurrencyConverter());
                    model.addAttribute("lisOfRates", historyOfRates);
                    model.addAttribute("average", average);
                    model.addAttribute("currency", Currency.ZAR);

                    return CONVERT_FROM_ZAR_TO_USD;
                }
            }
        }
        return CONVERT_FROM_ZAR_TO_USD;
    }

    @RequestMapping(value = "/history-of-rates/{currency}")
    public String adjustHistoryOfExchangeRateForZar(@PathVariable Currency currency, Model model) {
        if(currency.equals(Currency.ZAR)){
            List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.ZAR));
            Double average = exchangeRateService.findAverageExchangeRate(historyOfRates);

            model.addAttribute("currency", Currency.ZAR);
            model.addAttribute("average", average);
            model.addAttribute("rates", historyOfRates);
            model.addAttribute("exchangeRate", new ExchangeRate());

            return "historyofexchangerates";
        }else {
            List<ExchangeRate> historyOfRates = exchangeRateService.getExchangeRateForLastMonth(exchangeRateService.findByCurrency(Currency.USD));
            Double average = exchangeRateService.findAverageExchangeRate(historyOfRates);

            model.addAttribute("currency", Currency.USD);
            model.addAttribute("average", average);
            model.addAttribute("rates", historyOfRates);
            model.addAttribute("exchangeRate", new ExchangeRate());

            return "historyofexchangerates";
        }
    }

    @RequestMapping(value = "/add-new-er/{currency}")
    public String addExhangeRateZar(@ModelAttribute ExchangeRate exchangeRate, @PathVariable Currency currency) {
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");

        List<ExchangeRate> exchangeRateFrombB;
        try {
            exchangeRateFrombB = exchangeRateService.findByDate(myFormat.format(fromUser.parse(exchangeRate.getDate())));
            List<ExchangeRate> usdFromDB = exchangeRateFrombB.stream()
                    .filter(er -> er.getCurrencyFrom().equals(Currency.USD))
                    .collect(Collectors.toList());

            List<ExchangeRate> zarFromDB = exchangeRateFrombB.stream()
                    .filter(er -> er.getCurrencyFrom().equals(Currency.ZAR))
                    .collect(Collectors.toList());

            if (exchangeRate.getExchangeRateValue() != null && !exchangeRate.getDate().isEmpty()) {
                try {
                    if (zarFromDB.isEmpty() && currency.equals(Currency.ZAR)) {
                        String reformattedDate = myFormat.format(fromUser.parse(exchangeRate.getDate()));
                        exchangeRateService.setManualExchangeRate(exchangeRate, reformattedDate, Currency.ZAR, Currency.USD);
                        exchangeRateService.saveNewExchangeRate(exchangeRate);
                        return REDIRECT_HISTORY_ZAR;
                    } else if (usdFromDB.isEmpty() && currency.equals(Currency.USD)) {
                        String reformattedDate = myFormat.format(fromUser.parse(exchangeRate.getDate()));
                        exchangeRateService.setManualExchangeRate(exchangeRate, reformattedDate, Currency.USD, Currency.ZAR);
                        exchangeRateService.saveNewExchangeRate(exchangeRate);
                        return REDIRECT_HISTORY_USD;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currency.equals(Currency.ZAR)) {
            return REDIRECT_HISTORY_ZAR;
        } else
            return REDIRECT_HISTORY_USD;
    }

    @RequestMapping(value = "/delete/exchange-rate/{currency}/{exchangeRateId}")
    public String deleteExchangeRate(@PathVariable String exchangeRateId, @PathVariable Currency currency) {
        exchangeRateService.deleteExchangeRate(exchangeRateId);
        if (currency.equals(Currency.USD)) {
            return REDIRECT_HISTORY_USD;
        } else
            return REDIRECT_HISTORY_ZAR;
    }

//    @RequestMapping(value = "/back")
//    public String backButtonForSpecificView(@PathVariable Currency currency) {
//        if (currency.equals(Currency.USD)) {
//            return REDIRECT_HISTORY_USD;
//        } else
//            return REDIRECT_HISTORY_ZAR;
//    }
}