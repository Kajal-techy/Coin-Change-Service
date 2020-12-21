package com.coinChangeService.Coin.Change.Service.dao;

import com.coinChangeService.Coin.Change.Service.model.Currency;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyDao {

    Currency findCurrencyByValue(int value);
}
