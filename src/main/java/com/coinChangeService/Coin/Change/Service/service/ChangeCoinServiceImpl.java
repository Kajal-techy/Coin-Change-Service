package com.coinChangeService.Coin.Change.Service.service;

import com.coinChangeService.Coin.Change.Service.dao.AvailableCoinDao;
import com.coinChangeService.Coin.Change.Service.dao.ChangeCoinDao;
import com.coinChangeService.Coin.Change.Service.exception.BillNotSupportedException;
import com.coinChangeService.Coin.Change.Service.exception.OutOfMoneyException;
import com.coinChangeService.Coin.Change.Service.model.AvailableCoin;
import com.coinChangeService.Coin.Change.Service.model.Currency;
import com.coinChangeService.Coin.Change.Service.model.CurrencyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ChangeCoinServiceImpl implements ChangeCoinService {

    private final ChangeCoinDao changeCoinDao;
    private final AvailableCoinDao availableCoinDao;

    public ChangeCoinServiceImpl(ChangeCoinDao changeCoinDao,
                                 AvailableCoinDao availableCoinDao) {
        this.changeCoinDao = changeCoinDao;
        this.availableCoinDao = availableCoinDao;
    }

    /**
     * This method will provide the list of all the possible least coins for a given currency
     *
     * @param billValue : It is a bill -> for which user wants least coins from a machine
     * @return Map of Currency and number of coins to be returned as change
     */
    @Override
    public Map<Currency, Integer> getLeastCoinChange(int billValue) {
        Currency currency = getCurrencyForBillAmount(billValue);
        List<Map<Currency, Integer>> coinCombinations = changeCoinDao.getSortedCombinationsForCurrency(currency);
        List<AvailableCoin> availableCoins = availableCoinDao.getAvailableCoins();

        for (Map<Currency, Integer> combination : coinCombinations) {
            // Check the first combination that can be satisfied with the current state of coins
            if (isSatisfied(availableCoins, combination)) {
                log.info("Found a valid combination : {} for currency: {}", combination.toString(), billValue);
                availableCoinDao.updateCoins(combination);
                log.info("Updated AVAILABLE coins count {} ", availableCoinDao.getAvailableCoins());
                return combination;
            }
        }
        throw new OutOfMoneyException("Machine doesn't have enough coin-change");
    }

    /**
     * This method will provide the most coins list first
     *
     * @param billValue : It is a bill -> for which user wants most coins from a machine
     * @return Map of Currency and number of coins to be returned as change
     */
    @Override
    public Map<Currency, Integer> getMostCoinChange(int billValue) {
        Currency currency = getCurrencyForBillAmount(billValue);
        List<Map<Currency, Integer>> coinCombinations = changeCoinDao.getSortedCombinationsForCurrency(currency);
        // It will reverse the sorted coin list and in this way we would be able to fetch most coins list first
        List<Map<Currency, Integer>> mostCoinCombinations = new ArrayList<>(coinCombinations);
        Collections.reverse(mostCoinCombinations);
        List<AvailableCoin> availableCoins = availableCoinDao.getAvailableCoins();

        for (Map<Currency, Integer> combination : mostCoinCombinations) {
            // Check the first combination that can be satisfied with the current state of coins
            if (isSatisfied(availableCoins, combination)) {
                log.info("Found a valid combination : {} for currency: {}", combination.toString(), billValue);
                availableCoinDao.updateCoins(combination);
                log.info("Updated AVAILABLE coins count {} ", availableCoinDao.getAvailableCoins());
                return combination;
            }
        }
        throw new OutOfMoneyException("Machine doesn't have enough coin-change");
    }

    /**
     * This method will return the currency for a given billValue if the billValue would be valid then
     * it will return currency else will throw BillNotSupportedException
     *
     * @param billValue : It is a bill -> for which user wants least coins from a machine
     * @return Currency for the bill value
     */
    private Currency getCurrencyForBillAmount(int billValue) {
        if (!changeCoinDao.validateBill(billValue)) {
            throw new BillNotSupportedException("The requested Bill amount : " + billValue + " is not supported");
        }
        return CurrencyFactory.createDollarBillFromValue(billValue);
    }

    /**
     * This method will return true if given combination is possible with available coins in the machine
     * else machine will return false if its not possible
     */
    private boolean isSatisfied(List<AvailableCoin> availableCoins, Map<Currency, Integer> combination) {
        for (Currency currency : combination.keySet()) {
            Optional<AvailableCoin> associatedCoin = availableCoins.stream()
                    .filter(coin -> coin.getCurrency().equals(currency))
                    .findFirst();
            if (!associatedCoin.isPresent()) {
                // should never happen
                return false;
            }
            if (associatedCoin.get().getRemaining() < combination.get(currency))
                return false;
        }
        return true;
    }
}
