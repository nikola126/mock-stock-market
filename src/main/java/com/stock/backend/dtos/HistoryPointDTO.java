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
public class HistoryPointDTO {
    private Double open;
    private Double close;
    private Double low;
    private Double high;
    private Double change;
    private Double changePercent;
    private String date;
    private Long updated;
}
