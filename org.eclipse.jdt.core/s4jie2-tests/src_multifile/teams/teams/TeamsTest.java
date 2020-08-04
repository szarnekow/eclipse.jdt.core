package teams;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TeamsTest {

	@Test
	void test() {
		StudyProgramme bioInformatics = new StudyProgramme();
		OOPStudent alice = new OOPStudent(bioInformatics);
		OOPStudent bob = new OOPStudent(bioInformatics);
		OOPStudent carol = new OOPStudent(bioInformatics);
		OOPStudent dan = new OOPStudent(bioInformatics);
		OOPStudent eve = new OOPStudent(bioInformatics);
		
		alice.setTeammate(bob);
		carol.setTeammate(dan);
		
		assertEquals(bob, alice.getTeammate());
		assertEquals(alice, bob.getTeammate());
		assertEquals(dan, carol.getTeammate());
		assertEquals(carol, dan.getTeammate());
		assertEquals(null, eve.getTeammate());
	}

}
