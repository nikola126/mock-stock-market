package com.stock.backend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StockDTO {
    private String symbol;
    private String name;
    private Double price;
    private Long lastUpdate;
}
