package com.stock.backend.controllers;

import com.stock.backend.config.ApiConfiguration;
import com.stock.backend.dtos.HistoryDTO;
import com.stock.backend.dtos.HistoryPointDTO;
import com.stock.backend.dtos.HistoryRequestDTO;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import com.stock.backend.exceptions.ApiExceptions.ApiException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiController {
    private final ApiConfiguration apiConfiguration = new ApiConfiguration();

    @GetMapping(path = "/quote")
    public QuoteDTO getQuote(@RequestBody QuoteRequestDTO quoteRequestDTO) {
        QuoteDTO quoteDTO = new QuoteDTO();

        try {
            quoteDTO = apiQuote(quoteRequestDTO);
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return quoteDTO;
    }

    @Bean
    private RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    private RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    public QuoteDTO apiQuote(QuoteRequestDTO quoteRequestDTO) throws ApiException {
        RestTemplate restTemplate = restTemplate(restTemplateBuilder());
        StringBuilder sb = new StringBuilder();
        sb.append(apiConfiguration.getApiQuotePath());
        sb.append(quoteRequestDTO.getSymbol());
        sb.append("/quote?token=");

        if (quoteRequestDTO.getToken() != null) {
            sb.append(quoteRequestDTO.getToken());
        } else {
            sb.append(apiConfiguration.getDefaultToken());
        }

        QuoteDTO quoteDTO = new QuoteDTO();
        try {
            quoteDTO = restTemplate.getForObject(sb.toString(), QuoteDTO.class);
            return quoteDTO;
        } catch (HttpClientErrorException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public HistoryDTO apiHistory(HistoryRequestDTO historicalRequestDTO) throws ApiException {
        RestTemplate restTemplate = restTemplate(restTemplateBuilder());
        StringBuilder sb = new StringBuilder();
        sb.append(apiConfiguration.getApiHistoricalPath());
        sb.append(historicalRequestDTO.getSymbol());

        sb.append("/chart/");
        sb.append(historicalRequestDTO.getRange());
        sb.append("?token=");
        sb.append(historicalRequestDTO.getApiToken());

        HistoryDTO historicalDataDTO = new HistoryDTO();
        try {
            ResponseEntity<HistoryPointDTO[]> response =
                restTemplate.getForEntity(sb.toString(), HistoryPointDTO[].class);
            HistoryPointDTO[] data = response.getBody();
            historicalDataDTO.setData(data);

            return historicalDataDTO;
        } catch (HttpClientErrorException e) {
            throw new ApiException((e.getMessage()));
        }
    }

}
