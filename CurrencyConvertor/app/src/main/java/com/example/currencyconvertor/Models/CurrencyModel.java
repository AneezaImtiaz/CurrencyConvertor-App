package com.example.currencyconvertor.Models;

import java.io.Serializable;

public class CurrencyModel implements Serializable, Comparable<CurrencyModel> {

    public String Currency;
    public long  Amount;
    public double Rate;

    public CurrencyModel(String Currency, double Rate, long Amount)
    {
        this.Currency = Currency;
        this.Rate = Rate;
        this.Amount = Amount;
    }

    public int compareTo(CurrencyModel currency) {
        return Currency.compareTo(currency.Currency);
    }

    /********************************
     * Getters
     ********************************/
    public 	String getCurrency (){
        return this.Currency;
    }

    public double getRate (){
        return this.Rate;
    }

    public long getAmount (){
        return this.Amount;
    }


    /********************************
     * Setters
     ********************************/
    public void setCurrency (String Currency){
        this.Currency = Currency;
    }

    public void setRate (double Rate){ this.Rate = Rate; }

    public void setAmount (long Amount){
        this.Amount = Amount;
    }

}
