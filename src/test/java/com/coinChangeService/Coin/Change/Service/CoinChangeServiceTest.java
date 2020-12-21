package com.coinChangeService.Coin.Change.Service;

import com.coinChangeService.Coin.Change.Service.dao.ChangeCoinDao;
import com.coinChangeService.Coin.Change.Service.model.Currency;
import com.coinChangeService.Coin.Change.Service.service.ChangeCoinServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CoinChangeServiceTest {

    Map<Currency, Integer> coinsCountMap;
    List<Map<Integer, Integer>> coinCombinations;

    @Mock
    private ChangeCoinDao changeCoinDao;

    @InjectMocks
    private ChangeCoinServiceImpl changeCoinServiceImpl;

    @Before
    public void preRequisiteTestData() {
        coinsCountMap = new TreeMap<>();
        coinsCountMap.put(Currency.builder().value(1).build(), 100);
        coinsCountMap.put(Currency.builder().value(5).build(), 100);
        coinsCountMap.put(Currency.builder().value(25).build(), 100);
        coinsCountMap.put(Currency.builder().value(50).build(), 100);


    }

    @Test
    public void changeCoinIfCoinsAreAvailable() {

        when(changeCoinDao.getCurrentCoinsCount()).thenReturn(coinsCountMap);
        when(changeCoinDao.getSortedCombinationsForCurrency(Currency.builder().value(1).build())).thenReturn(coinsCountMap);
    /*    Map<Currency, Integer> fetchedCoins = changeCoinServiceImpl.getAllPossibleCoins(2);
        assertEquals("", fetchedCoins); */
    }

    @Test
    public void changeCoinIfCoinsAreNotAvailable() {

    }
}
