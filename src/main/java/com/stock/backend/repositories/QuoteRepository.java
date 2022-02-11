package com.stock.backend.repositories;

import java.util.Optional;

import com.stock.backend.models.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    Optional<Quote> getBySymbol(String symbol);
}
