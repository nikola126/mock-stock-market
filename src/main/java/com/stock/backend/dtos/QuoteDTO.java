package com.stock.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties
public class QuoteDTO {
    private Double avgTotalVolume;
    private Double change;
    private Double changePercent;
    private String companyName;
    private String symbol;
    private String currency;
    private Double latestPrice;
    private Double week52High;
    private Double week52Low;
}
