package com.coinChangeService.Coin.Change.Service.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ChangeCoinService {

    Map<Integer, Integer> getLeastCoinChange(int amount);
}
