package com.github.janissl.draftticket.service;

import com.github.janissl.draftticket.conf.ExternalWebApiService;
import com.github.janissl.draftticket.exception.DataUnavailableException;
import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import com.github.janissl.draftticket.exception.InvalidUserInputException;
import com.github.janissl.draftticket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    ExternalWebApiService externalWebApiService;

    @InjectMocks
    TicketService ticketService;

    @InjectMocks
    PriceService priceService;

    String destination;
    List<Passenger> passengerList = new ArrayList<>();

    @BeforeEach
    void resetPassengerList() {
        destination = "Vilnius";
        passengerList.clear();
    }

    @Test
    @DisplayName("Total amount of an empty applicable tax list must be equal to zero")
    void testNoTaxCalculation() throws ExternalServiceUnavailableException, InvalidUserInputException {
        Double netPrice = 10.0;
        Double[] applicableTaxes = {};
        Mockito.when(externalWebApiService.getApplicableTaxes()).thenReturn(applicableTaxes);

        assertEquals(
                BigDecimal.valueOf(0.0),
                BigDecimal.valueOf(PriceService.calculateTotalTax(netPrice, priceService.getApplicableTaxes()))
        );
    }

    @Test
    @DisplayName("A negative tax rate must throw IncorrectInputException")
    void testNegativeTax() throws ExternalServiceUnavailableException {
        Double netPrice = 100.0;
        Double[] applicableTaxes = {5.0, -5.0};
        Mockito.when(externalWebApiService.getApplicableTaxes()).thenReturn(applicableTaxes);

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> PriceService.calculateTotalTax(netPrice, priceService.getApplicableTaxes()));

        String expectedMessage = "Tax rates cannot be negative.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Amount of a single tax must be calculated correctly")
    void testSingleTaxCalculation() throws ExternalServiceUnavailableException, InvalidUserInputException {
        Double netPrice = 100.0;
        Double[] applicableTaxes = {21.0};
        Mockito.when(externalWebApiService.getApplicableTaxes()).thenReturn(applicableTaxes);

        assertEquals(
                BigDecimal.valueOf(21.0),
                BigDecimal.valueOf(PriceService.calculateTotalTax(netPrice, priceService.getApplicableTaxes()))
        );
    }

    @Test
    @DisplayName("Total amount of multiple applicable taxes must be calculated correctly")
    void testMultipleTaxCalculation() throws ExternalServiceUnavailableException, InvalidUserInputException {
        Double netPrice = 100.0;
        Double[] applicableTaxes = {5.0, 21.0};
        Mockito.when(externalWebApiService.getApplicableTaxes()).thenReturn(applicableTaxes);

        assertEquals(
                BigDecimal.valueOf(26.0),
                BigDecimal.valueOf(PriceService.calculateTotalTax(netPrice, priceService.getApplicableTaxes()))
        );
    }

    @Test
    @DisplayName("A net price of an adult passenger must be equal to the base price")
    void testNetPriceAdult() throws InvalidUserInputException {
        Double basePrice = 10.0;
        Ticket ticket = new Ticket(TicketType.ADULT, basePrice);

        assertEquals(basePrice, PriceService.getTicketNetPrice(ticket));
    }

    @Test
    @DisplayName("A net price of a child passenger must be equal to a half of the base price")
    void testNetPriceChild() throws InvalidUserInputException {
        double basePrice = 10.0;
        double percentageFromBasePrice = 50.0;
        Ticket ticket = new Ticket(TicketType.CHILD, basePrice);

        assertEquals(basePrice * percentageFromBasePrice / 100.0, PriceService.getTicketNetPrice(ticket));
    }

    @Test
    @DisplayName("A net price of a luggage item must be equal to a 30% of the base price")
    void testNetPriceLuggage() throws InvalidUserInputException {
        double basePrice = 10.0;
        double percentageFromBasePrice = 30.0;
        Ticket ticket = new Ticket(TicketType.LUGGAGE, basePrice);

        assertEquals(basePrice * percentageFromBasePrice / 100.0, PriceService.getTicketNetPrice(ticket));
    }

    @Test
    @DisplayName("A net price for multiple luggage items must be calculated correctly")
    void testNetPriceMultipleLuggageItems() throws InvalidUserInputException {
        TicketType ticketType = TicketType.LUGGAGE;
        double basePrice = 10.0;
        int numberOfItems = 3;
        double percentageFromBasePrice = 30.0;

        Ticket ticket = new Ticket(ticketType, basePrice, numberOfItems);

        assertEquals(numberOfItems * basePrice * percentageFromBasePrice/ 100.0,
                PriceService.getTicketNetPrice(ticket));
    }

    @Test
    @DisplayName("A missing passenger type on a ticket must throw InvalidUserInputException")
    void testNetPriceMissingPassengerType() {
        TicketType ticketType = null;
        double basePrice = 10.0;
        Ticket ticket = new Ticket(ticketType, basePrice);

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> PriceService.getTicketNetPrice(ticket));

        String expectedMessage = "No passenger type has been set for a ticket.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("A negative base price of a ticket must throw InvalidUserInputException")
    void testNegativeBasePrice() {
        TicketType ticketType = TicketType.ADULT;
        Double basePrice = -10.0;
        Ticket ticket = new Ticket(ticketType, basePrice);

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> PriceService.getTicketNetPrice(ticket));

        String expectedMessage = "Ticket base price cannot be negative.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("A negative number of luggage items must throw InvalidUserInputException")
    void testNegativeNumberOfItems() {
        Integer numberOfItems = -1;
        Double basePrice = 10.0;
        Ticket ticket = new Ticket(TicketType.LUGGAGE, basePrice, numberOfItems);

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> PriceService.getTicketNetPrice(ticket));

        String expectedMessage = "Number of items for a ticket cannot be negative.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    @DisplayName("Multiple incorrect properties of a ticket must throw InvalidUserInputException with concatenated error messages")
    void testMultipleTicketValidationErrors() {
        TicketType ticketType = null;
        Double basePrice = -15.0;
        Integer numberOfItems = -3;

        Ticket ticket = new Ticket(ticketType, basePrice, numberOfItems);

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> PriceService.getTicketNetPrice(ticket));

        String expectedMessage = "No passenger type has been set for a ticket. " +
                "Ticket base price cannot be negative. " +
                "Number of items for a ticket cannot be negative.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Total price for the given passenger group and destination must be calculated correctly")
    void testCorrectTotalPrice()
            throws ExternalServiceUnavailableException, DataUnavailableException, InvalidUserInputException {
        int numberOfItems1 = 2;
        int numberOfItems2 = 1;

        passengerList.add(new Passenger(PassengerType.ADULT, destination, numberOfItems1));
        passengerList.add(new Passenger(PassengerType.CHILD, destination, numberOfItems2));

        double basePrice = 10.0;
        Double[] applicableTaxes = {21.0};

        Mockito.when(externalWebApiService.getBasePrice(destination)).thenReturn(basePrice);
        Mockito.when(externalWebApiService.getApplicableTaxes()).thenReturn(applicableTaxes);

        DraftTicket draftTicket = ticketService.createDraftTicket(passengerList, priceService.getApplicableTaxes());

        assertEquals(BigDecimal.valueOf(29.04), PriceService.getTotalPriceOfDraftTicket(draftTicket.getTickets()));
    }
}
