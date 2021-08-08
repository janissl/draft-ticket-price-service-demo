package com.github.janissl.draftticket.conf;

import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import com.github.janissl.draftticket.exception.InvalidUserInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;


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

    public Double getBasePrice(String destination) throws ExternalServiceUnavailableException, InvalidUserInputException {
        try {
            String destinationURL = ExternalServiceUrlBuilder.buildUrl(basePriceUrl, destination);
            return restTemplate.getForObject(destinationURL, Double.class);
        } catch (ResourceAccessException e) {
            throw new ExternalServiceUnavailableException(String.format(
                    "Failed to get a base price for the '%s' destination. Reason: %s", destination, e.getMessage()));
        } catch (MalformedURLException e) {
            throw new InvalidUserInputException(String.format(
                    "A malformed URL has occurred using %s as a base URL and %s as a destination",
                    basePriceUrl, destination));
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
