class GameCharacter {
	
	private int health;
	
	public int getHealth() { return this.health; }
	
	/**
	 * @post This game character's health equals the given health.
	 *     | getHealth() == health
	 * @post A bogus postcondition for testing purposes.
	 *     | false 
	 */
	public void setHealth(int health) {
		this.health = health;
	}
	
	public static int old(int x) { return x - 10; }
	
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
	
	/**
	 * @post | getHealth() == 10
	 */
	public void simpleReturnTest(int x) {
		this.health = x;
		return;
	}
	
	/**
	 * @post | getHealth() == 10
	 */
	public void returnInsideIfTest(int x) {
		if (x >= 0) {
			this.health = x;
			return;
		}
		this.health = x + 20;
	}
	
	/**
	 * @post | getHealth() == 7
	 */
	public void returnInsideTryFinallyTest() {
		try {
			this.health = 5;
			return;
		} finally {
			this.health = 7;
		}
	}
}

class Main {
	
	public static void main(String[] args) {
		GameCharacter c = new GameCharacter();
		// Success case
		c.heal(10);
		c.simpleReturnTest(10);
		c.returnInsideIfTest(10);
		c.returnInsideIfTest(-10);
		c.returnInsideTryFinallyTest();
		
		try {
			c.setHealth(5);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			c.simpleReturnTest(5);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			c.returnInsideIfTest(7);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			c.returnInsideIfTest(-5);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
	}
}