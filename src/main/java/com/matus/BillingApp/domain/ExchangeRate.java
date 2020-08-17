package com.matus.BillingApp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Objects;

@Document(collection = "ExchangeRate")
public class ExchangeRate {

    @Id
    private String id;
    private Double exchangeRateValue;

    @Indexed(direction = IndexDirection.ASCENDING )
    @DateTimeFormat(pattern = "MM/dd/yyyy")
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

    public Double getExchangeRateValue() {
        return exchangeRateValue;
    }

    public void setExchangeRateValue(Double exchangeRateValue) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(exchangeRateValue, that.exchangeRateValue) &&
                Objects.equals(date, that.date) &&
                currencyFrom == that.currencyFrom &&
                currencyTo == that.currencyTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exchangeRateValue, date, currencyFrom, currencyTo);
    }
}
