package com.coinChangeService.Coin.Change.Service.model;

public class CurrencyFactory {

    static public Currency createDollarBillFromValue(int value) {
        return Currency.builder().value(value).type(CurrencyType.BILL).unit(CurrencyUnit.DOLLAR).build();
    }

    static public Currency createCentCoinFromValue(int value) {
        return Currency.builder().value(value).type(CurrencyType.COIN).unit(CurrencyUnit.CENT).build();
    }
}
