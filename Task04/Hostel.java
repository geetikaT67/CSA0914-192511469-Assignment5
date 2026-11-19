package Task04;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Hostel Entity - Root object of the serialization graph.
 * Student: Geetika T | Reg No: 192511469
 */
public class Hostel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String hostelName;
    private List<Room> rooms;

    public Hostel(String hostelName) {
        this.hostelName = hostelName;
        this.rooms = new ArrayList<>();
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    public String getHostelName() { return hostelName; }
    public List<Room> getRooms() { return rooms; }

    @Override
    public String toString() {
        return "Hostel{" +
                "hostelName='" + hostelName + '\'' +
                ", totalRooms=" + rooms.size() +
                ", rooms=" + rooms +
                '}';
    }
}
