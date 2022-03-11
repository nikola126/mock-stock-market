package com.stock.backend.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class ApiConfiguration {
    private final String defaultToken = "pk_b6e3d012a5f24e6a8b86640b97423948";
    private final String apiQuotePath = "https://cloud.iexapis.com/stable/stock/";
}
