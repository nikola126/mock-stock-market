package com.stock.backend.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import java.sql.Date;

import com.stock.backend.dtos.StockDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String symbol;

    @Column
    private String name;

    @Column
    private Double price;

    @Column
    private Date lastUpdate;

    public StockDTO mapToDTO() {
        StockDTO stockDTO = new StockDTO();

        stockDTO.setSymbol(this.getSymbol());
        stockDTO.setName(this.getName());
        stockDTO.setPrice(this.getPrice());
        stockDTO.setLastUpdate(this.getLastUpdate());

        return stockDTO;
    }
}
