package exams_rooms;

import java.util.HashSet;
import java.util.Set;

import logicalcollections.LogicalSet;

/**
 * @invar | getRooms() != null
 * @invar | getRooms().stream().allMatch(room -> room != null && room.getExams().contains(this))
 */
public class Exam {

	/**
	 * @invar | rooms != null
	 * @invar | rooms.stream().allMatch(room -> room != null)
	 * @representationObject
	 */
	private Set<Room> rooms = new HashSet<>();
	
	/**
	 * @invar | getRoomsInternal().stream().allMatch(room -> room.getExamsInternal().contains(this))
	 * 
	 * @creates | result
	 * @post | result != null
	 * @post | result.stream().allMatch(room -> room != null)
	 * 
	 * @peerObjects (package-level)
	 */
	Set<Room> getRoomsInternal() {
		return Set.copyOf(rooms);
	}
	
	/**
	 * @creates | result
	 * @peerObjects
	 */
	public Set<Room> getRooms() {
		return Set.copyOf(rooms);
	}
	
	/**
	 * @mutates | this
	 * @post | getRooms().isEmpty()
	 */
	public Exam() {}
	
	/**
	 * @throws IllegalArgumentException | room == null
	 * @mutates_properties | getRooms(), room.getExams()
	 * @post | getRooms().equals(LogicalSet.plus(old(getRooms()), room))
	 * @post | room.getExams().equals(LogicalSet.plus(old(room.getExams()), this))
	 */
	public void linkTo(Room room) {
		if (room == null)
			throw new IllegalArgumentException("room is null");
		rooms.add(room);
		room.addExam(this);
	}
	
	/**
	 * @pre | room != null
	 * @mutates_properties | getRooms(), room.getExams()
	 * @post | getRooms().equals(LogicalSet.minus(old(getRooms()), room))
	 * @post | room.getExams().equals(LogicalSet.minus(old(room.getExams()), this))
	 */
	public void unlinkFrom(Room room) {
		rooms.remove(room);
		room.removeExam(this);
	}
	
}
