package com.coinChangeService.Coin.Change.Service.service;

import com.coinChangeService.Coin.Change.Service.dao.AvailableCoinDao;
import com.coinChangeService.Coin.Change.Service.dao.ChangeCoinDao;
import com.coinChangeService.Coin.Change.Service.dao.CurrencyDao;
import com.coinChangeService.Coin.Change.Service.exception.BillNotSupportedException;
import com.coinChangeService.Coin.Change.Service.exception.OutOfMoneyException;
import com.coinChangeService.Coin.Change.Service.model.AvailableCoin;
import com.coinChangeService.Coin.Change.Service.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ChangeCoinServiceImpl implements ChangeCoinService {

    private final ChangeCoinDao changeCoinDao;
    private final CurrencyDao currencyDao;
    private final AvailableCoinDao availableCoinDao;

    public ChangeCoinServiceImpl(ChangeCoinDao changeCoinDao, CurrencyDao currencyDao,
                                 AvailableCoinDao availableCoinDao) {
        this.changeCoinDao = changeCoinDao;
        this.currencyDao = currencyDao;
        this.availableCoinDao = availableCoinDao;
    }

    @Override
    public Map<Integer, Integer> getLeastCoinChange(int billValue) {
        Currency currency = getCurrencyForBillAmount(billValue);
        List<Map<Integer, Integer>> coinCombinations = changeCoinDao.getSortedCombinationsForCurrency(currency);
        List<AvailableCoin> availableCoins = availableCoinDao.getAvailableCoins();

        for (Map<Integer, Integer> combination : coinCombinations) {
            // Check the first combination that can be satisfied with the current state of coins
            if (isSatisfied(availableCoins, combination)) {
                log.info("Found a valid combination : {} for currency: {}", combination.toString(), billValue);
                availableCoinDao.updateCoins(combination);
                log.info("Updated initial coins count {} ", availableCoinDao.getAvailableCoins());
                return combination;
            }
        }
        throw new OutOfMoneyException("Machine doesn't have enough coin-change");
    }

    private Currency getCurrencyForBillAmount(int billValue) {
        if (!changeCoinDao.validateBill(billValue)) {
            throw new BillNotSupportedException("The requested Bill amount : " + billValue + " is not supported");
        }
        return currencyDao.findCurrencyByValue(billValue);
    }

    /**
     *
     */
    private boolean isSatisfied(List<AvailableCoin> availableCoins, Map<Integer, Integer> combination) {
        for (Integer key : combination.keySet()) {
            Optional<AvailableCoin> associatedCoin = availableCoins.stream()
                    .filter(coin -> coin.getCurrency().getValue() == key)
                    .findFirst();
            if (!associatedCoin.isPresent()) {
                // should never happen
                return false;
            }
            if (associatedCoin.get().getRemaining() < combination.get(key))
                return false;
        }
        return true;
    }
}
