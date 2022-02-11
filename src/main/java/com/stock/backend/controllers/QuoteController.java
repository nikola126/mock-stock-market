package com.stock.backend.controllers;

import com.stock.backend.config.ApiConfiguration;
import com.stock.backend.dtos.QuoteDTO;
import com.stock.backend.dtos.QuoteRequestDTO;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/quotes", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuoteController {
    private final ApiConfiguration configuration = new ApiConfiguration();

    @GetMapping(path = "/quote")
    public QuoteDTO getQuote(@RequestBody QuoteRequestDTO quoteRequestDTO) {
        return apiQuote(quoteRequestDTO);
    }

    @Bean
    private RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    private RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    public QuoteDTO apiQuote(QuoteRequestDTO quoteRequestDTO) {
        RestTemplate restTemplate = restTemplate(restTemplateBuilder());
        StringBuilder sb = new StringBuilder();
        sb.append(configuration.getAPI_QUOTE_PATH());
        sb.append(quoteRequestDTO.getSymbol());
        sb.append("/quote?token=");

        if (quoteRequestDTO.getToken() != null)
            sb.append(quoteRequestDTO.getToken());
        else
            sb.append(configuration.getDEFAULT_TOKEN());

        return restTemplate.getForObject(
            sb.toString(),
            QuoteDTO.class);
    }

}
