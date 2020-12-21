package com.coinChangeService.Coin.Change.Service.dao;

import com.coinChangeService.Coin.Change.Service.model.AvailableCoin;
import com.coinChangeService.Coin.Change.Service.model.Currency;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AvailableCoinDao {

    void populateAvailableCoins(List<AvailableCoin> availableCoins);

    List<AvailableCoin> getAvailableCoins();

    void updateCoins(Map<Currency, Integer> deductCoinMap);
}
