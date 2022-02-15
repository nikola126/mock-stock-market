package com.stock.backend.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

import com.stock.backend.dtos.StockDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "stocks")
@ToString
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String symbol;

    @Column
    private String name;

    @Column
    private Double price;

    @Column
    private Date lastUpdate;

    public Stock(String symbol, String name, Double price, Date lastUpdate) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.lastUpdate = lastUpdate;
    }

    public StockDTO mapToDTO() {
        StockDTO stockDTO = new StockDTO();

        stockDTO.setSymbol(this.getSymbol());
        stockDTO.setName(this.getName());
        stockDTO.setPrice(this.getPrice());
        stockDTO.setLastUpdate(this.getLastUpdate());

        return stockDTO;
    }
}
