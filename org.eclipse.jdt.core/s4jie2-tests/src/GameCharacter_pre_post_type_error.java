class GameCharacter {
	
	private int health;
	
	public int getHealth() { return this.health; }
	
	/**
	 * @post This game character's health equals the given health.
	 *     | getHealth()
	 * @post A bogus postcondition for testing purposes.
	 *     | false 
	 */
	public void setHealth(int health) {
		this.health = health;
	}
	
	/**
	 * Reduces this game character's health by the given amount.
	 * | this is ignored
	 * @pre The given amount is nonnegative.
	 *    | 0 <= amount
	 * @other | this is ignored
	 * @post This game character's health equals its old health minus the given amount of damage.
	 *    | getHealth() ==
	 *    |    old(getHealth()) - amount
	 */
	public void takeDamage(int amount) {
		this.health -= amount;
	}
	
	/**
	 * Increases this game character's health by the given amount.
	 * @pre The given amount is nonnegative.
	 *    | amount >= 0
	 * @post This game character's health equals its old health plus the given amount of damage.
	 *    | old(getHealth()) + amount
	 *    |    == getHealth()
	 */
	public void heal(int amount) {
		this.health += amount;
	}
	
	/** @post  result == (getHealth() * 3 > 0) */
	public boolean isHealthy() { return health > 0; }
	
}

class Main {
	public static void main(String[] args) {
		GameCharacter c = new GameCharacter();
		// Success case
		c.heal(10);
		
		try {
			c.setHealth(5);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
	}
}