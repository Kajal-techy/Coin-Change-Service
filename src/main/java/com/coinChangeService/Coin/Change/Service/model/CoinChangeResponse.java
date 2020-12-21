package com.coinChangeService.Coin.Change.Service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoinChangeResponse {

    Currency currency;
    int numberOfCoins;
}
