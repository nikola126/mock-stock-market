package com.stock.backend.dtos;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionSummaryDTO {
    private Long userId;
    private List<Integer> actions;
}
