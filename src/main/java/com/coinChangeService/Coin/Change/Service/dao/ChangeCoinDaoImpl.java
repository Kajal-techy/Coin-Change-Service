package com.coinChangeService.Coin.Change.Service.dao;

import com.coinChangeService.Coin.Change.Service.model.Currency;
import com.coinChangeService.Coin.Change.Service.model.CurrencyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ChangeCoinDaoImpl implements ChangeCoinDao {

    private Map<Currency, List<Map<Currency, Integer>>> coinsCombination;
    private List<Currency> validBills;

    @Override
    public void populateCoinCombinationMap(Map<Currency, List<Map<Currency, Integer>>> coinsCombination) {
        log.info("Initializing ChangeCoinDao Coins-Combinations for different Bills");
        this.coinsCombination = new HashMap<>(coinsCombination);
    }

    @Override
    public void populateValidBills(List<Currency> bills) {
        validBills = bills;
    }

    @Override
    public List<Map<Currency, Integer>> getSortedCombinationsForCurrency(Currency currency) {
        return coinsCombination.get(currency);
    }

    @Override
    public boolean validateBill(int billAmount) {
        return validBills.contains(CurrencyFactory.createDollarBillFromValue(billAmount));
    }
}
