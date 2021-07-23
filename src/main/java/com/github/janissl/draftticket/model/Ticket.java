package com.github.janissl.draftticket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonIgnoreProperties(value = {"netPrice"})
public class Ticket {
    private final TicketType ticketType;
    private final Double basePrice;
    private final Integer count;
    private BigDecimal price;

    public Ticket(TicketType ticketType, Double basePrice) {
        this(ticketType, basePrice, 1);
    }

    public Ticket(TicketType ticketType, Double basePrice, Integer numberOfItems) {
        this.ticketType = ticketType;
        this.basePrice = basePrice;
        this.count = numberOfItems;
    }

    public void setTotalPrice(Double totalPrice) {
        this.price = BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Double getNetPrice() {
        Double percentageDiscountChild = 50.0;
        Double percentageLuggage = 30.0;
        Double netPrice;

        switch (ticketType) {
            case ADULT:
                netPrice = basePrice;
                break;
            case CHILD:
                netPrice = basePrice * percentageDiscountChild / 100.0;
                break;
            case LUGGAGE:
                netPrice = count * basePrice * percentageLuggage / 100.0;
                break;
            default:
                netPrice = -1.0;
        }

        return netPrice;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public Integer getCount() {
        return count;
    }
}
