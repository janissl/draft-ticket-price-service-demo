package com.github.janissl.draftticket.service;

import com.github.janissl.draftticket.conf.ExternalWebApiService;
import com.github.janissl.draftticket.exception.DataUnavailableException;
import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import com.github.janissl.draftticket.exception.InvalidUserInputException;
import com.github.janissl.draftticket.model.DraftTicket;
import com.github.janissl.draftticket.model.Passenger;
import com.github.janissl.draftticket.model.Ticket;
import com.github.janissl.draftticket.model.TicketType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TicketService {
    private final ExternalWebApiService externalWebApiService;

    @Autowired
    public TicketService(ExternalWebApiService externalWebApiService) {
        this.externalWebApiService = externalWebApiService;
    }

    private void validatePassengerData(List<Passenger> passengers) throws InvalidUserInputException {
        List<String> dataValidationMessages = new ArrayList<>();

        for (Passenger passenger: passengers) {
            if (null == passenger.getPassengerType()) {
                dataValidationMessages.add("No passenger type received from the client.");
            }

            if (null == passenger.getItemsOfLuggage()) {
                dataValidationMessages.add("No number of luggage items received from the client.");
            } else if (passenger.getItemsOfLuggage() < 0) {
                dataValidationMessages.add("Number of luggage items cannot be negative.");
            }

            if (null == passenger.getDestination()) {
                dataValidationMessages.add("No destination received from the client.");
            }
        }

        if (!dataValidationMessages.isEmpty()) {
            throw new InvalidUserInputException(String.join(" ", dataValidationMessages));
        }
    }

    public Double getBasePriceForDestination(String destination, Map<String, Double> basePriceCache)
            throws ExternalServiceUnavailableException {

        Double basePrice;

        if (basePriceCache.containsKey(destination)) {
            basePrice = basePriceCache.get(destination);
        } else {
            basePrice = externalWebApiService.getBasePrice(destination);
            basePriceCache.put(destination, basePrice);
        }

        if (basePrice < 0.0) {
            basePriceCache.put(destination, basePrice);
            throw new DataUnavailableException(String.format("Destination '%s' is not available.", destination));
        }

        return basePrice;
    }

    private static List<Ticket> generateTicketsForPassenger(Double basePrice, Passenger passenger) {

        List<Ticket> tickets = new ArrayList<>(Collections.singletonList(
                new Ticket(TicketType.valueOf(passenger.getPassengerType().toString()), basePrice)));

        Integer itemsOfLuggage = passenger.getItemsOfLuggage();
        tickets.add(new Ticket(TicketType.LUGGAGE, basePrice, itemsOfLuggage));

        return tickets;
    }

    protected List<Ticket> generateTicketsForAllPassengers(List<Passenger> passengers)
            throws ExternalServiceUnavailableException {

        List<Ticket> tickets = new ArrayList<>();
        Map<String, Double> basePriceCache = new HashMap<>();

        for (Passenger passenger: passengers) {
            String destination = passenger.getDestination();
            Double basePrice = getBasePriceForDestination(destination, basePriceCache);
            tickets.addAll(TicketService.generateTicketsForPassenger(basePrice, passenger));
        }

        return tickets;
    }

    public DraftTicket createDraftTicket(List<Passenger> passengers, List<Double> applicableTaxes)
            throws ExternalServiceUnavailableException, DataUnavailableException, InvalidUserInputException {

        validatePassengerData(passengers);
        List<Ticket> tickets = generateTicketsForAllPassengers(passengers);

        return new DraftTicket(tickets, applicableTaxes);
    }
}
