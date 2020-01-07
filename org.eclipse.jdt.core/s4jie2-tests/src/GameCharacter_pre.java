class GameCharacter_pre {
	
	private int health;
	
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