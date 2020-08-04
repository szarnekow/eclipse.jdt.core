package bigteams;

import java.util.HashSet;
import java.util.Set;

import logicalcollections.LogicalSet;

/**
 * Each instance of this class represents a team in a student-team graph.
 * 
 * @invar Each of this team's members has this team as its team.
 *    | getMembers().stream().allMatch(s -> s != null && s.getTeam() == this)
 */
public class Team {

	/**
	 * @invar | members != null
	 * @invar | members.stream().allMatch(s -> s != null)
	 * 
	 * @representationObject
	 */
	private HashSet<ProjectCourseStudent> members = new HashSet<>();
	
	/**
	 * Returns this team's set of members.
	 * 
	 * @invar | getMembersInternal().stream().allMatch(s -> s.getTeamInternal() == this)
	 * 
	 * @post | result != null && result.stream().allMatch(s -> s != null)
	 * @peerObjects
	 */
	Set<ProjectCourseStudent> getMembersInternal() { return Set.copyOf(members); }
	
	/**
	 * Returns this team's set of members.
	 * 
	 * @post | result != null
	 * @creates | result
	 * @peerObjects
	 */
	public Set<ProjectCourseStudent> getMembers() { return Set.copyOf(members); }

	/**
	 * Initializes this object as representing an empty team.
	 * 
	 * @mutates | this
	 * @post This team has no members.
	 *    | getMembers().isEmpty()
	 */
	public Team() {}
	
	/**
	 * Adds the given student to this team's set of students.
	 * 
	 * @throws IllegalArgumentException if {@code student} is null
	 *    | student == null
	 * @mutates | this
	 * @post This team's set of members equals its old set of members plus the given student.
	 *    | getMembersInternal().equals(LogicalSet.plus(old(getMembersInternal()), student))
	 */
	void addMember(ProjectCourseStudent student) {
		if (student == null)
			throw new IllegalArgumentException("student is null");
		
		members.add(student);
	}
	
	/**
	 * Removes the given student from this team's set of students.
	 * 
	 * @throws IllegalArgumentException if {@code student} is null
	 *    | student == null
	 * @mutates | this
	 * @post This team's set of members equals its old set of members minus the given student.
	 *    | getMembersInternal().equals(LogicalSet.minus(old(getMembersInternal()), student))
	 */
	void removeMember(ProjectCourseStudent student) {
		if (student == null)
			throw new IllegalArgumentException("student is null");
		
		members.remove(student);
	}
	
}
