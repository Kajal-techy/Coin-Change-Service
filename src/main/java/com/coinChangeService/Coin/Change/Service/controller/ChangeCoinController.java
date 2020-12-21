package com.coinChangeService.Coin.Change.Service.controller;

import com.coinChangeService.Coin.Change.Service.service.ChangeCoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1")
public class ChangeCoinController {

    private final ChangeCoinService changeCoinService;

    public ChangeCoinController(ChangeCoinService changeCoinService) {
        this.changeCoinService = changeCoinService;
    }

    @GetMapping("/change-coins")
    public ResponseEntity<Map> getAllPossibleCoins(@RequestParam Integer billAmount) {
        Map<Integer, Integer> fetchedCoins = changeCoinService.getLeastCoinChange(billAmount);
        if (fetchedCoins != null && !fetchedCoins.isEmpty())
            return ResponseEntity.ok().body(fetchedCoins);
        throw new OutOfMemoryError("Machine doesn't have enough coins");
    }
}
