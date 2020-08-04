package bigteams.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

import bigteams.ProjectCourseStudent;
import bigteams.Team;

class BigTeamsTest {

	@Test
	void test() {
		ProjectCourseStudent student1 = new ProjectCourseStudent();
		ProjectCourseStudent student2 = new ProjectCourseStudent();
		Team team = new Team();
		
		student1.join(team);
		assertEquals(team, student1.getTeam());
		assertEquals(Set.of(student1), team.getMembers());
		
		assertThrows(IllegalArgumentException.class, () -> student2.join(null));
		
		student2.join(team);
		assertEquals(team, student2.getTeam());
		assertEquals(Set.of(student1, student2), team.getMembers());
		
		student1.leaveTeam();
		assertEquals(Set.of(student2), team.getMembers());
		
		student2.leaveTeam();
		assertEquals(Set.of(), team.getMembers());
	}

}
