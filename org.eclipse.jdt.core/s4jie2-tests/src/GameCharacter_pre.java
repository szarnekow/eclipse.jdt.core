class GameCharacter {
	
	private int health;

	/**
	 * Initializes this game character with the given health.
	 * @param health
	 * @pre The given health is nonnegative.
	 *      0 <= health // TODO: Does not work yet!
	 */
	public GameCharacter(int health) {
		this.health = health;
	}

	public int getHealth() { return this.health; }

	/**
	 * Reduces this game character's health by the given amount.
	 * @pre The given amount is nonnegative.
	 *    | 0 <= amount
	 */
	public void takeDamage(int amount) {
		this.health -= amount;
	}
}

class Main {

	public static void main(String[] args) {
		// Success case
		GameCharacter c = new GameCharacter(5);
		c.takeDamage(3);

		// Failure case
		c.takeDamage(-3);
	}

}