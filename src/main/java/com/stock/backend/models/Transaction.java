package com.stock.backend.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.stock.backend.dtos.TransactionDTO;
import com.stock.backend.enums.Actions;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transactions")
@ToString
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Stock stock;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Actions action;

    @Column
    private Integer shares;

    @Column
    private Double value;

    @Column
    private Long date;

    public TransactionDTO mapToDTO() {
        System.out.println(this);

        TransactionDTO transactionDTO = new TransactionDTO();

        transactionDTO.setUserId(this.getUser().getId());
        transactionDTO.setSymbol(this.getStock().getSymbol());
        transactionDTO.setCompanyName(this.getStock().getName());
        transactionDTO.setAction(this.getAction().toString());
        transactionDTO.setShares(this.getShares());
        transactionDTO.setValue(this.getValue());
        transactionDTO.setDate(this.getDate());

        return transactionDTO;
    }
}
