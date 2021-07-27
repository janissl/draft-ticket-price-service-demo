package com.github.janissl.draftticket.model;

import java.math.BigDecimal;
import java.util.List;

public class DraftPrice {
    private final DraftTicket draftTicket;
    private BigDecimal totalPrice;

    public DraftPrice(DraftTicket draftTicket) {
        this.draftTicket = draftTicket;
    }

    public List<Ticket> getTickets() {
        return draftTicket.getTickets();
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

}
