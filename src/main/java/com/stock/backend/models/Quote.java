package com.stock.backend.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

import com.stock.backend.dtos.QuoteDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quotes")
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column
    private Double change;

    @Column
    private Double changePercent;

    @Column
    private String companyName;

    @Column(unique = true)
    private String symbol;

    @Column
    private String currency;

    @Column
    private Double latestPrice;

    @Column
    private Date latestUpdate = new Date(System.currentTimeMillis());

    public void mapFromDTO(QuoteDTO quoteDTO) {
        this.change = quoteDTO.getChange();
        this.changePercent = quoteDTO.getChangePercent();
        this.companyName = quoteDTO.getCompanyName();
        this.latestPrice = quoteDTO.getLatestPrice();
    }
}
