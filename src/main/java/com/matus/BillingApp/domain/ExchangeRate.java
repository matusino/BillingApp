package com.matus.BillingApp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ExchangeRate")
public class ExchangeRate {

    @Id
    private String id;
    private double exchangeRateValue;

    @Indexed(direction = IndexDirection.ASCENDING )
    private String date;

    private Currency currencyFrom;

    private Currency currencyTo;

    public ExchangeRate(String id, double exchangeRateValue, String date, Currency currencyFrom, Currency currencyTo) {
        this.id = id;
        this.exchangeRateValue = exchangeRateValue;
        this.date = date;
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
    }

    public ExchangeRate() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getExchangeRateValue() {
        return exchangeRateValue;
    }

    public void setExchangeRateValue(double exchangeRateValue) {
        this.exchangeRateValue = exchangeRateValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Currency getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(Currency currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public Currency getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(Currency currencyTo) {
        this.currencyTo = currencyTo;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id='" + id + '\'' +
                ", exchangeRateValue=" + exchangeRateValue +
                ", date='" + date + '\'' +
                ", currencyFrom=" + currencyFrom +
                ", currencyTo=" + currencyTo +
                '}';
    }
}
