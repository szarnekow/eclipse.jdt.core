import java.math.BigInteger;

/**
 * Each instance of this class stores, at each point in time, a rational number.
 * 
 * @invar The denominator is positive.
 *    | 0 < getDenominator()
 * @invar The numerator is greater than the minimum {@code long} value.
 *    | Long.MIN_VALUE < getNumerator()
 * @invar The fraction is irreducible: the greatest common divisor of
 *        the absolute value of the numerator and the denominator is one.
 *    | MoreMath.gcd(Math.abs(getNumerator()), getDenominator()) == 1 
 */
public class FractionContainer {
	
	/**
	 * @invar | 0 < denominator
	 * @invar | Long.MIN_VALUE < numerator
	 * @invar | MoreMath.gcd(Math.abs(numerator), denominator) == 1
	 */
	private long numerator;
	private long denominator;
	
	public long getNumerator() { return numerator; }
	public long getDenominator() { return denominator; }
	
	/**
	 * Returns whether the rational number stored by this object
	 * equals the rational number defined by the given numerator
	 * and denominator.
	 * 
	 * @throws IllegalArgumentException if the given denominator is zero.
	 *    | denominator == 0
	 * @post
	 *    | result ==
	 *    |     BigInteger.valueOf(getNumerator())
	 *    |         .multiply(BigInteger.valueOf(denominator))
	 *    |         .equals(
	 *    |              BigInteger.valueOf(numerator)
	 *    |                  .multiply(BigInteger.valueOf(this.getDenominator())))
	 */
	public boolean equals(long numerator, long denominator) {
		if (denominator == 0)
			throw new IllegalArgumentException("denominator is zero");
		if (denominator % this.denominator != 0)
			return false;
		long factor = denominator / this.denominator;
		if (numerator % factor != 0)
			return false;
		return numerator / factor == this.numerator;
	}
	
	/**
	 * Initializes this object so that it stores the number zero.
	 * @post | getNumerator() == 0
	 */
	public FractionContainer() {
		denominator = 1;
	}
	
	/**
	 * Mutates this object so that it stores the rational number defined
	 * by the given numerator and denominator.
	 * 
	 * @throws IllegalArgumentException if the given denominator is zero.
	 *    | denominator == 0
	 * @may_throw ArithmeticException if arithmetic overflow occurs.
	 *    | true
	 * @post The rational number stored by this object equals the rational number defined by the
	 *       given numerator and denominator.
	 *    | BigInteger.valueOf(getNumerator()).multiply(BigInteger.valueOf(denominator)).equals(
	 *    |     BigInteger.valueOf(numerator).multiply(BigInteger.valueOf(getDenominator())))
	 */
	public void set(long numerator, long denominator) {
		if (denominator == 0)
			throw new IllegalArgumentException("denominator is zero");
		long gcd = MoreMath.gcd(
				MoreMath.absExact(numerator),
				MoreMath.absExact(denominator));
		if (denominator < 0)
			gcd = -gcd;
		this.numerator = numerator / gcd;
		this.denominator = denominator / gcd;
	}
	
	/**
	 * Mutates this object so that it stores the rational number obtained by adding
	 * the old rational number stored by this object to the rational number
	 * defined by the given numerator and denominator.
	 * 
	 * @throws IllegalArgumentException if the given denominator is zero.
	 *    | denominator == 0
	 * @may_throw ArithmeticException if arithmetic overflow occurs.
	 *    | true
	 * @post a/b == c/d + e/f if and only if adf == cbf + ebd.
	 *    | BigInteger.valueOf(getNumerator()).
	 *    |     multiply(BigInteger.valueOf(old(getDenominator()))).
	 *    |     multiply(BigInteger.valueOf(denominator)).
	 *    |     equals(
	 *    |         BigInteger.valueOf(old(getNumerator())).
	 *    |             multiply(BigInteger.valueOf(getDenominator())).
	 *    |             multiply(BigInteger.valueOf(denominator)).
	 *    |             add(
	 *    |                 BigInteger.valueOf(numerator).
	 *    |                     multiply(BigInteger.valueOf(getDenominator())).
	 *    |                     multiply(BigInteger.valueOf(old(getDenominator())))))
	 */
	public void add(long numerator, long denominator) {
		if (denominator == 0)
			throw new IllegalArgumentException("denominator is zero");
		long gcd = MoreMath.gcd(this.denominator, MoreMath.absExact(denominator));
		if (denominator < 0)
			gcd = -gcd;
		long newNumerator = Math.addExact(
				Math.multiplyExact(this.numerator, denominator / gcd),
				Math.multiplyExact(numerator, this.denominator / gcd));
		long newDenominator =
				Math.multiplyExact(this.denominator, denominator / gcd);
		set(newNumerator, newDenominator);
	}
	
}
