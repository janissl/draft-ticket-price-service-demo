package com.github.janissl.draftticket.controller;

import com.github.janissl.draftticket.exception.DataUnavailableException;
import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import com.github.janissl.draftticket.exception.InvalidUserInputException;
import com.github.janissl.draftticket.model.DraftPrice;
import com.github.janissl.draftticket.model.Passenger;
import com.github.janissl.draftticket.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
class DraftTicketController {
    private final PriceService priceService;

    @Autowired
    public DraftTicketController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping(path="/draftprice", consumes="application/json")
    public DraftPrice getDraftTicket(@RequestBody List<Passenger> passengerList)
            throws InvalidUserInputException, ExternalServiceUnavailableException {
        return priceService.getDraftPrice(passengerList);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataUnavailableException.class)
    public ResponseEntity<Map<String, String>> returnDataUnavailableErrorMessage(DataUnavailableException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Data Unavailable");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> returnExternalServiceUnavailableErrorMessage(ExternalServiceUnavailableException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "External Service Unavailable");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InvalidUserInputException.class)
    public ResponseEntity<Map<String, String>> returnInvalidUserInputErrorMessage(InvalidUserInputException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid Input From Client");
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
