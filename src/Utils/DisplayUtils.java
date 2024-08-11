package Utils;

import contains.PatternConstants;
import model.IRoom;
import validation.Validation;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DisplayUtils {
    public static <T> void displayCollection(String title, Collection<T> collection, Function<T, String> formatter, String separator) {
        if (Validation.isCollectionEmpty(collection)) {
            System.out.println("No items found.");
        } else {
            System.out.println(title);
            System.out.println(PatternConstants.SEPARATOR);
            String formattedItems = collection.stream()
                    .map(formatter)
                    .collect(Collectors.joining(separator + "\n"));
            System.out.println(formattedItems);
            System.out.println(PatternConstants.SEPARATOR);
        }
    }

    public static void browseRooms(Collection<IRoom> rooms) {
        displayCollection("Available Rooms:", rooms, room ->
                String.format("Room Number: %s%nPrice: %.2f%nType: %s%nAvailable: %b",
                        room.getRoomNumber(), room.getRoomPrice(), room.getRoomType(), room.isFree()), PatternConstants.SEPARATOR);
    }
}
