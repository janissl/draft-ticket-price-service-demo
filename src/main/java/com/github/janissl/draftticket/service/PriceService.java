package com.github.janissl.draftticket.service;

import com.github.janissl.draftticket.conf.ExternalWebApiService;
import com.github.janissl.draftticket.exception.DataUnavailableException;
import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import com.github.janissl.draftticket.exception.InvalidUserInputException;
import com.github.janissl.draftticket.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PriceService {

    private final ExternalWebApiService externalWebApiService;

    @Autowired
    public PriceService(ExternalWebApiService externalWebApiService) {
        this.externalWebApiService = externalWebApiService;
    }

    public DraftTicket createDraftTicket(List<Passenger> passengers)
            throws ExternalServiceUnavailableException, DataUnavailableException, InvalidUserInputException {

        for (Passenger passenger: passengers) {
            if (null == passenger.getPassengerType() ||
                    null == passenger.getItemsOfLuggage() ||
                    null == passenger.getDestination()) {
                throw new InvalidUserInputException("Invalid passenger data received from the user");
            }

            if (passenger.getItemsOfLuggage() < 0) {
                throw new InvalidUserInputException("Number of luggage items cannot be negative");
            }
        }

        Map<String, Double> basePriceCache = new HashMap<>();

        List<Ticket> draftTickets = new ArrayList<>();

        for (Passenger passenger: passengers) {
            String destination = passenger.getDestination();
            Double basePrice;

            if (basePriceCache.containsKey(destination)) {
                basePrice = basePriceCache.get(destination);
            } else {
                basePrice = externalWebApiService.getBasePrice(destination);
                basePriceCache.put(destination, basePrice);
            }

            if (basePrice < 0.0) {
                throw new DataUnavailableException(String.format("Destination '%s' is not available", destination));
            }

            draftTickets.addAll(generateTicketsForPassenger(basePrice, passenger));
        }

        List<Double> applicableTaxes = Arrays.asList(externalWebApiService.getApplicableTaxes());

        return new DraftTicket(draftTickets, applicableTaxes);
    }

    private List<Ticket> generateTicketsForPassenger(Double basePrice, Passenger passenger) {

        List<Ticket> tickets = new ArrayList<>(Collections.singletonList(
                new Ticket(TicketType.valueOf(passenger.getPassengerType().toString()), basePrice)));

        Integer itemsOfLuggage = passenger.getItemsOfLuggage();
        tickets.add(new Ticket(TicketType.LUGGAGE, basePrice, itemsOfLuggage));

        return tickets;
    }
}
