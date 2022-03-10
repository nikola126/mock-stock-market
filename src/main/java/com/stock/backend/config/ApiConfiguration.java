package com.stock.backend.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class ApiConfiguration {
    private final String defaultToken = "pk_7f87328a7eb7494fb3ded2a0e2bdfcf2";
    private final String apiQuotePath = "https://cloud.iexapis.com/stable/stock/";
    private final String apiHistoricalPath = "https://cloud.iexapis.com/stable/stock/";
}
