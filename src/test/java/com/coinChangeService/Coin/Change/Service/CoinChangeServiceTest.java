package com.coinChangeService.Coin.Change.Service;

import com.coinChangeService.Coin.Change.Service.dao.AvailableCoinDao;
import com.coinChangeService.Coin.Change.Service.dao.ChangeCoinDao;
import com.coinChangeService.Coin.Change.Service.exception.BillNotSupportedException;
import com.coinChangeService.Coin.Change.Service.exception.OutOfMoneyException;
import com.coinChangeService.Coin.Change.Service.model.AvailableCoin;
import com.coinChangeService.Coin.Change.Service.model.Currency;
import com.coinChangeService.Coin.Change.Service.model.CurrencyFactory;
import com.coinChangeService.Coin.Change.Service.service.ChangeCoinServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoinChangeServiceTest {

    Map<Currency, Integer> coinsCountMap;
    List<Map<Currency, Integer>> coinCombinations;
    List<AvailableCoin> availableCoins;
    List<AvailableCoin> finishedCoins;
    List<AvailableCoin> updatedCoins = new ArrayList<>();
    Map<Currency, Integer> coinsMapForCurrency1;
    Map<Currency, Integer> coinsMapForCurrency2;
    Map<Currency, Integer> coinsMapForCurrency;
    Currency currency_1_cent = CurrencyFactory.createCentCoinFromValue(1);
    Currency currency_25_cent = CurrencyFactory.createCentCoinFromValue(25);

    @Mock
    private ChangeCoinDao changeCoinDao;

    @Mock
    private AvailableCoinDao availableCoinDao;

    @Mock
    private CurrencyFactory currencyFactory;

    @InjectMocks
    private ChangeCoinServiceImpl changeCoinServiceImpl;

    @Before
    public void preRequisiteTestData() {
        populateCoinCountMap();
        currency_1_cent = CurrencyFactory.createCentCoinFromValue(1);
        currency_25_cent = CurrencyFactory.createCentCoinFromValue(25);
        coinsMapForCurrency1 = new HashMap<>();
        coinsMapForCurrency2 = new HashMap<>();
        coinsMapForCurrency1.put(currency_1_cent, 100);
        coinsMapForCurrency2.put(currency_25_cent, 4);
        coinCombinations = new ArrayList<>();
        coinCombinations.add(coinsMapForCurrency2);
        coinCombinations.add(coinsMapForCurrency1);
        AvailableCoin availableCoin1 = populateAvailableCoins();
        populateUpdatedCoins(availableCoin1);
        populateFinishedCoins();
    }

    private AvailableCoin populateAvailableCoins() {
        // Create available coins list
        AvailableCoin availableCoin1 = AvailableCoin.builder().currency(currency_1_cent)
                .remaining(100)
                .build();
        AvailableCoin availableCoin2 = AvailableCoin.builder().currency(currency_25_cent)
                .remaining(100)
                .build();

        availableCoins = new ArrayList<>();
        availableCoins.add(availableCoin1);
        availableCoins.add(availableCoin2);
        return availableCoin1;
    }

    private void populateFinishedCoins() {
        // Finished Coins
        AvailableCoin finishedCoin1 = AvailableCoin.builder().currency(currency_1_cent)
                .remaining(0)
                .build();
        AvailableCoin finishedCoin2 = AvailableCoin.builder().currency(currency_25_cent)
                .remaining(0)
                .build();
        finishedCoins = new ArrayList<>();
        finishedCoins.add(finishedCoin1);
        finishedCoins.add(finishedCoin2);
    }

    private void populateUpdatedCoins(AvailableCoin availableCoin1) {
        // Updated coins
        AvailableCoin updatedCoin2 = AvailableCoin.builder().currency(currency_25_cent)
                .remaining(96)
                .build();
        updatedCoins.add(availableCoin1);
        updatedCoins.add(updatedCoin2);
    }

    private void populateCoinCountMap() {
        coinsCountMap = new HashMap<>();
        coinsCountMap.put(CurrencyFactory.createCentCoinFromValue(1), 100);
        coinsCountMap.put(CurrencyFactory.createCentCoinFromValue(5), 100);
        coinsCountMap.put(CurrencyFactory.createCentCoinFromValue(10), 100);
        coinsCountMap.put(CurrencyFactory.createCentCoinFromValue(25), 100);
    }

    @Test
    public void changeCoinIfCoinsAreAvailable() {
        when(changeCoinDao.getSortedCombinationsForCurrency(CurrencyFactory.createDollarBillFromValue(1))).thenReturn(coinCombinations);
        when(availableCoinDao.getAvailableCoins()).thenReturn(availableCoins);
        when(changeCoinDao.validateBill(1)).thenReturn(true);
        Map<Currency, Integer> fetchedCoins = changeCoinServiceImpl.getLeastCoinChange(1);
        assertEquals(coinsMapForCurrency2, fetchedCoins);
    }

    @Test
    public void verifyThatCoinsShouldBeUpdatedSuccessfully() {
        when(changeCoinDao.getSortedCombinationsForCurrency(CurrencyFactory.createDollarBillFromValue(1))).thenReturn(coinCombinations);
        when(availableCoinDao.getAvailableCoins()).thenReturn(availableCoins);
        when(changeCoinDao.validateBill(1)).thenReturn(true);
        Map<Currency, Integer> fetchedCoins = changeCoinServiceImpl.getLeastCoinChange(1);
        assertEquals(coinsMapForCurrency2, fetchedCoins);
    }

    @Test(expected = BillNotSupportedException.class)
    public void throwErrorIfBillIsNotSupported() {
        when(changeCoinDao.getSortedCombinationsForCurrency(CurrencyFactory.createDollarBillFromValue(15))).thenThrow(BillNotSupportedException.class);
        Map<Currency, Integer> fetchedCoins = changeCoinServiceImpl.getLeastCoinChange(15);
    }

    @Test(expected = OutOfMoneyException.class)
    public void throwErrorIfCoinsAreNotAvailable() {
        when(changeCoinDao.getSortedCombinationsForCurrency(CurrencyFactory.createDollarBillFromValue(20))).thenReturn(coinCombinations);
        when(availableCoinDao.getAvailableCoins()).thenReturn(finishedCoins);
        when(changeCoinDao.validateBill(20)).thenReturn(true);
        Map<Currency, Integer> fetchedCoins = changeCoinServiceImpl.getLeastCoinChange(20);
    }

    @Test
    public void changeCoinWithDifferentCombination() {
        List<Map<Currency, Integer>> coinsCombinationsFor2$ = new ArrayList<>();
        coinsMapForCurrency = new HashMap<>();
        coinsMapForCurrency.put(currency_1_cent, 100);
        coinsMapForCurrency.put(currency_25_cent, 4);
        coinsCombinationsFor2$.add(coinsMapForCurrency);
        when(changeCoinDao.getSortedCombinationsForCurrency(CurrencyFactory.createDollarBillFromValue(2))).thenReturn(coinsCombinationsFor2$);
        when(availableCoinDao.getAvailableCoins()).thenReturn(availableCoins);
        when(changeCoinDao.validateBill(2)).thenReturn(true);
        Map<Currency, Integer> fetchedCoins = changeCoinServiceImpl.getLeastCoinChange(2);
        assertEquals(coinsMapForCurrency, fetchedCoins);
    }
}
