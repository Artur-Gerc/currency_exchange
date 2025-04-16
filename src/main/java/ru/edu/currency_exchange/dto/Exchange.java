package ru.edu.currency_exchange.dto;

import ru.edu.currency_exchange.models.Currency;

import java.math.BigDecimal;

public class Exchange {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
    private BigDecimal rate;

    public Exchange() {
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Exchange{" +
                "baseCurrency=" + baseCurrency +
                ", targetCurrency=" + targetCurrency +
                ", amount=" + amount +
                ", convertedAmount=" + convertedAmount +
                ", rate=" + rate +
                '}';
    }
}
