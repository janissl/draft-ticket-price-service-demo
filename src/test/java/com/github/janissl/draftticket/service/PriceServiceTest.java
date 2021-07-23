package com.github.janissl.draftticket.service;

import com.github.janissl.draftticket.conf.ExternalWebApiService;
import com.github.janissl.draftticket.exception.DataUnavailableException;
import com.github.janissl.draftticket.exception.ExternalServiceUnavailableException;
import com.github.janissl.draftticket.exception.InvalidUserInputException;
import com.github.janissl.draftticket.model.DraftTicket;
import com.github.janissl.draftticket.model.Passenger;
import com.github.janissl.draftticket.model.PassengerType;
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
    PriceService priceService;

    Double[] taxRates = {21.0};
    String destination;
    List<Passenger> passengerList = new ArrayList<>();

    @BeforeEach
    void resetPassengerList() {
        passengerList.clear();
        destination = "Vilnius";
    }

    @Test
    @DisplayName("Total amount of multiple applicable taxes must be calculated correctly")
    void testCorrectTaxCalculation() {
        List<Double> applicableTaxes = new ArrayList<>();
        applicableTaxes.add(11.0);
        applicableTaxes.add(10.0);

        assertEquals(
                BigDecimal.valueOf(2.10),
                BigDecimal.valueOf(DraftTicket.calculateTotalTax(10.0, applicableTaxes))
        );
    }

    @Test
    @DisplayName("Total price for the given passenger group and destination must be calculated correctly")
    void testCorrectTotalPrice()
            throws ExternalServiceUnavailableException, DataUnavailableException, InvalidUserInputException {
        passengerList.add(new Passenger(PassengerType.ADULT, destination, 2));
        passengerList.add(new Passenger(PassengerType.CHILD, destination, 1));

        Mockito.when(externalWebApiService.getBasePrice(destination)).thenReturn(10.0);
        Mockito.when(externalWebApiService.getApplicableTaxes()).thenReturn(taxRates);

        assertEquals(BigDecimal.valueOf(29.04), priceService.createDraftTicket(passengerList).getTotalPrice());
    }

    @Test
    @DisplayName("Null parameter values of passengers must throw IncorrectInputException")
    void testPassengerNullValues() {
        passengerList.add(new Passenger(null, null, null));

        Exception exception = assertThrows(InvalidUserInputException.class, () -> priceService.createDraftTicket(passengerList));

        String expectedMessage = "Invalid passenger data received from the user";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Unsupported destination must throw DataUnavailableException")
    void testUnsupportedDestination() throws ExternalServiceUnavailableException {
        destination = "MiddleOfNowhere";
        passengerList.add(new Passenger(PassengerType.ADULT, destination, 2));

        Mockito.when(externalWebApiService.getBasePrice(destination)).thenReturn(-1.0);

        Exception exception = assertThrows(DataUnavailableException.class, () -> priceService.createDraftTicket(passengerList));

        String expectedMessage = String.format("Destination '%s' is not available", destination);
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Negative number of luggage items must throw IncorrectInputException")
    void testIncorrectLuggageItemNumber() {
        passengerList.add(new Passenger(PassengerType.ADULT, destination, -1));

        Exception exception = assertThrows(InvalidUserInputException.class, () -> priceService.createDraftTicket(passengerList));

        String expectedMessage = "Number of luggage items cannot be negative";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
