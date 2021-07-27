package com.github.janissl.draftticket.model;

import com.github.janissl.draftticket.exception.InvalidUserInputException;
import com.github.janissl.draftticket.service.PriceService;

import java.util.List;

public class DraftTicket {
    private final List<Ticket> tickets;

    public DraftTicket(List<Ticket> ticketList, List<Double> applicableTaxes) throws InvalidUserInputException {
        for (Ticket ticket: ticketList) {
            Double netPrice = PriceService.getTicketNetPrice(ticket);
            ticket.setPrice(PriceService.getTotalPriceOfTicket(netPrice, applicableTaxes));
        }

        this.tickets = ticketList;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
