package com.github.janissl.draftticket.controller;

import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import com.github.janissl.draftticket.exception.InvalidUserInputException;
import com.github.janissl.draftticket.model.DraftPrice;
import com.github.janissl.draftticket.model.Passenger;
import com.github.janissl.draftticket.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class DraftTicketController {
    private final PriceService priceService;

    @Autowired
    public DraftTicketController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping(path="/getDraftPrice", consumes="application/json")
    public DraftPrice getDraftTicket(@RequestBody List<Passenger> passengerList)
            throws InvalidUserInputException, ExternalServiceUnavailableException {
        return priceService.getDraftPrice(passengerList);
    }
}
