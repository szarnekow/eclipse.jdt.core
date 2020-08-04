package bigteams;

import java.util.HashSet;
import java.util.Set;

/**
 * Each instance of this class represents a team in a student-team graph.
 * 
 * @invar Each of this team's members has this team as its team.
 *    | getMembers().stream().allMatch(s -> s != null && s.getTeam() == this)
 */
public class Team {
	
	/**
	 * @invar | members != null // Phase 1 representation invariant
	 * @invar | members.stream().allMatch(s -> s != null && s.team == this) // Phase 2 representation invariant
	 * 
	 * @representationObject
	 * @peerObjects
	 */
	HashSet<ProjectCourseStudent> members = new HashSet<>();
	
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
	
}
