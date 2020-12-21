package com.coinChangeService.Coin.Change.Service.controller;

import com.coinChangeService.Coin.Change.Service.model.CoinChangeResponse;
import com.coinChangeService.Coin.Change.Service.model.Currency;
import com.coinChangeService.Coin.Change.Service.service.ChangeCoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class ChangeCoinController {

    private final ChangeCoinService changeCoinService;

    public ChangeCoinController(ChangeCoinService changeCoinService) {
        this.changeCoinService = changeCoinService;
    }

    @GetMapping("/change-coins")
    public ResponseEntity<List<CoinChangeResponse>> getAllPossibleCoins(@RequestParam Integer billAmount) {
        Map<Currency, Integer> fetchedCoins = changeCoinService.getLeastCoinChange(billAmount);
        if (fetchedCoins != null && !fetchedCoins.isEmpty()) {
            List<CoinChangeResponse> coinChangeResponseList = fetchedCoins.keySet().stream()
                    .filter(x -> fetchedCoins.get(x) != 0)
                    .map(x -> CoinChangeResponse.builder()
                            .currency(x)
                            .numberOfCoins(fetchedCoins.get(x))
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(coinChangeResponseList);
        }

        throw new OutOfMemoryError("Machine doesn't have enough coins");
    }
}
