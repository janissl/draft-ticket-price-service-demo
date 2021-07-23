package com.github.janissl.draftticket.conf;

import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR ||
                response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            throw new ExternalServiceUnavailableException("Failed to get data from an external server");
        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            throw new ExternalServiceUnavailableException("Failed to get data from a client");
        }
    }
}
