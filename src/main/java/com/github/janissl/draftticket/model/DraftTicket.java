package com.github.janissl.draftticket.model;

import com.github.janissl.draftticket.exception.InvalidUserInputException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DraftTicket {
    private final List<Ticket> tickets;
    public DraftTicket(List<Ticket> ticketList, List<Double> applicableTaxes) throws InvalidUserInputException {
        for (Ticket ticket: ticketList) {
            Double price = ticket.getNetPrice();

            if (price < 0.0) {
                throw new InvalidUserInputException("Invalid passenger data received from the user");
            }

            Double totalPrice = price + calculateTotalTax(price, applicableTaxes);
            ticket.setTotalPrice(totalPrice);
        }
        this.tickets = ticketList;
    }

    public static Double calculateTotalTax(Double netPrice, List<Double> taxRates) {
        List<Double> taxAmounts = new ArrayList<>();

        for (Double rate: taxRates){
            taxAmounts.add(netPrice * rate / 100.0);
        }

        return taxAmounts.stream().reduce(0.0, Double::sum);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public BigDecimal getTotalPrice() {
        return tickets.stream().map(Ticket::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
