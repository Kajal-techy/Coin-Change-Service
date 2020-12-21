package com.coinChangeService.Coin.Change.Service.dao;

import com.coinChangeService.Coin.Change.Service.model.Currency;
import org.springframework.stereotype.Repository;

@Repository
public class CurrencyDaoImpl implements CurrencyDao {

    @Override
    public Currency findCurrencyByValue(int value) {
        return Currency.builder().value(value).build();
    }
}
