package com.coinChangeService.Coin.Change.Service.dao;

import com.coinChangeService.Coin.Change.Service.model.AvailableCoin;
import com.coinChangeService.Coin.Change.Service.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class AvailableCoinDaoImpl implements AvailableCoinDao {

    private List<AvailableCoin> availableCoins;

    @Override
    public void populateAvailableCoins(List<AvailableCoin> availableCoins) {
        log.info("Initializing ChangeCoinDao with initial Coins Count");
        this.availableCoins = new ArrayList<>(availableCoins);
    }

    @Override
    public List<AvailableCoin> getAvailableCoins() {
        return availableCoins;
    }

    @Override
    public void updateCoins(Map<Currency, Integer> deductCoinMap) {
        for (Currency currency : deductCoinMap.keySet()) {
            availableCoins.stream()
                    .filter(coin -> coin.getCurrency().equals(currency))
                    .forEach(coin -> {
                        coin.setRemaining(coin.getRemaining() - deductCoinMap.get(currency));
                    });
        }
    }
}
