package com.github.janissl.draftticket.service;

import com.github.janissl.draftticket.conf.ExternalWebApiService;
import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import com.github.janissl.draftticket.exception.InvalidUserInputException;
import com.github.janissl.draftticket.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class PriceService {
    private final ExternalWebApiService externalWebApiService;

    public PriceService(ExternalWebApiService externalWebApiService) {
        this.externalWebApiService = externalWebApiService;
    }

    public List<Double> getApplicableTaxes() throws ExternalServiceUnavailableException, InvalidUserInputException {
        List<Double> applicableTaxes = Arrays.asList(externalWebApiService.getApplicableTaxes());

        for (double rate: applicableTaxes) {
            if (rate < 0.0) {
                throw new InvalidUserInputException("Tax rates cannot be negative.");
            }
        }

        return applicableTaxes;
    }

    private static void validateTicketData(Ticket ticket) throws InvalidUserInputException {
        List<String> dataValidationMessages = new ArrayList<>();

        if (null == ticket.getTicketType()) {
            dataValidationMessages.add("No passenger type has been set for a ticket.");
        }

        if (ticket.getBasePrice() < 0.0) {
            dataValidationMessages.add("Ticket base price cannot be negative.");
        }

        if (ticket.getCount() < 1) {
            dataValidationMessages.add("Number of items for a ticket cannot be negative.");
        }

        if (!dataValidationMessages.isEmpty()) {
            throw new InvalidUserInputException(String.join(" ", dataValidationMessages));
        }
    }

    public static Double getTicketNetPrice(Ticket ticket) throws InvalidUserInputException {
        Double percentageDiscountChild = 50.0;
        Double percentageLuggage = 30.0;
        Double netPrice;

        validateTicketData(ticket);

        switch (ticket.getTicketType()) {
            case ADULT:
                netPrice = ticket.getBasePrice();
                break;
            case CHILD:
                netPrice = ticket.getBasePrice() * percentageDiscountChild / 100.0;
                break;
            case LUGGAGE:
                netPrice = ticket.getCount() * ticket.getBasePrice() * percentageLuggage / 100.0;
                break;
            default:
                throw new InvalidUserInputException(String.format(
                            "Incorrect passenger type of the ticket: %s.",
                            ticket.getTicketType().toString()));
        }

        return netPrice;
    }

    public static Double calculateTotalTax(Double netPrice, List<Double> applicableTaxes) {
        List<Double> taxAmounts = new ArrayList<>();

        for (Double rate: applicableTaxes){
            taxAmounts.add(netPrice * rate / 100.0);
        }

        return taxAmounts.stream().reduce(0.0, Double::sum);
    }

    public static BigDecimal getTotalPriceOfTicket(Double netPrice, List<Double> applicableTaxes) {
        double totalTax = calculateTotalTax(netPrice, applicableTaxes);
        double totalPrice = netPrice + totalTax;
        return BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getTotalPriceOfDraftTicket(List<Ticket> tickets) {
        return tickets.stream().map(Ticket::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public DraftPrice getDraftPrice(List<Passenger> passengers) throws ExternalServiceUnavailableException, InvalidUserInputException {
        TicketService ticketService = new TicketService(externalWebApiService);
        List<Double> applicableTaxes = Arrays.asList(externalWebApiService.getApplicableTaxes());

        DraftTicket draftTicket = ticketService.createDraftTicket(passengers, applicableTaxes);
        DraftPrice draftPrice = new DraftPrice(draftTicket);
        draftPrice.setTotalPrice(getTotalPriceOfDraftTicket(draftTicket.getTickets()));

        return draftPrice;
    }
}
