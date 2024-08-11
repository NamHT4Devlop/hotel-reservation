package service.reservation;

import Utils.DisplayUtils;
import contains.PatternConstants;
import model.Customer;
import model.Reservation;
import model.IRoom;
import validation.Validation;

import java.util.*;
import java.util.stream.Collectors;

public class ReservationService {

    private static final ReservationService RESERVATION_SERVICE_INSTANCE = new ReservationService();
    private final Map<String, IRoom> rooms = new HashMap<>();
    private final Map<String, Collection<Reservation>> reservationsMap = new HashMap<>();

    private ReservationService() {
    }

    public static ReservationService getReservationServiceInstance() {
        return RESERVATION_SERVICE_INSTANCE;
    }

    public void addRoom(IRoom room) {
        String roomNumber = room.getRoomNumber();
        IRoom existingRoom = rooms.putIfAbsent(roomNumber, room);

        if (Objects.nonNull(existingRoom)) {
            System.out.printf("Room with number %s already exists and was not added.%n", roomNumber);
        }
    }

    public Collection<IRoom> getAllRooms() {
        return rooms.values().stream()
                .filter(room -> room.getRoomPrice() > 0)
                .collect(Collectors.toList());
    }

    public Reservation reserveARoom(Customer customer, IRoom room, Date checkIn, Date checkOut) {
        Objects.requireNonNull(customer, "Customer must not be null");
        Objects.requireNonNull(room, "Room must not be null");
        Objects.requireNonNull(checkIn, "Check-in date must not be null");
        Objects.requireNonNull(checkOut, "Check-out date must not be null");

        if (!checkIn.before(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

        var reservation = new Reservation(customer, room, checkIn, checkOut);
        reservationsMap.computeIfAbsent(customer.email(), _ -> new LinkedList<>()).add(reservation);
        return reservation;
    }

    public Collection<IRoom> findRooms(Date checkInDate, Date checkOutDate) {
        return Optional.ofNullable(retrieveAvailableRooms(checkInDate, checkOutDate))
                .orElse(Collections.emptyList());    }

    public Collection<Reservation> getCustomersReservation(Customer customer) {
        return Optional.ofNullable(reservationsMap.get(customer.email()))
                .orElse(Collections.emptyList());
    }

    public void printAllReservation() {
        Collection<Reservation> reservations = retrieveAllReservations();
        DisplayUtils.displayCollection("Reservations List:", reservations, Reservation::toString, PatternConstants.SEPARATOR);
    }

    private Collection<IRoom> retrieveAvailableRooms(Date checkInDate, Date checkOutDate) {
        Collection<Reservation> allReservationEntries = retrieveAllReservations();

        // Find the set of rooms that are not available
        Set<IRoom> nonAvailableRooms = allReservationEntries.stream()
                .filter(reservation -> isOverlapWithReservation(reservation, checkInDate, checkOutDate))
                .map(Reservation::room)
                .collect(Collectors.toSet());

        // Return the list of available rooms
        return rooms.values().stream()
                .filter(room -> !nonAvailableRooms.contains(room))
                .toList();
    }

    private boolean isOverlapWithReservation(Reservation reservation, Date checkIn, Date checkOut) {
        Optional<Date> optionalCheckIn = Optional.ofNullable(checkIn);
        Optional<Date> optionalCheckOut = Optional.ofNullable(checkOut);

        if (Validation.isCollectionEmpty(Collections.singleton(optionalCheckIn)) ||
                Validation.isCollectionEmpty(Collections.singleton(optionalCheckOut)) ||
                !Objects.requireNonNull(checkIn).before(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }
        return checkIn.before(reservation.checkOutDate()) &&
                Objects.requireNonNull(checkOut).after(reservation.checkInDate());
    }

    private Collection<Reservation> retrieveAllReservations() {
        return reservationsMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
