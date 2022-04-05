package com.stock.backend.repositories;

import java.sql.Date;
import java.util.List;

import com.stock.backend.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository
    extends JpaRepository<Transaction, Long>, PagingAndSortingRepository<Transaction, Long> {
    Page<Transaction> getAllByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM transactions WHERE user_id = :userId AND action IN :actions", nativeQuery = true)
    Page<Transaction> getAllByUserIdByAction(@Param("userId") Long userId,
                                             @Param("actions") List<Integer> actions,
                                             Pageable pageable);

    List<Transaction> getAllByAction(Integer action);

    List<Transaction> getAllByDate(Date date);

    @EntityGraph(value = "stock.id", type = EntityGraph.EntityGraphType.FETCH)
    List<Transaction> getAllByStockId(Integer id);

    @Query(value = "SELECT " +
        "SUM(" +
        "CASE action " +
        "WHEN 1 THEN (price * shares) " +
        "WHEN 0 THEN -(price * shares) " +
        "ELSE 0 " +
        "END) " +
        "AS total_return " +
        "FROM transactions " +
        "WHERE user_id = :userId " +
        "AND company_id = :companyId", nativeQuery = true)
    Double getTotalCost(@Param("userId") Long userId, @Param("companyId") Long companyId);
}
