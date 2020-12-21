package com.coinChangeService.Coin.Change.Service.config;

import com.coinChangeService.Coin.Change.Service.dao.AvailableCoinDao;
import com.coinChangeService.Coin.Change.Service.dao.ChangeCoinDao;
import com.coinChangeService.Coin.Change.Service.model.AvailableCoin;
import com.coinChangeService.Coin.Change.Service.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final ChangeCoinDao changeCoinDao;
    private final AvailableCoinDao availableCoinDao;

    public DataLoader(ChangeCoinDao changeCoinDao, AvailableCoinDao availableCoinDao) {
        this.changeCoinDao = changeCoinDao;
        this.availableCoinDao = availableCoinDao;
    }

    @Value("${coins.count}")
    private Integer MAX_COINS_COUNTS;

    List<Map<Integer, Integer>> billCombinations;

    @Override
    public void run(ApplicationArguments args) {
        List<AvailableCoin> availableCoins = new ArrayList<>();
        Map<Currency, List<Map<Integer, Integer>>> coinCombinationsMap = new HashMap<>();

        int coinFreqMax = 100;
        populateCoinsCount(availableCoins, coinFreqMax);
        List<Currency> validBills = populateValidBills();

        // Create a coins list
        List<Integer> coinChangeList = availableCoins.stream()
                .map(x -> x.getCurrency().getValue())
                .collect(Collectors.toList());

        log.info("Starting computing combinations of all the bills");
        for (Currency currency : validBills) {
            log.info("\tCreating combinations for bill : {}", currency.getValue() * 100);

            // Re-initialize the billCombinationList
            billCombinations = new ArrayList<>();
            populateBillCombinations(coinChangeList, new HashMap<>(), 0, currency.getValue() * 100, coinFreqMax);

            // Sort the map based on the increasing total-number of coins for the combinations
            List<Map<Integer, Integer>> sortedCombinations = billCombinations.stream()
                    .sorted((x, y) -> Integer.compare(
                            x.values().stream().mapToInt(e -> e).sum(),
                            y.values().stream().mapToInt(e -> e).sum()))
                    .collect(Collectors.toList());
            coinCombinationsMap.put(Currency.builder().value(currency.getValue()).build(), sortedCombinations);
            log.info("\tFinished combinations for {}", currency.getValue() * 100);
        }

        log.info("Finished Computing combinations of all the bills");
        availableCoinDao.populateAvailableCoins(availableCoins);
        changeCoinDao.populateCoinCombinationMap(coinCombinationsMap);
        changeCoinDao.populateValidBills(validBills);
    }

    /**
     *
     */
    private List<Currency> populateValidBills() {
        List<Currency> validBills = new ArrayList<>();
        validBills.add(Currency.builder().value(1).build());
        validBills.add(Currency.builder().value(2).build());
        validBills.add(Currency.builder().value(5).build());
        validBills.add(Currency.builder().value(10).build());
        validBills.add(Currency.builder().value(20).build());
        validBills.add(Currency.builder().value(50).build());
        validBills.add(Currency.builder().value(100).build());
        return validBills;
    }

    /**
     *
     */
    private void populateCoinsCount(List<AvailableCoin> availableCoins, int coinFreqMax) {
        availableCoins.add(AvailableCoin.builder()
                .currency(Currency.builder().value(1).build())
                .remaining(coinFreqMax)
                .build());

        availableCoins.add(AvailableCoin.builder()
                .currency(Currency.builder().value(5).build())
                .remaining(coinFreqMax)
                .build());

        availableCoins.add(AvailableCoin.builder()
                .currency(Currency.builder().value(10).build())
                .remaining(coinFreqMax)
                .build());

        availableCoins.add(AvailableCoin.builder()
                .currency(Currency.builder().value(25).build())
                .remaining(coinFreqMax)
                .build());
    }

    /**
     *
     */
    private void populateBillCombinations(List<Integer> moneyList, Map<Integer, Integer> tempMap, int start,
                                          int sum, int coinFreqMax) {
        if (sum < 0)
            return;
        else if (sum == 0) {
            billCombinations.add(new LinkedHashMap<>(tempMap));
        } else {
            for (int i = start; i < moneyList.size(); i++) {
                int coinToConsider = moneyList.get(i);
                boolean canAddCoin = checkAndAddToMap(tempMap, coinToConsider, coinFreqMax);
                if (!canAddCoin)
                    continue;
                populateBillCombinations(moneyList, tempMap, i, sum - moneyList.get(i), coinFreqMax);
                removeFreqCount(tempMap, coinToConsider);
            }
        }
    }

    /**
     *
     */
    private boolean checkAndAddToMap(Map<Integer, Integer> tempMap, Integer coinToAdd, int coinFreqMax) {
        Integer numberOfCoins = tempMap.get(coinToAdd);
        if (numberOfCoins != null) {
            if (numberOfCoins + 1 > coinFreqMax) {
                return false;
            }
            tempMap.put(coinToAdd, tempMap.get(coinToAdd) + 1);
        } else {
            tempMap.put(coinToAdd, 1);
        }
        return true;
    }

    /**
     *
     */
    void removeFreqCount(Map<Integer, Integer> tempMap, Integer coinToReduce) {
        Integer numberOfCoins = tempMap.get(coinToReduce);
        tempMap.put(coinToReduce, numberOfCoins - 1);
    }
}
