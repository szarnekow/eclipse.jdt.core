import java.math.BigInteger;

/**
 * Each instance of this class represents a rational number.
 * 
 * @immutable
 * 
 * @invar The denominator is positive.
 *    | 0 < getDenominator()
 * @invar The numerator is greater than the minimum {@code long} value.
 *    | Long.MIN_VALUE < getNumerator()
 * @invar The fraction is irreducible: the greatest common divisor of
 *        the absolute value of the numerator and the denominator is one.
 *    | MoreMath.gcd(Math.abs(getNumerator()), getDenominator()) == 1 
 */
public class Fraction {
	
	/**
	 * @invar | 0 < denominator
	 * @invar | Long.MIN_VALUE < numerator
	 * @invar | MoreMath.gcd(Math.abs(numerator), denominator) == 1
	 */
	private final long numerator;
	private final long denominator;
	
	public long getNumerator() { return numerator; }
	public long getDenominator() { return denominator; }
	
	private Fraction(long numerator, long denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	/**
	 * An object that represents the number zero.
	 */
	public static final Fraction ZERO = new Fraction(0, 1);
	
	/**
	 * Returns an object representing the rational number defined by the given numerator and denominator.
	 * 
	 * @throws IllegalArgumentException if the given denominator is zero.
	 *    | denominator == 0
	 * @may_throw ArithmeticException if arithmetic overflow occurs.
	 *    | true
	 * @post The result is not null.
	 *    | result != null
	 * @post The rational number represented by the result equals the rational number defined by the
	 *       given numerator and denominator.
	 *    | BigInteger.valueOf(result.getNumerator()).multiply(BigInteger.valueOf(denominator)).equals(
	 *    |     BigInteger.valueOf(numerator).multiply(BigInteger.valueOf(result.getDenominator())))
	 */
	public static Fraction of(long numerator, long denominator) {
		if (denominator == 0)
			throw new IllegalArgumentException("denominator is zero");
		if (numerator == 0)
			return ZERO; 
		long gcd = MoreMath.gcd(
				MoreMath.absExact(numerator),
				MoreMath.absExact(denominator));
		if (denominator < 0)
			gcd = -gcd;
		return new Fraction(numerator / gcd, denominator / gcd);
	}
	
	/**
	 * Returns whether this object and the given object represent the same rational number.
	 *
	 * @throws IllegalArgumentException if {@code other} is null.
	 *    | other == null
	 * @post
	 *    | result == (
	 *    |     getNumerator() == other.getNumerator() &&
	 *    |     getDenominator() == other.getDenominator()
	 *    | )
	 */
	public boolean equals(Fraction other) {
		if (other == null)
			throw new IllegalArgumentException("other is null");
		return numerator == other.numerator && denominator == other.denominator;
	}
	
	/**
	 * Returns an object representing the rational number obtained by adding
	 * the rational number represented by this object to the rational number
	 * represented by the given object.
	 * 
	 * @throws IllegalArgumentException if {@code other} is null.
	 *    | other == null
	 * @may_throw ArithmeticException if arithmetic overflow occurs.
	 *    | true
	 * @post The result is not null.
	 *    | result != null
	 * @post a/b == c/d + e/f if and only if adf == cbf + ebd.
	 *    | BigInteger.valueOf(result.getNumerator()).
	 *    |     multiply(BigInteger.valueOf(this.getDenominator())).
	 *    |     multiply(BigInteger.valueOf(other.getDenominator())).
	 *    |     equals(
	 *    |         BigInteger.valueOf(this.getNumerator()).
	 *    |             multiply(BigInteger.valueOf(result.getDenominator())).
	 *    |             multiply(BigInteger.valueOf(other.getDenominator())).
	 *    |             add(
	 *    |                 BigInteger.valueOf(other.getNumerator()).
	 *    |                     multiply(BigInteger.valueOf(result.getDenominator())).
	 *    |                     multiply(BigInteger.valueOf(this.getDenominator()))))
	 */
	public Fraction plus(Fraction other) {
		if (other == null)
			throw new IllegalArgumentException("other is null");
		long gcd = MoreMath.gcd(this.denominator, other.denominator);
		long numerator = Math.addExact(
				Math.multiplyExact(this.numerator, other.denominator / gcd),
				Math.multiplyExact(other.numerator, this.denominator / gcd));
		long denominator =
				Math.multiplyExact(this.denominator, other.denominator / gcd);
		return Fraction.of(numerator, denominator);
	}
	
}
