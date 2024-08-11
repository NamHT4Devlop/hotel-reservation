package api;

import Utils.DisplayUtils;
import contains.PatternConstants;
import model.Customer;
import model.IRoom;
import service.customer.CustomerService;
import service.reservation.ReservationService;
import validation.Validation;

import java.util.Collection;
import java.util.List;

public class AdminResource {

    private static final AdminResource ADMIN_RESOURCE_SINGLETON = new AdminResource();

    private final CustomerService customerService = CustomerService.getCustomerServiceInstance();
    private final ReservationService reservationService = ReservationService.getReservationServiceInstance();

    private AdminResource() {}

    public static AdminResource getAdminResourceInstance() {
        return ADMIN_RESOURCE_SINGLETON;
    }

    public void addRoom(List<IRoom> rooms) {
        if (Validation.anyEmptyOrNull(rooms, "Room list must not be null or empty.")) {
            rooms.forEach(reservationService::addRoom);
            System.out.println("Rooms have been successfully added.");
        }
    }

    public Collection<IRoom> getAllRooms() {
        Collection<IRoom> allRooms = reservationService.getAllRooms();
        DisplayUtils.displayCollection("Available Rooms:", allRooms, room ->
                String.format("Room Number: %s%nPrice: %.2f%nType: %s%nAvailable: %b",
                        room.getRoomNumber(), room.getRoomPrice(), room.getRoomType(), room.isFree()), PatternConstants.SEPARATOR);
        return allRooms;
    }

    public Collection<Customer> getAllCustomers() {
        Collection<Customer> allCustomers = customerService.getAllCustomers();
        DisplayUtils.displayCollection("Customers List:", allCustomers, Customer::toString, PatternConstants.SEPARATOR);
        return allCustomers;
    }

    public void displayAllReservations() {
        System.out.println("List of Reservations:");
        reservationService.printAllReservation();
    }

}
