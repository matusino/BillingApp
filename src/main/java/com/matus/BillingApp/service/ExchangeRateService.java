package com.matus.BillingApp.service;

import com.matus.BillingApp.domain.ExchangeRate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ExchangeRateService {

    ExchangeRate saveNewExchangeRate(ExchangeRate exchangeRate);

    List<ExchangeRate> getNewExchangeRate(String path);

    List<ExchangeRate> findByDate(String date);

}
