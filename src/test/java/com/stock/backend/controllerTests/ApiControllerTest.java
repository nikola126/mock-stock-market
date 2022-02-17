package com.stock.backend.controllerTests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.stock.backend.controllers.ApiController;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.exceptions.ApiExceptions.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class ApiControllerTest {
    @Spy
    @InjectMocks
    ApiController apiController;

    QuoteRequestDTO quoteRequestDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        quoteRequestDTO = new QuoteRequestDTO();
    }

    @Test
    void getQuoteAfterApiSuccess() throws ApiException {
        doReturn(new QuoteDTO()).when(apiController).apiQuote(any());
        apiController.getQuote(quoteRequestDTO);

        verify(apiController).apiQuote(quoteRequestDTO);
    }

    @Test
    void throwExceptionAfterApiException() throws ApiException {
        doThrow(ResponseStatusException.class).when(apiController).apiQuote(any());

        assertThrows(ResponseStatusException.class, () -> {
            apiController.getQuote(quoteRequestDTO);
        });
    }

}
