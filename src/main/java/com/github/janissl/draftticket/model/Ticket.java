package com.github.janissl.draftticket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Objects;

@JsonIgnoreProperties(value = {"basePrice"})
public class Ticket {
    private final TicketType ticketType;
    private final Double basePrice;
    private final Integer count;
    private BigDecimal totalPrice;

    public Ticket(TicketType ticketType, Double basePrice) {
        this(ticketType, basePrice, 1);
    }

    public Ticket(TicketType ticketType, Double basePrice, Integer numberOfItems) {
        this.ticketType = ticketType;
        this.basePrice = basePrice;
        this.count = numberOfItems;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setPrice(BigDecimal price) {
        this.totalPrice = price;
    }

    public BigDecimal getPrice() {
        return totalPrice;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public Integer getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return ticketType == ticket.ticketType &&
                Objects.equals(basePrice, ticket.basePrice) &&
                Objects.equals(count, ticket.count) &&
                Objects.equals(totalPrice, ticket.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketType, basePrice, count, totalPrice);
    }
}
