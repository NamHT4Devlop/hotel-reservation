package api;

import model.Customer;
import model.Reservation;
import model.IRoom;
import service.customer.CustomerService;
import service.reservation.ReservationService;
import validation.Validation;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

public class HotelResource {

    private static final HotelResource HOTEL_RESOURCE_SINGLETON = new HotelResource();

    private final CustomerService customerService = CustomerService.getCustomerServiceInstance();
    private final ReservationService reservationService = ReservationService.getReservationServiceInstance();

    private HotelResource() {
    }

    public static HotelResource getHotelResourceInstance() {
        return HOTEL_RESOURCE_SINGLETON;
    }

    public Optional<Customer> getCustomer(String email) {
        var trimmedEmail = Optional.ofNullable(email)
                .map(String::trim)
                .filter(e -> !e.isEmpty())
                .map(String::toLowerCase);

        return trimmedEmail.flatMap(customerService::getCustomer);
    }

    public void createACustomer(String email, String firstName, String lastName) {
        var validEmail = Optional.ofNullable(email)
                .map(String::trim)
                .filter(e -> !e.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Email must not be null or empty."));

        var validFirstName = Optional.ofNullable(firstName)
                .map(String::trim)
                .filter(f -> !f.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("First name must not be null or empty."));

        var validLastName = Optional.ofNullable(lastName)
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Last name must not be null or empty."));

        customerService.addCustomer(validEmail.toLowerCase(), validFirstName, validLastName);
    }

    public Optional<Reservation> bookARoom(String customerEmail, IRoom room, Date checkIn, Date checkOut) {
        if (!Validation.areDatesNotNull(checkIn, checkOut) || !checkIn.before(checkOut)) {
            throw new IllegalArgumentException("Invalid check-in or check-out dates.");
        }

        return getCustomer(customerEmail)
                .map(customer -> reservationService.reserveARoom(customer, room, checkIn, checkOut))
                .or(() -> {
                    throw new IllegalArgumentException("Customer not found with email: " + customerEmail);
                });
    }

    public Collection<Reservation> getCustomersReservations(String customerEmail) {
        return getCustomer(customerEmail)
                .map(reservationService::getCustomersReservation)
                .orElseGet(Collections::emptyList);
    }

    public Collection<IRoom> findARoom(Date checkIn, Date checkOut) {
        if (!Validation.areDatesNotNull(checkIn, checkOut) || !checkIn.before(checkOut)) {
            throw new IllegalArgumentException("Invalid check-in or check-out dates.");
        }
        return reservationService.findRooms(checkIn, checkOut);
    }

}
