package com.coinChangeService.Coin.Change.Service.dao;

import com.coinChangeService.Coin.Change.Service.model.Currency;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ChangeCoinDao {

    void populateCoinCombinationMap(Map<Currency, List<Map<Integer, Integer>>> coinsCombination);

    void populateValidBills(List<Currency> bills);

    List<Map<Integer, Integer>> getSortedCombinationsForCurrency(Currency currency);

    boolean validateBill(int billAmount);

}
