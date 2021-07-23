package com.github.janissl.draftticket.conf;

import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


@Service
public class ExternalWebApiService {
    @Value("${taxes.url}")
    private String taxesUrl;

    @Value("${basePrice.url}")
    private String basePriceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public ExternalWebApiService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public Double getBasePrice(String destination) throws ExternalServiceUnavailableException {
        try {
            return restTemplate.getForObject(basePriceUrl + destination, Double.class);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceUnavailableException(String.format(
                    "Failed to get a base price for the '%s' destination. Reason: %s", destination, e.getMessage()));
        }
    }

    public Double[] getApplicableTaxes() throws ExternalServiceUnavailableException {
        try{
            return restTemplate.getForObject(taxesUrl, Double[].class);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceUnavailableException(String.format(
                    "Failed to get applicable taxes. Reason: %s", e.getMessage()));
        }
    }
}
