import java.util.ArrayList;

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
	public void takeDamage(int amount, boolean isBroken) {
		if (isBroken)
			this.health += amount;
		else
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
		c.heal(5);
		c.takeDamage(7, false);
		c.simpleReturnTest(10);
		c.returnInsideIfTest(10);
		c.returnInsideIfTest(-10);
		c.returnInsideTryFinallyTest();
		
		try {
			c.takeDamage(7, true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
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

		if (old(5) != -5)
			System.err.println("'old' outside javadoc incorrectly treated as keyword!");
		
		booleanResult(10, false);
		byteResult(2, false);
		charResult('e', false);
		doubleResult(9, false);
		floatResult(10, 2, false);
		intResult(5, 7, false);
		longResult(3, 5, false);
		shortResult(7, false);
		genericResult(false);
		
		try {
			booleanResult(10, true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			byteResult(2, true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			charResult('e', true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			doubleResult(9, true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			floatResult(10, 2, true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			intResult(5, 7, true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			longResult(3, 5, true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			shortResult(7, true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			genericResult(true);
			System.err.println("No exception thrown! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
	}
	
	public static int old(int x) { return x - 10; }
	
	/** @post | result == (x == 10) */
	public static boolean booleanResult(float x, boolean broken) {
		return broken ? x != 10 : x == 10;
	}
	
	/** @post | result == (a | 4) */
	public static byte byteResult(long a, boolean broken) {
		return broken ? (byte)(a & 4) : (byte)(a | 4);
	}
	
	/** @post | 'A' <= result && result <= 'Z' */
	public static char charResult(char c, boolean broken) {
		return broken ? (char)(c - 'A' + 'a') : (char)(c - 'a' + 'A');
	}

	/** @post | result * result == x */
	public static double doubleResult(int x, boolean broken) {
		return broken ? x * x : Math.sqrt(x);
	}
	
	/** @post | result * y == x */
	public static float floatResult(int x, int y, boolean broken) {
		return broken ? (float)x * y : (float)x / y;
	}
	
	/** @post | result == x + y */
	public static int intResult(int x, int y, boolean broken) {
		return broken ? x - y : x + y;
	}
	
	/** @post | result == x * y */
	public static long longResult(long x, long y, boolean broken) {
		return broken ? x / y : x * y;
	}
	
	/** @post | result == -x */
	public static short shortResult(int x, boolean broken) {
		return broken ? (short)x : (short)-x;
	}
	
	/** @post | result.size() == 3 && result.get(0).size() == 0 */
	public static ArrayList<ArrayList<Integer>> genericResult(boolean broken) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<>();
		result.add(new ArrayList<>());
		result.add(new ArrayList<>());
		result.add(new ArrayList<>());
		if (broken)
			result.get(0).add(10);
		return result;
	}

}