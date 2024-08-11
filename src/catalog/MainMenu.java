package catalog;

import Utils.DisplayUtils;
import api.HotelResource;
import contains.PatternConstants;
import model.Reservation;
import model.IRoom;
import service.HotelReservationProcess;
import service.ReservationProcess;
import validation.Validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainMenu {

    private static final HotelResource hotelResourceService = HotelResource.getHotelResourceInstance();
    private static final Scanner scanner = new Scanner(System.in);
    private static final ReservationProcess reservationProcess = new HotelReservationProcess(hotelResourceService, scanner);

    public static boolean displayMainMenu() {
        while (true) {
            printMainMenuOptions();
            String userInput = reservationProcess.receiveInput("Please enter your choice: ");
            if (Validation.validateMenuInput(userInput, "Invalid input. Please enter a single digit.", 1)) {
                char choice = userInput.charAt(0);
                if (choice == '5') {
                    System.out.println("Exiting...");
                    return false;
                } else {
                    handleUserChoice(choice);
                }
            }
        }
    }

    private static void handleUserChoice(char choice) {
        switch (choice) {
            case '1' -> searchAndBookRoom();
            case '2' -> reviewMyReservation();
            case '3' -> registerCustomer();
            case '4' -> AdminMenu.adminMenu();
            case '5' -> System.out.println("Exiting...");
            default -> System.out.println("Unknown action\n");
        }
    }

    private static void searchAndBookRoom() {
        Date checkIn = parseDateInput("Please enter the check-in date (MM/dd/yyyy): ");
        Date checkOut = parseDateInput("Please enter the check-out date (MM/dd/yyyy): ");

        if (Validation.areDatesNotNull(checkIn, checkOut)) {
            Collection<IRoom> roomsAvailable = hotelResourceService.findARoom(checkIn, checkOut);
            if (!Validation.isCollectionEmpty(roomsAvailable)) {
                handleRoomsAvailable(roomsAvailable, checkIn, checkOut);
            } else {
                displayNoRoomsAvailableMessage(checkIn, checkOut);
            }
        }
    }

    private static void displayNoRoomsAvailableMessage(Date checkIn, Date checkOut) {
        String pattern = PatternConstants.MY_CUSTOM_DATE_FORMAT;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        // Hiển thị thông báo không có phòng cho các ngày đã chọn
        System.out.printf("No rooms available for the selected dates: Check-in %s, Check-out %s.%n",
                sdf.format(checkIn), sdf.format(checkOut));
        // Hiển thị thông tin về phòng gợi ý nếu có
        Collection<IRoom> recommendedRooms = reservationProcess.findRecommendedRooms(checkIn, checkOut);
        if (!Validation.isCollectionEmpty(recommendedRooms)) {
            System.out.println("However, we found some rooms available for alternative dates:");
            DisplayUtils.browseRooms(recommendedRooms); // Hiển thị các phòng gợi ý
        } else {
            System.out.println("No rooms available even for alternative dates.");
        }
    }


    private static Date parseDateInput(String promptDate) {
        while (true) {
            String input = reservationProcess.receiveInput(promptDate);
            if (!input.matches(PatternConstants.DATE_PATTERN)) {
                System.out.printf("Error: Invalid date format '%s'. Use MM/dd/yyyy.%n", input);
                continue;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(PatternConstants.MY_CUSTOM_DATE_FORMAT);
                sdf.setLenient(false);
                return sdf.parse(input);
            } catch (ParseException ex) {
                System.out.printf("Error: Invalid date '%s'. Use MM/dd/yyyy.%n", input);
            }
        }
    }

    private static void handleRoomsAvailable(Collection<IRoom> rooms, Date checkIn, Date checkOut) {
        DisplayUtils.browseRooms(rooms);
        reservationProcess.execute(checkIn, checkOut, rooms);
    }

    private static void reviewMyReservation() {
        String getEmail = getEmailInput();
        Optional<String> optionalEmail = Optional.ofNullable(getEmail);
        optionalEmail.ifPresentOrElse(
                email -> {
                    if (Validation.isEmail(email)) {
                        Collection<Reservation> reservations = hotelResourceService.getCustomersReservations(email);
                        displayReservations(reservations);
                    } else {
                        System.out.printf("Invalid email format '%s'. Please try again.%n", email);
                    }
                },
                () -> System.out.println("No valid email provided.")
        );
    }

    private static void displayReservations(Collection<Reservation> reservations) {
        DisplayUtils.displayCollection("Reservations List:", reservations, Reservation::toString, PatternConstants.SEPARATOR);
    }

    private static void registerCustomer() {
        String email = null;
        String firstName = null;
        String lastName = null;

        while (email == null) {
            email = getEmailInput();
            if (!Validation.isEmail(email)) {
                System.out.printf("Invalid email format '%s'. Please try again.%n", email);
                email = null;
            }
        }

        while (firstName == null) {
            firstName = reservationProcess.receiveInput("Enter your first name: ");
            if (firstName.trim().isEmpty()) {
                System.out.println("First name cannot be empty. Please try again.");
                firstName = null;
            }
        }

        while (lastName == null) {
            lastName = reservationProcess.receiveInput("Enter your last name: ");
            if (lastName.trim().isEmpty()) {
                System.out.println("Last name cannot be empty. Please try again.");
                lastName = null;
            }
        }

        try {
            hotelResourceService.createACustomer(email, firstName, lastName);
            System.out.println("Your account is now active!");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
            System.out.println("Please try again.");
        }
    }

    private static String getEmailInput() {
        while (true) {
            String email = reservationProcess.receiveInput("Enter your email (namht4@gmail.com): ");
            if (Validation.isEmail(email)) {
                return email;
            } else {
                System.out.printf("Invalid email format '%s'. Please try again.%n", email);
            }
        }
    }

    private static void printMainMenuOptions() {
        String menu = """
                Welcome to Hotel NamHT4:
                --------------------------------------------
                1. Find and reserve a room
                2. See my reservations
                3. Create an account
                4. Admin
                5. Exit
                --------------------------------------------
                Please select a number for the menu option:
                   \s""";
        System.out.print(menu);
    }
}
