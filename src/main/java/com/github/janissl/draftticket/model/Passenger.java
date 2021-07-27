package com.github.janissl.draftticket.model;

public class Passenger {
    public PassengerType passengerType;
    public Integer itemsOfLuggage;
    public String destination;

    public Passenger() {
        super();
    }

    public Passenger(PassengerType passengerType, String destination) {
        this(passengerType, destination, 0);
    }

    public Passenger(PassengerType passengerType, String destination, Integer itemsOfLuggage) {
        this.passengerType = passengerType;
        this.itemsOfLuggage = itemsOfLuggage;
        this.destination = destination;
    }

    public PassengerType getPassengerType() {
        return passengerType;
    }

    public Integer getItemsOfLuggage() {
        return itemsOfLuggage;
    }

    public String getDestination() {
        return destination;
    }
}
