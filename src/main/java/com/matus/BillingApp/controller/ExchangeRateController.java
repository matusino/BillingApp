package com.matus.BillingApp.controller;

import com.matus.BillingApp.domain.Currency;
import com.matus.BillingApp.domain.ExchangeRate;
import com.matus.BillingApp.repository.ExchangeRateRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExchangeRateController {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateController(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @RequestMapping(value = "/")
    public String test(){
        ExchangeRate exchangeRate = new ExchangeRate("dfdf","fdfdfd", Currency.USD);
        exchangeRateRepository.save(exchangeRate);

        return "main";
    }
}
