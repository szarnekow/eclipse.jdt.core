import java.util.stream.LongStream;

public class MoreMath {

	/**
	 * Returns the absolute value of the given number.
	 * 
	 * @throws ArithmeticException if arithmetic overflow occurs.
	 *    | x == Long.MIN_VALUE
	 * @post The result is nonnegative.
	 *    | 0 <= result
	 * @post The result equals either the argument or its negation.
	 *    | result == x || result == -x
	 */
	public static long absExact(long x) {
		if (x == Long.MIN_VALUE)
			throw new ArithmeticException("Arithmetic overflow");
		return Math.abs(x);
	}

	/**
	 * Returns whether the first given number divides the second given number.
	 * 
	 * @pre The first given number is not zero.
	 *    | a != 0
	 * @post | result == (b % a == 0)
	 */
	public static boolean divides(long a, long b) {
		return b % a == 0;
	}
	
	/**
	 * Returns the greatest common divisor of the two given integers.
	 * 
	 * @pre The given numbers are nonnegative.
	 *    | 0 <= a && 0 <= b
	 * @pre At least one given number is nonzero.
	 *    | a != 0 || b != 0
	 * @post The result is positive.
	 *    | 0 < result
	 * @post The result divides both given numbers.
	 *    | divides(result, a) && divides(result, b)
	 * @post No greater number divides both given numbers.
	 *    | LongStream.range(result, Math.max(a, b)).allMatch(x -> !(divides(x + 1, a) && divides(x + 1, b)))
	 */
	public static long gcd(long a, long b) {
		if (a == 0) return b;
		if (b == 0) return a;
		if (a < b) return gcd(b % a, a);
		return gcd(a % b, b);
	}
	
}
