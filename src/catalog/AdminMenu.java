package catalog;

import Utils.DisplayUtils;
import api.AdminResource;
import contains.PatternConstants;
import model.Customer;
import model.IRoom;
import model.Room;
import model.RoomType;
import validation.Validation;

import java.util.*;
import java.util.function.Function;


public class AdminMenu {

    private static final AdminResource hotelAdminResource = AdminResource.getAdminResourceInstance();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, RoomType> roomTypeMap = new HashMap<>(Map.of(
            "1", RoomType.SINGLE,
            "2", RoomType.DOUBLE
    ));

    public static void adminMenu() {
        while (true) {
            printMenu();
            String adminInput = receiveInput("Please enter your choice: ");
            if (Validation.validateMenuInput(adminInput, "Invalid input. Please enter a single digit.", 1)) {
                char action = adminInput.charAt(0);
                if (action == '5') {
                    MainMenu.displayMainMenu();
                    break;
                }
                handleMenuChoice(action);
            }
        }
    }

    private static void handleMenuChoice(char action) {
        switch (action) {
            case '1' -> displayAllCustomers();
            case '2' -> displayAllRooms();
            case '3' -> hotelAdminResource.displayAllReservations();
            case '4' -> addRoom();
            default -> System.out.println("Unrecognized action. Please select a valid option.");
        }
    }

    private static void addRoom() {
        do {
            Room room = createRoom();
            hotelAdminResource.addRoom(Collections.singletonList(room));
            System.out.println("The room has been successfully added!");
        } while (shouldAddAnotherRoom());
    }

    private static Room createRoom() {
        String roomNumber = receiveInput("Please provide the room number: ");
        double roomPrice = promptForValidDouble("Please input the nightly rate: ", "Invalid price. Please input a valid number:");
        RoomType roomType = promptForValidRoomType("Please select a room type (1 for single bed, 2 for double bed): ", "Invalid room type. Enter 1 for a single bed or 2 for a double bed:");
        return new Room(roomNumber, roomPrice, roomType);
    }

    private static <T> T promptForValidInput(String prompt, String errorMessage, Function<String, T> parser) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                T value = parser.apply(input);
                if (value != null) {
                    return value;
                } else {
                    System.out.println(errorMessage);
                }
            } catch (Exception e) {
                System.out.println(errorMessage);
            }
        }
    }

    private static double promptForValidDouble(String prompt, String errorMessage) {
        return promptForValidInput(prompt, errorMessage, Double::parseDouble);
    }

    private static RoomType promptForValidRoomType(String prompt, String errorMessage) {
        return promptForValidInput(prompt, errorMessage, input -> roomTypeMap.get(input));
    }

    private static boolean shouldAddAnotherRoom() {
        String response;
        do {
            response = receiveInput("Do you want to add another room? (Y/N): ");
            if (!"Y".equalsIgnoreCase(response) && !"N".equalsIgnoreCase(response)) {
                System.out.println("Invalid input. Please type 'Y' for yes or 'N' for no.");
            }
        } while (!"Y".equalsIgnoreCase(response) && !"N".equalsIgnoreCase(response));
        return "Y".equalsIgnoreCase(response);
    }

    private static String receiveInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static void displayAllRooms() {
        Collection<IRoom> rooms = hotelAdminResource.getAllRooms();
        DisplayUtils.displayCollection("Available Rooms:", rooms, room ->
                String.format("Room Number: %s%nPrice: %.2f%nType: %s%nAvailable: %b",
                        room.getRoomNumber(), room.getRoomPrice(), room.getRoomType(), room.isFree()), PatternConstants.SEPARATOR);
    }

    private static void displayAllCustomers() {
        Collection<Customer> customers = hotelAdminResource.getAllCustomers();
        DisplayUtils.displayCollection("Customers List:", customers, Customer::toString, PatternConstants.SEPARATOR);
    }

    private static void printMenu() {
        String menu = """
                Admin Menu
                --------------------------------------------
                1. See all Customers
                2. See all Rooms
                3. See all Reservations
                4. Add a Room
                5. Back to Main Menu
                --------------------------------------------
                """;
        System.out.print(menu);
    }
}
