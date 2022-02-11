package com.stock.backend.services;

import java.sql.Date;
import java.util.Optional;

import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.models.Quote;
import com.stock.backend.repositories.QuoteRepository;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {
    private final QuoteRepository quoteRepository;

    public QuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    public Quote getById(Long id) {
        return quoteRepository.getById(id);
    }

    public Optional<Quote> getBySymbol(String symbol) {
        return quoteRepository.getBySymbol(symbol);
    }

    // TODO Split this
    public Quote addOrUpdateQuote(QuoteDTO quoteDTO) {
        Optional<Quote> existingQuote = quoteRepository.getBySymbol(quoteDTO.getSymbol());

        if (existingQuote.isEmpty()) {
            Quote newQuote = new Quote();
            newQuote.mapFromDTO(quoteDTO);
            quoteRepository.save(newQuote);
            return newQuote;
        } else {
            existingQuote.get().mapFromDTO(quoteDTO);
            existingQuote.get().setLatestUpdate(new Date(System.currentTimeMillis()));
            return existingQuote.get();
        }
    }
}
