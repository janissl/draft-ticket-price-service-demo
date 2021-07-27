package com.github.janissl.draftticket.service;

import com.github.janissl.draftticket.conf.ExternalWebApiService;
import com.github.janissl.draftticket.exception.DataUnavailableException;
import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import com.github.janissl.draftticket.exception.InvalidUserInputException;
import com.github.janissl.draftticket.model.Passenger;
import com.github.janissl.draftticket.model.PassengerType;
import com.github.janissl.draftticket.model.Ticket;
import com.github.janissl.draftticket.model.TicketType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @Mock
    ExternalWebApiService externalWebApiService;

    @InjectMocks
    TicketService ticketService;

    List<Double> applicableTaxes;
    String destination;
    List<Passenger> passengerList = new ArrayList<>();


    @BeforeEach
    void resetInputData() {
        destination = "Vilnius";
        passengerList.clear();
        applicableTaxes = Collections.singletonList(21.0);
    }

    @Test
    @DisplayName("A null value for a passenger type must throw InvalidUserInputException")
    void testNullPassengerType() {
        passengerList.add(new Passenger(null, "Vilnius"));

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> ticketService.createDraftTicket(passengerList, applicableTaxes));

        String expectedMessage = "No passenger type received from the client.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("A null value for a destination must throw InvalidUserInputException")
    void testNullDestination() {
        destination = null;
        passengerList.add(new Passenger(PassengerType.ADULT, destination));

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> ticketService.createDraftTicket(passengerList, applicableTaxes));

        String expectedMessage = "No destination received from the client.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("A null value for a number of luggage items must throw InvalidUserInputException")
    void testNullLuggageItems() {
        passengerList.add(new Passenger(PassengerType.ADULT, destination, null));

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> ticketService.createDraftTicket(passengerList, applicableTaxes));

        String expectedMessage = "No number of luggage items received from the client.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Multiple null parameters must throw InvalidUserInputException with concatenated error messages")
    void testNullValuesPassenger() {
        passengerList.add(new Passenger(null, null, null));

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> ticketService.createDraftTicket(passengerList, applicableTaxes));

        String expectedMessage = "No passenger type received from the client. " +
                "No number of luggage items received from the client. " +
                "No destination received from the client.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("A negative base price for an unsupported destination must throw DataUnavailableException")
    void testUnsupportedDestination() throws ExternalServiceUnavailableException {
        destination = "MiddleOfNowhere";
        double basePrice = -1.0;
        passengerList.add(new Passenger(PassengerType.ADULT, destination, 2));

        Mockito.when(externalWebApiService.getBasePrice(destination)).thenReturn(basePrice);

        Exception exception = assertThrows(
                DataUnavailableException.class,
                () -> ticketService.createDraftTicket(passengerList, applicableTaxes));

        String expectedMessage = String.format("Destination '%s' is not available.", destination);
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Negative number of luggage items must throw InvalidUserInputException")
    void testIncorrectLuggageItemNumber() {
        int numberOfItems = -1;
        passengerList.add(new Passenger(PassengerType.ADULT, destination, numberOfItems));

        Exception exception = assertThrows(
                InvalidUserInputException.class,
                () -> ticketService.createDraftTicket(passengerList, applicableTaxes));

        String expectedMessage = "Number of luggage items cannot be negative.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Tickets for all passengers must be generated correctly")
    void testGenerationOfTicketsForAllPassengers() throws ExternalServiceUnavailableException {
        PassengerType passengerType1 = PassengerType.ADULT;
        PassengerType passengerType2 = PassengerType.CHILD;

        TicketType ticketType1 = TicketType.ADULT;
        TicketType ticketType2 = TicketType.CHILD;

        int numberOfItems1 = 2;
        int numberOfItems2 = 1;

        passengerList.add(new Passenger(passengerType1, destination, numberOfItems1));
        passengerList.add(new Passenger(passengerType2, destination, numberOfItems2));

        double basePrice = externalWebApiService.getBasePrice(destination);

        List<Ticket> expectedTickets = new ArrayList<>();
        expectedTickets.add(new Ticket(ticketType1, basePrice));
        expectedTickets.add(new Ticket(TicketType.LUGGAGE, basePrice, numberOfItems1));
        expectedTickets.add(new Ticket(ticketType2, basePrice));
        expectedTickets.add(new Ticket(TicketType.LUGGAGE, basePrice, numberOfItems2));

        List<Ticket> actualTickets = ticketService.generateTicketsForAllPassengers(passengerList);

        assertEquals(expectedTickets, actualTickets);
    }
}
