class Foo {
	public Foo() {}
	boolean isOk() { return true; }
}

public class GameCharacter_pre_type_error {

	private int health;
	
	public int bazz;
	
	private boolean helper() { return true; }

	public int getHealth() { return this.health; }

	/**
	 * Reduces this game character's health by the given amount.
	 * @pre The given amount is nonnegative.
	 *    | amount
	 * @pre | new Foo().isOk()
	 * @pre | health == 0
	 * @pre | this.health == 0
	 * @pre | helper()
	 * @pre | Foo.class.getName() == "Foo"
	 * @pre | (bazz += 1) + (bazz = 1) + (bazz++) == 42
	 */
	public void takeDamage(int amount) {
		this.health -= amount;
	}
}