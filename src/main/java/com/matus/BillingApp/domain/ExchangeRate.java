package com.matus.BillingApp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ExchangeRate")
public class ExchangeRate {

    @Id
    private String id;
    private String exchangeRateValue;

    @Indexed(direction = IndexDirection.ASCENDING )
    private String date;
    private Currency currency;

    public ExchangeRate(String exchangeRateValue, String date, Currency currency) {
        this.exchangeRateValue = exchangeRateValue;
        this.date = date;
        this.currency = currency;
    }

    public ExchangeRate() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExchangeRateValue() {
        return exchangeRateValue;
    }

    public void setExchangeRateValue(String exchangeRateValue) {
        this.exchangeRateValue = exchangeRateValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
