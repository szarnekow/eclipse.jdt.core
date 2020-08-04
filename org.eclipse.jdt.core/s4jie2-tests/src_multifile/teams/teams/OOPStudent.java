package teams;

/**
 * Each instance of this class represents an Object-Oriented Programming student
 * in an OOP team composition graph.
 * 
 * @invar | getStudyProgramme() != null
 * @invar | getTeammate() == null || getTeammate().getTeammate() == this
 */
public class OOPStudent {

	/**
	 * @invar | studyProgramme != null
	 * @invar | teammate == null || teammate.teammate == this
	 */
    private final StudyProgramme studyProgramme;
    /** @peerObject */
    private OOPStudent teammate;

    /** @immutable */
    public StudyProgramme getStudyProgramme() {
        return studyProgramme;
    }

    /**
     * Returns this student's teammate, or {@code null} if this student has no teammate.
     * 
     * @peerObject
     */
    public OOPStudent getTeammate() {
        return teammate;
    }

    /**
     * Initializes this object to represent a student with the given study programme
     * and no teammate.
     * 
     * @throws IllegalArgumentException if {@code studyProgramme} is null
     *    | studyProgramme == null
     * @post This student's study programme equals the given study programme
     *    | getStudyProgramme() == studyProgramme
     * @post This student has no teammate
     *    | getTeammate() == null
     */
    public OOPStudent(StudyProgramme studyProgramme) {
    	if (studyProgramme == null)
    		throw new IllegalArgumentException("`studyProgramme` is null");
        this.studyProgramme = studyProgramme;
    }

    /**
     * Registers the fact that this student is teaming up with the given student.
     * 
     * @throws IllegalArgumentException if {@code teammate} is null
     *    | teammate == null
     * @throws IllegalStateException if this student already has a teammate
     *    | getTeammate() != null
     * @throws IllegalArgumentException if the given student already has a teammate
     *    | teammate.getTeammate() != null
     * @mutates this, teammate
     * @post This student's teammate equals the given teammate
     *    | getTeammate() == teammate
     * @post The given student's teammate equals this student
     *       (Note: this postcondition is redundant because it follows from the public class invariant.)
     *    | teammate.getTeammate() == this
     */
    public void setTeammate(OOPStudent teammate) {
    	if (teammate == null)
    		throw new IllegalArgumentException("`teammate` is null");
    	if (this.teammate != null)
    		throw new IllegalStateException("This student already has a teammate");
    	if (teammate.teammate != null)
    		throw new IllegalArgumentException("The given teammate already has a teammate");
    	this.teammate = teammate;
    	teammate.teammate = this;
    }
    
    /**
     * Registers the fact that this student is splitting up with their teammate.
     * 
     * @throws IllegalStateException if this student has no teammate
     *    | getTeammate() == null
     * @mutates this
     * @post This student has no teammate
     *    | getTeammate() == null
     * @post This student's old teammate has no teammate
     *    | old(getTeammate()).getTeammate() == null
     */
    public void clearTeammate() {
    	if (teammate != null)
    		throw new IllegalStateException("This student does not have a teammate");
    	this.teammate.teammate = null;
    	this.teammate = null;	
    }
}
