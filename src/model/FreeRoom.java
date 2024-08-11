package model;

public final class FreeRoom extends Room {

    public FreeRoom(final String roomNumber, final RoomType roomType) {
        super(roomNumber, 0.0, roomType);
    }

    @Override
    public String toString() {
        return "FreeRoom{" +
                "roomNumber='" + getRoomNumber() + '\'' +
                ", roomPrice=" + getRoomPrice() +
                ", roomType=" + getRoomType() +
                '}';
    }
}
