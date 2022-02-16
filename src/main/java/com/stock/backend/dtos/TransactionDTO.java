package com.stock.backend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionDTO {
    private Long userId;
    private String symbol;
    private String companyName;
    private String action;
    private Integer shares;
    private Double price;
    private Long date;
}
