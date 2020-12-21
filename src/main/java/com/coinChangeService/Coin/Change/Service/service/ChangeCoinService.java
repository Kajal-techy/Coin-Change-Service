package com.coinChangeService.Coin.Change.Service.service;

import com.coinChangeService.Coin.Change.Service.model.Currency;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ChangeCoinService {

    Map<Currency, Integer> getLeastCoinChange(int amount);

    Map<Currency, Integer> getMostCoinChange(int amount);
}
