package service;

import Utils.DisplayUtils;
import api.HotelResource;
import model.IRoom;
import model.Reservation;
import validation.Validation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;

public abstract class ReservationProcess {
    protected final HotelResource hotelResourceService;
    protected final Scanner scanner;
    private static final Logger logger = Logger.getLogger(ReservationProcess.class.getName());

    protected ReservationProcess(HotelResource hotelResourceService, Scanner scanner) {
        this.hotelResourceService = hotelResourceService;
        this.scanner = scanner;
    }

    public final void execute(Date checkIn, Date checkOut, Collection<IRoom> rooms) {
        if (!proceedWithReservation()) {
            cancel("Reservation process cancelled.");
            return;
        }

        if (!isRegisteredCustomer()) {
            cancel("Please create an account to proceed.");
            return;
        }

        String email = getEmail();
        if (!customerExists(email)) {
            cancel("No customer found. Please create an account.");
            return;
        }

        IRoom selectedRoom = selectRoom(rooms);
        if (Objects.isNull(selectedRoom)) {
            cancel("Room not available. Please start again.");
            // Try to find recommended rooms
            Collection<IRoom> recommendedRooms = findRecommendedRooms(checkIn, checkOut);
            if (!recommendedRooms.isEmpty()) {
                handleRecommendedRooms(recommendedRooms, email, checkIn, checkOut);
            } else {
                cancel("No rooms available for alternative dates.");
            }
            return;
        }

        makeReservation(email, selectedRoom, checkIn, checkOut);
    }

    protected abstract boolean proceedWithReservation();

    protected abstract boolean isRegisteredCustomer();

    protected abstract String getEmail();

    protected abstract IRoom selectRoom(Collection<IRoom> rooms);

    private boolean customerExists(String email) {
        return hotelResourceService.getCustomer(email).isPresent();
    }

    private void cancel(String message) {
        System.out.println(message);
    }

    private void makeReservation(String email, IRoom selectedRoom, Date checkIn, Date checkOut) {
        Optional<Reservation> reservation = hotelResourceService.bookARoom(email, selectedRoom, checkIn, checkOut);
        reservation.ifPresentOrElse(
                res -> {
                    System.out.println("Reservation successful!");
                    System.out.println(res);
                },
                () -> System.out.println("Failed to make a reservation.")
        );
    }

    public Collection<IRoom> findRecommendedRooms(Date checkIn, Date checkOut) {
        logger.info("Finding recommended rooms");

        LocalDate checkInDate = convertToLocalDate(checkIn);
        LocalDate checkOutDate = convertToLocalDate(checkOut);

        logger.info("Original Check-In Date: " + checkInDate);
        logger.info("Original Check-Out Date: " + checkOutDate);

        LocalDate recommendedCheckIn = checkInDate.plusDays(7);
        LocalDate recommendedCheckOut = checkOutDate.plusDays(7);

        logger.info("Recommended Check-In Date: " + recommendedCheckIn);
        logger.info("Recommended Check-Out Date: " + recommendedCheckOut);

        Date recommendedCheckInDate = convertToDate(recommendedCheckIn);
        Date recommendedCheckOutDate = convertToDate(recommendedCheckOut);

        logger.info("Converted Recommended Check-In Date: " + recommendedCheckInDate);
        logger.info("Converted Recommended Check-Out Date: " + recommendedCheckOutDate);

        Collection<IRoom> recommendedRooms = hotelResourceService.findARoom(recommendedCheckInDate, recommendedCheckOutDate);

        if (Validation.isCollectionEmpty(recommendedRooms)) {
            logger.info("No recommended rooms available.");
        } else {
            logger.info("Found " + recommendedRooms.size() + " recommended rooms.");
        }

        return recommendedRooms;
    }

    private LocalDate convertToLocalDate(Date date) {
        logger.fine("Converting Date to LocalDate: " + date);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date convertToDate(LocalDate localDate) {
        logger.fine("Converting LocalDate to Date: " + localDate);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void handleRecommendedRooms(Collection<IRoom> recommendedRooms, String email, Date originalCheckIn, Date originalCheckOut) {
        DisplayUtils.browseRooms(recommendedRooms); // Hiển thị phòng gợi ý cho người dùng

        if (proceedWithReservation()) {
            IRoom selectedRoom = selectRoom(recommendedRooms);
            if (Objects.nonNull(selectedRoom)) {
                // Đảm bảo phòng không bị đặt chồng cho cùng khoảng thời gian
                Optional<Reservation> reservation = hotelResourceService.bookARoom(email, selectedRoom, originalCheckIn, originalCheckOut);
                reservation.ifPresentOrElse(
                        res -> {
                            System.out.println("Reservation successful!");
                            System.out.println(res);
                        },
                        () -> System.out.println("Failed to make a reservation.")
                );
            } else {
                cancel("Room selection failed. Please start again.");
            }
        } else {
            cancel("Reservation process cancelled.");
        }
    }

    public String receiveInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
