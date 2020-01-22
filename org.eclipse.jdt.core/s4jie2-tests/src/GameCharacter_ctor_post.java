class GameObject {
	static int count;
	
	GameObject() {
		count++;
	}
}

class GameCharacter extends GameObject {
	
	static int count;
	
	{
		count++;
	}
	
	private int health;
	
	/**
	 * @post 
	 *     | getHealth() == initialHealth
	 * @post 
	 *     | GameObject.count == old(GameObject.count) + 1 // Tests that old expressions are evaluated before superclass constructor call
	 * @post 
	 *     | GameCharacter.count == old(GameCharacter.count) + 1 // Tests that old expressions are evaluated before instance initializer blocks */
	
	public GameCharacter(int initialHealth, boolean setHealth, boolean explicitReturn, boolean destroyGOcount, boolean destroyGCcount) {
		if (setHealth)
			this.health = initialHealth;
		if (explicitReturn)
			return;
		if (destroyGOcount)
			GameObject.count = -10;
		if (destroyGCcount)
			GameCharacter.count = -100;
	}
	
	public int getHealth() { return this.health; }
	
}

class Main {
	
	public static void main(String[] args) {
		// Success case
		new GameCharacter(100, true, false, false, false);
		new GameCharacter(100, true, true, false, false);
		
		// Implicit return
		try {
			new GameCharacter(100, false, false, false, false);
			System.err.println("No exception was thrown! :-(");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		// Explicit return
		try {
			new GameCharacter(100, false, true, false, false);
			System.err.println("No exception was thrown! :-(");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		// Destroy GameObject.count
		try {
			new GameCharacter(100, true, false, true, false);
			System.err.println("No exception was thrown! :-(");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		// Destroy GameCharacter.count
		try {
			new GameCharacter(100, true, false, false, true);
			System.err.println("No exception was thrown! :-(");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
}