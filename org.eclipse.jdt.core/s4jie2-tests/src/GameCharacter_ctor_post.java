class GameCharacter {
	
	private int health;
	
	/**
	 * @post | getHealth() == initialHealth
	 */
	public GameCharacter(int initialHealth, int mode) {
		if (mode == 0)
			this.health = initialHealth;
		if (mode == 1)
			return;
	}
	
	public int getHealth() { return this.health; }
	
}

class Main {
	
	public static void main(String[] args) {
		// Success case
		new GameCharacter(100, 0);
		
		// Implicit return
		try {
			new GameCharacter(100, -1);
			System.err.println("No exception was thrown! :-(");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		// Explicit return
		try {
			new GameCharacter(100, 1);
			System.err.println("No exception was thrown! :-(");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
}