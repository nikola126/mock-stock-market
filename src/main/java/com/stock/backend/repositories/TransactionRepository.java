package com.stock.backend.repositories;

import java.sql.Date;
import java.util.List;

import com.stock.backend.models.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> getAllByUserId(Long userId);

    List<Transaction> getAllByAction(Integer action);

    List<Transaction> getAllByDate(Date date);

    @EntityGraph(value = "stock.id", type = EntityGraph.EntityGraphType.FETCH)
    List<Transaction> getAllByStockId(Integer id);
}
