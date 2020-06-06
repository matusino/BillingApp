package com.matus.BillingApp.util;

public class CurrencyConverter {

    private String amount;
    private String exchangeRateValue;
    private String result;

    public CurrencyConverter() {
    }

    public CurrencyConverter(String amount, String exchangeRateValue, String result) {
        this.amount = amount;
        this.exchangeRateValue = exchangeRateValue;
        this.result = result;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExchangeRateValue() {
        return exchangeRateValue;
    }

    public void setExchangeRateValue(String exchangeRateValue) {
        this.exchangeRateValue = exchangeRateValue;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
