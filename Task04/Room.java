package Task04;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Room Entity - Contained within Hostel, contains Residents.
 * Student: Geetika T | Reg No: 192511469
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private int roomNumber;
    private int capacity;
    private List<Resident> residents;

    public Room(int roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.residents = new ArrayList<>();
    }

    public void addResident(Resident resident) {
        this.residents.add(resident);
    }

    public int getRoomNumber() { return roomNumber; }
    public int getCapacity() { return capacity; }
    public List<Resident> getResidents() { return residents; }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber=" + roomNumber +
                ", capacity=" + capacity +
                ", residentCount=" + residents.size() +
                ", residents=" + residents +
                '}';
    }
}
