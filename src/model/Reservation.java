package model;

import java.util.Date;

public record Reservation(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
    @Override
    public String toString() {
        return "Customer: " + customer.toString()
                + "\nRoom: " + room.toString()
                + "\nCheckIn Date: " + checkInDate
                + "\nCheckOut Date: " + checkOutDate;
    }
}
