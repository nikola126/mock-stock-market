package com.stock.backend.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class ApiConfiguration {
    private final String DEFAULT_TOKEN = "pk_7f87328a7eb7494fb3ded2a0e2bdfcf2";
    private final String API_QUOTE_PATH = "https://cloud.iexapis.com/stable/stock/";
}
