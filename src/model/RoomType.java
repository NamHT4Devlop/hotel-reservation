package model;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RoomType {
    SINGLE("1"),
    DOUBLE("2");

    private final String labelRoomName;

    private static final Map<String, RoomType> BY_LABEL_ROOM_NAME = Stream.of(values())
            .collect(Collectors.toMap(RoomType::getLabelName, roomType -> roomType));

    RoomType(String labelRoomName) {
        this.labelRoomName = labelRoomName;
    }

    public String getLabelName() {
        return labelRoomName;
    }

    public static RoomType fromLabel(String labelRoomName) {
        return BY_LABEL_ROOM_NAME.get(labelRoomName);
    }

}

