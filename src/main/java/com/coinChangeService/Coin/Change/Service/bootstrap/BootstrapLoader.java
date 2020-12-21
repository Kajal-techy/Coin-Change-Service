package com.coinChangeService.Coin.Change.Service.bootstrap;

import com.coinChangeService.Coin.Change.Service.dao.AvailableCoinDao;
import com.coinChangeService.Coin.Change.Service.dao.ChangeCoinDao;
import com.coinChangeService.Coin.Change.Service.model.AvailableCoin;
import com.coinChangeService.Coin.Change.Service.model.Currency;
import com.coinChangeService.Coin.Change.Service.model.CurrencyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Loads the application and bootstrap Dao with data
 * Potentially can be moved to database in later implementation
 */
@Component
@Slf4j
public class BootstrapLoader implements ApplicationRunner {

    private final ChangeCoinDao changeCoinDao;
    private final AvailableCoinDao availableCoinDao;

    public BootstrapLoader(ChangeCoinDao changeCoinDao, AvailableCoinDao availableCoinDao) {
        this.changeCoinDao = changeCoinDao;
        this.availableCoinDao = availableCoinDao;
    }

    @Value("${coins.count}")
    private Integer MAX_COINS_COUNTS;

    @Value("#{'${supported.bills}'.split(',')}")
    private List<Integer> validBillList;

    @Value("#{'${supported.coins}'.split(',')}")
    private List<Integer> allowedCoinsForExchange;

    List<Map<Currency, Integer>> billCombinations;

    @Override
    public void run(ApplicationArguments args) {
        List<AvailableCoin> availableCoins = new ArrayList<>();
        Map<Currency, List<Map<Currency, Integer>>> coinCombinationsMap = new HashMap<>();

        populateCoinsCount(availableCoins, MAX_COINS_COUNTS);
        List<Currency> validBills = populateValidBills();
        List<Currency> coinChangeList = availableCoins.stream()
                .map(AvailableCoin::getCurrency)
                .collect(Collectors.toList());

        createCombinationsForBills(coinCombinationsMap, validBills, coinChangeList);
        availableCoinDao.populateAvailableCoins(availableCoins);
        changeCoinDao.populateCoinCombinationMap(coinCombinationsMap);
        changeCoinDao.populateValidBills(validBills);
    }

    /**
     * Creates combinations for various supported bills, based on the supported coins
     *
     * @param coinCombinationsMap  : The Map that contains bills to its combination map
     * @param supportedBills           : List of Supported bills
     * @param supportedCoins       : List of supported coins
     */
    private void createCombinationsForBills(Map<Currency, List<Map<Currency, Integer>>> coinCombinationsMap,
                                            List<Currency> supportedBills, List<Currency> supportedCoins) {
        log.info("Starting computing combinations of all the bills");
        for (Currency currency : supportedBills) {
            log.info("\tCreating combinations for bill : {}", currency.getValue() * 100);

            // Re-initialize the billCombinationList
            billCombinations = new ArrayList<>();
            populateBillCombinations(supportedCoins, new HashMap<>(), 0, currency.getValue() * 100);

            // Sort the map based on the increasing total-number of coins for the combinations
            // Assuming everything is coin and is of cent unit, so just comparing the values
            List<Map<Currency, Integer>> sortedCombinations = billCombinations.stream()
                    .sorted((x, y) -> Integer.compare(
                            x.values().stream().mapToInt(e -> e).sum(),
                            y.values().stream().mapToInt(e -> e).sum()))
                    .collect(Collectors.toList());
            coinCombinationsMap.put(currency, sortedCombinations);
            log.info("\tFinished combinations for {}", currency.getValue() * 100);
        }

        log.info("Finished Computing combinations of all the bills");
    }

    /**
     * This method will populate all the valid bills in List
     *
     * @return
     */
    private List<Currency> populateValidBills() {
        List<Currency> validBills = new ArrayList<>();
        validBillList.forEach((validBill) -> validBills.add(CurrencyFactory.createDollarBillFromValue(validBill)));
        return validBills;
    }

    /**
     * This method will initial max count for all the available coins
     * which will help us to decide how many times we cwn use one coin
     *
     * @param availableCoins : It will provide the list of all the available valid coins
     * @param coinFreqMax    : Coins max available frequency
     */
    private void populateCoinsCount(List<AvailableCoin> availableCoins, int coinFreqMax) {

        allowedCoinsForExchange.forEach((allowedCoinsForExchange) -> availableCoins.add(AvailableCoin.builder()
                .currency(CurrencyFactory.createCentCoinFromValue(allowedCoinsForExchange))
                .remaining(coinFreqMax)
                .build()));
    }

    /**
     * This method is providing all the possible combinations for the provided
     * bill
     *
     * @param supportedCoins : List of all the allowed coins which is use for the method
     *                       to provide all the possible combinations
     * @param tempMap        : This map will store all each possible combination for given currency
     * @param start          : Position it starts the array
     * @param sum            : It will help us to determine whether the combination is valid or not
     */
    private void populateBillCombinations(List<Currency> supportedCoins, Map<Currency, Integer> tempMap, int start,
                                          int sum) {
        if (sum < 0)
            return;
        else if (sum == 0) {
            billCombinations.add(new LinkedHashMap<>(tempMap));
        } else {
            for (int i = start; i < supportedCoins.size(); i++) {
                Currency coinToConsider = supportedCoins.get(i);
                boolean canAddCoin = checkAndAddToMap(tempMap, coinToConsider);
                if (!canAddCoin)
                    continue;
                populateBillCombinations(supportedCoins, tempMap, i, sum - coinToConsider.getValue());
                removeFreqCount(tempMap, coinToConsider);
            }
        }
    }

    /**
     * This method will check whether it is possible to take one more coin from existing one and if
     * possible then it will update the coins coint in the tempMap
     *
     * @param tempMap        : It will keep one valid combination of a given currency at a time
     * @param coinValueToAdd : Coin which we want to add in the combination
     * @return
     */
    private boolean checkAndAddToMap(Map<Currency, Integer> tempMap, Currency coinValueToAdd) {
        Integer numberOfCoins = tempMap.get(coinValueToAdd);
        if (numberOfCoins != null) {
            if (numberOfCoins + 1 > MAX_COINS_COUNTS) {
                return false;
            }
            tempMap.put(coinValueToAdd, numberOfCoins + 1);
        } else {
            tempMap.put(coinValueToAdd, 1);
        }
        return true;
    }

    /**
     * This method will reduce the coin coin by 1 as in the populateBillCombinations()
     * method to find the possible combinations for the given currency we are using backtracking(consider/not consider)
     *
     * @param tempMap      : It will keep one valid combination of a given currency at a time
     * @param coinToReduce : The coin whose frequency we want to reduce by 1
     */
    void removeFreqCount(Map<Currency, Integer> tempMap, Currency coinToReduce) {
        Integer numberOfCoins = tempMap.get(coinToReduce);
        tempMap.put(coinToReduce, numberOfCoins - 1);
    }
}
