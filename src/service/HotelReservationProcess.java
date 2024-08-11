package service;

import api.HotelResource;
import model.IRoom;

import java.util.Collection;
import java.util.Scanner;

public class HotelReservationProcess extends ReservationProcess {

    public HotelReservationProcess(HotelResource hotelResourceService, Scanner scanner) {
        super(hotelResourceService, scanner);
    }

    @Override
    protected boolean proceedWithReservation() {
        return "yes".equalsIgnoreCase(receiveInput("Proceed with reservation? (yes/no): "));
    }

    @Override
    protected boolean isRegisteredCustomer() {
        return "yes".equalsIgnoreCase(receiveInput("Are you a registered customer? (yes/no): "));
    }

    @Override
    protected String getEmail() {
        return receiveInput("Enter your email (namht4@gmail.com): ");
    }

    @Override
    protected IRoom selectRoom(Collection<IRoom> rooms) {
        String roomNumber = receiveInput("Enter the room number you'd like to book: ");
        return rooms.stream()
                .filter(r -> r.getRoomNumber().equals(roomNumber))
                .findFirst()
                .orElse(null);
    }

}