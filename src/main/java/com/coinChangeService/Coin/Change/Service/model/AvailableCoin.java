package com.coinChangeService.Coin.Change.Service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableCoin {

    private Currency currency;
    private int remaining;
}
