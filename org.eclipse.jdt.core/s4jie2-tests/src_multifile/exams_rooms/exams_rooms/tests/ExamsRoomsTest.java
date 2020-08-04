package exams_rooms.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

import exams_rooms.Exam;
import exams_rooms.Room;

class ExamsRoomsTest {

	@Test
	void test() {
		// Minimal test script that tests all statements
		Exam exam1 = new Exam();
		assertEquals(Set.of(), exam1.getRooms());

		Room room1 = new Room();
		assertEquals(Set.of(), room1.getExams());
		
		exam1.linkTo(room1);
		assertEquals(Set.of(room1), exam1.getRooms());
		assertEquals(Set.of(exam1), room1.getExams());
		
		exam1.unlinkFrom(room1);
		assertEquals(Set.of(), exam1.getRooms());
		assertEquals(Set.of(), room1.getExams());
		
		// Optional additional test cases to cover more scenarios
		Room room2 = new Room();
		exam1.linkTo(room1);
		exam1.linkTo(room2);
		assertEquals(Set.of(room1, room2), exam1.getRooms());
		assertEquals(Set.of(exam1), room1.getExams());
		assertEquals(Set.of(exam1), room2.getExams());
		
		exam1.unlinkFrom(room1);
		assertEquals(Set.of(room2), exam1.getRooms());
		assertEquals(Set.of(), room1.getExams());
		assertEquals(Set.of(exam1), room2.getExams());
		
		Exam exam2 = new Exam();
		exam2.linkTo(room2);
		assertEquals(Set.of(room2), exam1.getRooms());
		assertEquals(Set.of(room2), exam2.getRooms());
		assertEquals(Set.of(), room1.getExams());
		assertEquals(Set.of(exam1, exam2), room2.getExams());
		
		exam1.unlinkFrom(room2);
		assertEquals(Set.of(), exam1.getRooms());
		assertEquals(Set.of(room2), exam2.getRooms());
		assertEquals(Set.of(), room1.getExams());
		assertEquals(Set.of(exam2), room2.getExams());
	}

}
