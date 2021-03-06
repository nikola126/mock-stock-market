package com.stock.backend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuoteRequestDTO {
    private Long userId;
    private String symbol;
    private String token;
}
